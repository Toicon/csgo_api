package com.csgo.web.controller.membership;

import com.csgo.domain.plus.envelop.RedEnvelop;
import com.csgo.domain.plus.envelop.RedEnvelopItem;
import com.csgo.domain.plus.envelop.RedEnvelopRecord;
import com.csgo.domain.plus.gift.GiftPlus;
import com.csgo.domain.plus.membership.Membership;
import com.csgo.domain.plus.membership.MembershipLevelConfig;
import com.csgo.domain.plus.membership.MembershipTaskRecord;
import com.csgo.domain.plus.membership.MembershipTaskRule;
import com.csgo.domain.plus.membership.MembershipTaskStatus;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.envelop.RedEnvelopItemService;
import com.csgo.service.envelop.RedEnvelopRecordService;
import com.csgo.service.envelop.RedEnvelopService;
import com.csgo.service.gift.GiftService;
import com.csgo.service.membership.MembershipLevelConfigService;
import com.csgo.service.membership.MembershipService;
import com.csgo.service.membership.MembershipTaskRecordService;
import com.csgo.support.BusinessException;
import com.csgo.support.ExceptionCode;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.BeanUtilsEx;
import com.csgo.util.DateUtils;
import com.csgo.util.DateUtilsEx;
import com.csgo.util.IdCardUtil;
import com.csgo.web.controller.membership.support.MembershipDataConverter;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.membership.EditMembershipImgRequest;
import com.csgo.web.request.membership.EditMembershipRequest;
import com.csgo.web.response.membership.MembershipDetailResponse;
import com.csgo.web.response.membership.MembershipRedEnvelopStatus;
import com.csgo.web.response.membership.MembershipTaskResponse;
import com.csgo.web.support.SiteContext;
import com.csgo.web.support.UserInfo;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api
@io.swagger.annotations.Api(tags = "VIP等级接口")
@RequestMapping("/membership")
@LoginRequired
@RequireSession
@Slf4j
public class MembershipController {
//    @Autowired
//    private SiteContext siteContext;
//
//    @Autowired
//    private MembershipService membershipService;
//    @Autowired
//    private MembershipLevelConfigService membershipLevelConfigService;
//    @Autowired
//    private RedEnvelopService redEnvelopService;
//    @Autowired
//    private RedEnvelopRecordService redEnvelopRecordService;
//    @Autowired
//    private RedEnvelopItemService redEnvelopItemService;
//    @Autowired
//    private MembershipDataConverter dataConverter;
//    @Autowired
//    private MembershipTaskRecordService membershipTaskRecordService;
//    @Autowired
//    private RedisTemplateFacde redisTemplate;
//    @Autowired
//    private GiftService giftService;
//
//    @PostMapping("/birthday")
//    @ApiOperation(value = "生日登记接口", notes = "生日登记接口")
//    public BaseResponse<Void> birthday(@Valid @RequestBody EditMembershipRequest request) {
//        UserInfo currentUser = siteContext.getCurrentUser();
//        if (request.getRealName().length() < 2 || request.getRealName().length() > 4) {
//            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "名字输入错误，请重新输入");
//        }
//        if (!IdCardUtil.isIDNumber(request.getIdCard())) {
//            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "身份证号码输入错误，请重新输入");
//        }
//        Date birthDate = DateUtils.stringConnectionToDate(request.getIdCard().substring(6, 14));
//        membershipService.update(currentUser.getId(), request.getIdCard(), request.getRealName(), birthDate);
//        return BaseResponse.<Void>builder().get();
//    }
//
//    /**
//     * 获取VIP任务
//     *
//     * @return
//     */
//    @ApiOperation(value = "获取VIP积分任务接口", notes = "获取VIP积分任务接口")
//    @GetMapping("/task/info")
//    public BaseResponse<MembershipTaskResponse> taskInfo() {
//        int userId = siteContext.getCurrentUser().getId();
////        membershipTaskRecordService.addRecord(MembershipTaskRule.DAY_SIGN, userId, 1);
//        MembershipTaskResponse response = new MembershipTaskResponse();
//
//        Membership membership = membershipService.findByUserId(userId);
//        response.setCurrentGrowth(membership.getGrowth());
//        MembershipLevelConfig config = membershipLevelConfigService.nextLevel(membership.getGrade());
//        if (null == config) {
//            config = membershipLevelConfigService.nextLevel(membership.getGrade() - 1);
//        }
//        response.setLevelLimit(config.getLevelLimit());
//
//        response.setTaskInfoList(dataConverter.to(membershipTaskRecordService.findTaskRecord(userId)));
//        response.setBoxList(dataConverter.to(membership.getGrade(), userId));
//
//        return BaseResponse.<MembershipTaskResponse>builder().data(response).get();
//    }
//
//    @ApiOperation(value = "领取任务积分接口", notes = "领取任务积分接口")
//    @PutMapping("/task/receive/{id}")
//    public BaseResponse<Void> receiveTasks(@PathVariable("id") int id) {
//        int userId = siteContext.getCurrentUser().getId();
//        if (!redisTemplate.tryLock("membership-" + userId + "-receive-" + id, 60)) {
//            throw new BusinessException(ExceptionCode.MEMBERSHIP_RECEIVE_ERROR);
//        }
//        MembershipTaskRecord record = membershipTaskRecordService.getMembershipTaskRecord(id);
//        if (!MembershipTaskStatus.STANDARD.equals(record.getRecordStatus()) || record.getUserId() != userId) {
//            redisTemplate.delete("membership-" + userId + "-receive-" + id);
//            throw new BusinessException(ExceptionCode.MEMBERSHIP_TASK_ERROR);
//        }
//        if (MembershipTaskRule.getTodayRules().contains(record.getRuleType()) && record.getCreateDate().before(DateUtilsEx.toDayStart(new Date()))) {
//            redisTemplate.delete("membership-" + userId + "-receive-" + id);
//            throw new BusinessException(ExceptionCode.MEMBERSHIP_TASK_ERROR);
//        }
//        membershipTaskRecordService.update(record);
//        redisTemplate.delete("membership-" + userId + "-receive-" + id);
//        return BaseResponse.<Void>builder().get();
//    }
//
//    /**
//     * 获取VIP信息
//     *
//     * @return
//     */
//    @GetMapping("/info")
//    @ApiOperation(value = "获取等级介绍接口", notes = "获取等级介绍接口")
//    public BaseResponse<List<MembershipDetailResponse>> info() {
//        UserInfo currentUser = siteContext.getCurrentUser();
//        Membership membership = membershipService.findByUserId(currentUser.getId());
//        List<MembershipLevelConfig> configList = membershipLevelConfigService.list();
//        if (CollectionUtils.isEmpty(configList)) {
//            return BaseResponse.<List<MembershipDetailResponse>>builder().data(new ArrayList<>()).get();
//        }
//        List<GiftPlus> gifts = giftService.findLeGrade(Integer.MAX_VALUE);
//        Map<Integer, GiftPlus> giftPlusMap = gifts.stream().collect(Collectors.toMap(GiftPlus::getMembershipGrade, giftPlus -> giftPlus));
//        return BaseResponse.<List<MembershipDetailResponse>>builder().data(configList.stream().map(config -> {
//            MembershipDetailResponse detailResponse = new MembershipDetailResponse();
//            BeanUtilsEx.copyProperties(config, detailResponse);
//
//            RedEnvelop redEnvelop = redEnvelopService.findByGrade(config.getLevel());
//            if (null != redEnvelop) {
//                detailResponse.setRedAmount(redEnvelop.getMinAmount());
//                RedEnvelopItem item = redEnvelopItemService.getByEnvelopId(redEnvelop.getId());
//                if (CollectionUtils.isEmpty(redEnvelopRecordService.find(currentUser.getId(), item.getId(), item.getEffectiveStartTime()))) {
//                    detailResponse.setStatus(membership.getGrade() >= config.getLevel() ? MembershipRedEnvelopStatus.ACCEPTABLE : MembershipRedEnvelopStatus.UNRECEIVED);
//                } else {
//                    detailResponse.setStatus(MembershipRedEnvelopStatus.RECEIPTED);
//                }
//            }
//            if (giftPlusMap.containsKey(config.getLevel())) {
//                detailResponse.setBoxImg(giftPlusMap.get(config.getLevel()).getImg());
//            }
//
//            return detailResponse;
//        }).collect(Collectors.toList())).get();
//    }
//
//    /**
//     * 获取会员头像列表
//     *
//     * @return
//     */
//    @GetMapping("/img")
//    @ApiOperation(value = "获取会员头像接口", notes = "获取会员头像接口")
//    public BaseResponse<List<String>> img() {
//        Integer userId = siteContext.getCurrentUser().getId();
//        Membership membership = membershipService.findByUserId(userId);
//        List<MembershipLevelConfig> configList = membershipLevelConfigService.findLeLevel(membership.getGrade());
//        return BaseResponse.<List<String>>builder().data(configList.stream().map(MembershipLevelConfig::getImg).collect(Collectors.toList())).get();
//    }
//
//    /**
//     * 更换会员头像
//     *
//     * @return
//     */
//    @PutMapping("/change/img")
//    @ApiOperation(value = "更换会员头像接口", notes = "更换会员头像接口")
//    public BaseResponse<Void> change(@Valid @RequestBody EditMembershipImgRequest request) {
//        Integer userId = siteContext.getCurrentUser().getId();
//        Membership membership = membershipService.findByUserId(userId);
//        List<MembershipLevelConfig> configList = membershipLevelConfigService.findLeLevel(membership.getGrade());
//        List<String> imgList = configList.stream().map(MembershipLevelConfig::getImg).collect(Collectors.toList());
//        if (!imgList.contains(request.getImg())) {
//            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "当前头像框未配置，请重新设置");
//        }
//        membership.setImg(request.getImg());
//        membershipService.updateImg(membership);
//        return BaseResponse.<Void>builder().get();
//    }
//
//    /**
//     * 会员领取红包
//     *
//     * @return
//     */
//    @PostMapping(value = "/grade/receive/{grade}")
//    @ApiOperation(value = "VIP领取红包接口", notes = "VIP领取红包接口")
//    public BaseResponse<BigDecimal> receipted(@PathVariable("grade") int grade) {
//        UserInfo currentUser = siteContext.getCurrentUser();
//        Membership membership = membershipService.findByUserId(currentUser.getId());
//
//        if (membership.getGrade() < grade) {
//            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "当前等级不可领取该红包");
//        }
//
//        RedEnvelop redEnvelop = redEnvelopService.findByGrade(grade);
//        if (null == redEnvelop) {
//            throw new ApiException(StandardExceptionCode.RED_ENVELOP_NOT_EXIST, "红包不存在");
//        }
//
//        RedEnvelopItem item = redEnvelopItemService.getByEnvelopId(redEnvelop.getId());
//
//
//        if (!redisTemplate.tryLock("userId:" + currentUser.getId() + "redId:" + item.getId(), 60)) {
//            throw new ApiException(StandardExceptionCode.RED_ENVELOP_NOT_EXIST, "操作处理中，请1分钟后再试");
//        }
//
//        //活动时间判断
//        Date date = new Date();
//        if (item.getEffectiveStartTime().after(date)) {
//            throw new ApiException(StandardExceptionCode.RED_ENVELOP_NOT_EXIST, "当前活动红包未开始");
//        }
//        if (item.getEffectiveEndTime().before(date)) {
//            throw new ApiException(StandardExceptionCode.RED_ENVELOP_NOT_EXIST, "当前活动红包已结束");
//        }
//
//        //领取条件判断
//        List<RedEnvelopRecord> records = redEnvelopRecordService.findReceive(currentUser.getId(), item.getEnvelopId(), item.getEffectiveStartTime());
//        if (!CollectionUtils.isEmpty(records)) {
//            throw new ApiException(StandardExceptionCode.RED_ENVELOP_RECEIPTED, "您已领取过红包了");
//        }
//
//        BigDecimal amount = item.getMinAmount();
//        BigDecimal lastAmount = new BigDecimal("0.01");
//        if (amount.compareTo(lastAmount) < 0) {
//            log.info("red envelop amount :{}", amount); //金额错误，查看转换的金额值
//            amount = lastAmount;
//        }
//        redEnvelopRecordService.insert(currentUser.getId(), amount, item.getId());
//        redisTemplate.delete("userId:" + currentUser.getId() + "redId:" + item.getId());
//        return BaseResponse.<BigDecimal>builder().data(amount).get();
//    }
}
