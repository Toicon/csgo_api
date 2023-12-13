package com.csgo.web.controller.user;

import com.csgo.domain.CodeInfo;
import com.csgo.domain.user.User;
import com.csgo.domain.user.UserMessage;
import com.csgo.domain.user.UserMessageGift;
import com.csgo.mapper.CodeInfoMapper;
import com.csgo.mapper.UserMessageGiftMapper;
import com.csgo.mapper.UserMessageMapper;
import com.csgo.service.UserService;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Api(tags = "商品出售的信息")
@RestController
@RequestMapping("/user/message/gift")
@Slf4j
public class AdminFrontUserMessageGiftController extends BackOfficeController {

    @Autowired
    private UserMessageMapper messageSer;
    @Autowired
    private UserMessageGiftMapper messageMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CodeInfoMapper codeInfoMapper;

    /**
     * 根据用户id查询所有对应的出售记录
     *
     * @return
     */
    @ApiOperation("根据用户id查询所有对应的出售记录（后台管理）")
    @RequestMapping(value = "queryAll", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid", value = "用户id"),
            @ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页有几条数据"),
            @ApiImplicitParam(name = "start_time", value = "开始时间"),
            @ApiImplicitParam(name = "end_time", value = "结束时间"),
    })
    @Log(desc = "查询用户背包出售道具列表")
    public Result queryAll(Long start_time, Long end_time, Integer userid, Integer pageNum, Integer pageSize) {
        if (userid == null || userid == 0) {
            return new Result().fairResult("userId不能为空");
        }
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (pageNum == null || pageNum == 0) {
            pageNum = 1;
        }

        int p = pageNum;
        pageNum = (pageNum - 1) * pageSize;

        UserMessageGift ug = new UserMessageGift();
        ug.setPageNum(pageNum);
        ug.setPageSize(pageSize);
        ug.setState(1);
        ug.setUserId(userid);
        if (start_time != null && start_time != 0) {
            ug.setSt_time(new Date(start_time));
        }
        if (end_time != null && end_time != 0) {
            ug.setEd_time(new Date(end_time));
        }
        List<UserMessageGift> list = messageMapper.selectList(ug);
        if (!CollectionUtils.isEmpty(list)) {
            for (UserMessageGift gg : list) {
                User user = userService.queryUserId(gg.getUserId());
                if (user != null && user.getSteam() != null) {
                    gg.setSteam(user.getSteam());
                }
            }
            list.get(0).setTotal(messageMapper.selectListBys(ug).size());
        }
        return new Result().result(list);
    }


    /**
     * 查询所有对应的提取记录
     *
     * @return
     */
    @ApiOperation("查询所有对应的提取记录（后台管理）")
    @RequestMapping(value = "queryAllAllextract", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页有几条数据"),
            @ApiImplicitParam(name = "name", value = "提取物品名称或者用户账号"),
            @ApiImplicitParam(name = "start_time", value = "开始时间"),
            @ApiImplicitParam(name = "end_time", value = "结束时间"),
    })
    @Log(desc = "查询用户背包出售道具提取记录")
    public Result queryAllAllextract(Long start_time, Long end_time, Integer pageNum, Integer pageSize,
                                     String name) {
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (pageNum == null || pageNum == 0) {
            pageNum = 1;
        }

        int p = pageNum;
        pageNum = (pageNum - 1) * pageSize;

        UserMessageGift ug = new UserMessageGift();
        ug.setPageNum(pageNum);
        ug.setPageSize(pageSize);
        if (name != null && !("").equals(name)) {
            ug.setGiftProductName(name);
        }
        if (start_time != null && start_time != 0) {
            ug.setSt_time(new Date(start_time));
        }
        if (end_time != null && end_time != 0) {
            ug.setEd_time(new Date(end_time));
        }
        List<UserMessageGift> list = messageMapper.selectListTq(ug);
        if (!CollectionUtils.isEmpty(list)) {
            for (UserMessageGift gg : list) {
                User user = userService.queryUserId(gg.getUserId());
                if (user != null && user.getSteam() != null) {
                    gg.setSteam(user.getSteam());
                    gg.setFlag(user.getFlag() + "");
                }
            }
            list.get(0).setTotal(messageMapper.selectListTqWfy(ug).size());
        }

        return new Result().result(list);
    }


    /**
     * 根据用户id查询所有对应的记录
     *
     * @return
     */
    @ApiOperation("根据用户id查询所有对应的记录（后台管理）")
    @RequestMapping(value = "queryAllAll", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid", value = "用户id"),
            @ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页有几条数据"),
            @ApiImplicitParam(name = "start_time", value = "开始时间"),
            @ApiImplicitParam(name = "end_time", value = "结束时间"),
    })
    public Result queryAllAll(Long start_time, Long end_time, Integer userid, Integer pageNum, Integer pageSize) {
        if (userid == null || userid == 0) {
            return new Result().fairResult("userId不能为空");
        }
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (pageNum == null || pageNum == 0) {
            pageNum = 1;
        }

        int p = pageNum;
        pageNum = (pageNum - 1) * pageSize;

        UserMessageGift ug = new UserMessageGift();
        ug.setPageNum(pageNum);
        ug.setPageSize(pageSize);
        ug.setUserId(userid);
        if (start_time != null && start_time != 0) {
            ug.setSt_time(new Date(start_time));
        }
        if (end_time != null && end_time != 0) {
            ug.setEd_time(new Date(end_time));
        }
        List<UserMessageGift> list = messageMapper.selectList(ug);
        if (!CollectionUtils.isEmpty(list)) {
            list.get(0).setTotal(messageMapper.selectListBys(ug).size());
        }

        return new Result().result(list);
    }

    /**
     * 修改状态
     *
     * @return
     */
    @ApiOperation("修改状态（后台）")
    @RequestMapping(value = "updateState", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "记录ID"),
            @ApiImplicitParam(name = "state", value = "1入库 2 已提取 4 已出售  ")
    })
    @Log(desc = "修改用户背包出售道具状态")
    public Result updateState(Integer id, Integer state) {
        if (id == null || id.equals("")) {
            return new Result().fairResult("记录ID不能为空");
        }
        if (state == null || state == 0) {
            return new Result().fairResult("state不能为空");
        }
        UserMessageGift userMessageGift = messageMapper.selectById(id);
        if (userMessageGift != null) {
            if (state == 2) {
                userMessageGift.setState(2);
                User user = userService.queryUserId(userMessageGift.getUserId());
                if (user == null) {
                    return new Result().fairResult("用户信息不能为空");
                }

                //用户总的提取金额
                user.setExtract(user.getExtract().add(userMessageGift.getMoney()));
                userService.update(user, user.getId());

                if (user.getExtensionCode() != null) {
                    CodeInfo co = new CodeInfo();
                    co.setCode(user.getExtensionCode());
                    List<CodeInfo> liss = codeInfoMapper.getList(co);
                    if (liss != null && liss.size() > 0) {
                        co = liss.get(0);
                        co.setExtract(co.getExtract().add(userMessageGift.getMoney()));
                        codeInfoMapper.updateByPrimaryKeySelective(co);
                    }
                }

            } else if (state == 4) {
                userMessageGift.setState(1);
                userMessageGift.setSellMoney(new BigDecimal(0));
                UserMessage record = new UserMessage();
                record.setId(userMessageGift.getUserMessageId());
                UserMessage um = messageSer.selectOne(record);
                um.setState("1");
                messageSer.updateByPrimaryKeySelective(um);
            } else if (state == 1) {
                userMessageGift.setState(0);
                UserMessage record = new UserMessage();
                record.setId(userMessageGift.getUserMessageId());
                UserMessage um = messageSer.selectOne(record);
                um.setState("0");
                messageSer.updateByPrimaryKeySelective(um);
            }
        } else {
            return new Result().fairResult("修改状态失败！");
        }
        //}
        return new Result().result("修改状态成功！");
        //return new Result().fairResult("因平台限制，请您先使用人工通道。");
    }


}
