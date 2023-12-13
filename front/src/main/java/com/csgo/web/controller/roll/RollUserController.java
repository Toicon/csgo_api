package com.csgo.web.controller.roll;

import cn.hutool.core.util.StrUtil;
import com.csgo.domain.Roll;
import com.csgo.domain.RollGift;
import com.csgo.domain.RollUser;
import com.csgo.domain.plus.membership.Membership;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.plus.roll.RollPlus;
import com.csgo.domain.plus.roll.RollType;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.RollGiftMapper;
import com.csgo.mapper.RollMapper;
import com.csgo.mapper.RollUserMapper;
import com.csgo.modular.user.logic.UserLogic;
import com.csgo.service.OrderRecordService;
import com.csgo.service.lock.RedissonLockService;
import com.csgo.service.membership.MembershipService;
import com.csgo.service.roll.RollHelp;
import com.csgo.service.roll.RollService;
import com.csgo.service.roll.RollUserService;
import com.csgo.service.user.UserService;
import com.csgo.support.GlobalConstants;
import com.csgo.support.Result;
import com.echo.framework.platform.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Api(tags = "roll房间对应的用户")
@RestController
@RequestMapping("/roll/user")
@Slf4j
@Configuration
public class RollUserController {


    @Autowired
    private RollUserMapper rollUserMapper;

    @Autowired
    private RollMapper rollMapper;
    @Autowired
    private UserService userService;

    @Autowired
    private RollGiftMapper giftMapper;
    @Autowired
    private OrderRecordService orderRecordService;
    @Autowired
    private RollService rollService;
    @Autowired
    private MembershipService membershipService;
    @Autowired
    private RollUserService rollUserService;
    @Autowired
    private RollHelp rollHelp;
    @Autowired
    private UserLogic userLogic;

    @Autowired
    private RedissonLockService redissonLockService;

    /**
     * 新增参与抽奖的用户信息
     *
     * @return
     */
    @ApiOperation("新增参与抽奖的用户信息,(用户参与抽奖的接口信息)")
    @PostMapping("/add/{userId}/{rollId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID"),
            @ApiImplicitParam(name = "rollId", value = "roll房间ID"),
            @ApiImplicitParam(name = "password", value = "roll房间密码"),
            @ApiImplicitParam(name = "email", value = "邮箱")

    })
    public Result add(@PathVariable("userId") int userId,
                      @PathVariable("rollId") int rollId,
                      @RequestParam(value = "email", required = false) String email,
                      String password) {
        if (StringUtils.isEmpty(userId)) {
            return new Result().fairResult("用户id不能为空");
        }
        if (StringUtils.isEmpty(rollId)) {
            return new Result().fairResult("roll福利房间不能为空");
        }

        UserPlus user = userService.get(userId);
        if (user.isFrozen() || user.getIsDelete() == 0) {
            return new Result().fairResult("当前账户异常");
        }

        RollPlus roll = rollService.get(rollId);
        if ("1".equals(roll.getStatus())) {
            return new Result().fairResult("ROLL房时间已过期");
        }

        if (StrUtil.isNotBlank(roll.getPassword()) && !Objects.equals(password, roll.getPassword())) {
            return new Result().fairResult("您输入的密码有误！");
        }

        // 邮箱
        userLogic.checkEmailExist(user);

        List<OrderRecord> orderRecords = orderRecordService.findRollLimitRecharge(userId, roll.getStartTime(), roll.getEndTime(), "2");
        BigDecimal totalAmount = orderRecords.stream().map(OrderRecord::getOrderAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // 内部客户&&不限制，才允许跳过校验
        if (!(GlobalConstants.INTERNAL_USER_FLAG == user.getFlag() && Optional.ofNullable(user.getIsRollNoLimit()).orElse(false))) {
            if (roll.getUserLimit().compareTo(BigDecimal.ZERO) > 0 && totalAmount.compareTo(roll.getUserLimit()) < 0) {
                return new Result().fairResult("您未达到参与ROLL房标准");
            }
            if (roll.getRollType().equals(RollType.PERSONAL) && !roll.getAnchorUserId().equals(user.getParentId())) {
                return new Result().fairResult("您不是该主播的推荐用户");
            }
        }
        if (roll.getMinGrade() != null) {
            Membership membership = membershipService.findByUserId(user.getId());
            if (membership.getGrade() < roll.getMinGrade()) {
                throw new ApiException(HttpStatus.SC_BAD_REQUEST, "您无权限参与，请联系管理员");
            }
        }
        RollUser rollUser = new RollUser();
        String lockKey = "roll:user:" + userId;
        RLock rLock = null;
        try {
            rLock = redissonLockService.acquire(lockKey, 5, TimeUnit.SECONDS);
            if (rLock == null) {
                return new Result().fairResult("不允许重复操作，请10秒后再试");
            }
            RollUser rollUserQuery = new RollUser();
            rollUserQuery.setRollId(rollId);
            rollUserQuery.setUserid(userId);
            List<RollUser> rollUsers = rollUserMapper.selectByRollUserList(rollUserQuery);
            if (!CollectionUtils.isEmpty(rollUsers)) {
                return new Result().fairResult("您已经加入该房间，不允许重复加入");
            }
            rollUser.setRollId(rollId);
            rollUser.setUserid(userId);
            rollUser.setCt(new Date());
            rollUser.setUsername(user.getName());
            rollUser.setFlag("0");
            rollUser.setRollname(roll.getRollName());
            rollUser.setImg(user.getImg());
            rollUserService.add(rollUser);
            rollHelp.joinRoll(rollId);
        } finally {
            redissonLockService.releaseLock(lockKey, rLock);
        }
        return new Result().result(rollUser);
    }


    /**
     * 根据RollId查询到对应的用户信息
     *
     * @return
     */
    @ApiOperation("根据RollId查询到对应的用户中奖信息")
    @GetMapping("queryByRollId/{rollId}/{pageSize}/{pageNum}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rollId", value = "roll房间ID"),
            @ApiImplicitParam(name = "flag", value = "是否中奖0-不是1-中奖"),
            @ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页多少条数据"),
            @ApiImplicitParam(name = "name", value = "模糊搜索字段"),
    })
    public Result queryByRollId(@PathVariable("rollId") Integer rollId,
                                String flag,
                                @PathVariable("pageSize") Integer pageSize,
                                @PathVariable("pageNum") Integer pageNum,
                                String name) {
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = 10;
        }
        if (StringUtils.isEmpty(pageNum)) {
            pageNum = 1;
        }
        pageNum = (pageNum - 1) * pageSize;
        RollUser rollUser = new RollUser();
        if (!StringUtils.isEmpty(flag)) {
            rollUser.setFlag(flag);
        }
        if (!StringUtils.isEmpty(rollId)) {
            rollUser.setRollId(rollId);
        }
        rollUser.setPageNum(pageNum);
        rollUser.setPageSize(pageSize);
        if (name != null && !name.equals("")) {
            rollUser.setUsername(name);
        }
        List<RollUser> list = rollUserMapper.selectByList(rollUser);
        if (list != null && list.size() > 0) {
            rollUser.setPageNum(0);
            rollUser.setPageSize(99999999);
            List<RollUser> rolls2 = rollUserMapper.selectByList(rollUser);
            list.get(0).setTotal(rolls2.size());
        }
        return new Result().result(list);
    }


    /**
     * 根据用户ID查询到对应的房间信息
     *
     * @return
     */
    @ApiOperation("根据用户ID查询到对应的房间信息,")
    @GetMapping("queryByUserId/{userId}/{status}/{pageSize}/{pageNum}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID"),
            @ApiImplicitParam(name = "status", value = "房间状态"),
            @ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页多少条数据"),
            @ApiImplicitParam(name = "name", value = "模糊搜索字段"),
    })
    public Result queryByRoll(@PathVariable("userId") Integer userId,
                              @PathVariable("status") String status,
                              @PathVariable("pageSize") Integer pageSize,
                              @PathVariable("pageNum") Integer pageNum,
                              String name) {
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = 10;
        }
        if (StringUtils.isEmpty(pageNum)) {
            pageNum = 1;
        }
        pageNum = (pageNum - 1) * pageSize;
        if (StringUtils.isEmpty(userId)) {
            return new Result().fairResult("查询的用户不能为空");
        }
        Roll roll = new Roll();
        if (!StringUtils.isEmpty(userId)) {
            roll.setId(userId);
            roll.setPageNum(pageNum);
            roll.setPageSize(pageSize);
            roll.setStatus(status);
            if (name != null && !name.equals("")) {
                roll.setRollname(name);
            }
        }
        List<Roll> list = rollMapper.selectByRollUserIdList(roll);

        if (list != null && list.size() > 0) {

            for (Roll roll1 : list) {
                //根据rollId查询当前房间对应的商品信息
                RollGift rollGift = new RollGift();
                rollGift.setRollid(roll1.getId());
                rollGift.setPageNum(0);
                rollGift.setPageSize(3);
                List<RollGift> selectByList = giftMapper.selectByList(rollGift);
                if (CollectionUtils.isEmpty(selectByList)) {
                    continue;
                }
                rollGift.setPageNum(0);
                rollGift.setPageSize(9999);
                List<RollGift> selectByLists = giftMapper.selectByList(rollGift);
                BigDecimal totalPrice =
                        selectByLists.stream().map(RollGift::getPrice)
                                .reduce(BigDecimal::add).get();
                roll1.setTotalPrice(totalPrice);
                roll1.setProducts(selectByList);
                //根据rollid查询到当前房间参与的人数
                RollUser rollUser = new RollUser();
                rollUser.setRollId(roll1.getId());
                List<RollUser> rollUserList = rollUserMapper.selectByRollUserList(rollUser);
                roll1.setUsernum(rollUserList.size());
            }

            roll.setPageNum(0);
            roll.setPageSize(99999999);
            List<Roll> rolls2 = rollMapper.selectByRollUserIdList(roll);
            list.get(0).setTotal(rolls2.size());
        }
        return new Result().result(list);
    }
}
