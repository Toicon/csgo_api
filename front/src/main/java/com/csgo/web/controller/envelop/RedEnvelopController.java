package com.csgo.web.controller.envelop;

import com.csgo.constants.CommonBizCode;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.envelop.RedEnvelopItem;
import com.csgo.domain.plus.envelop.RedEnvelopItemDTO;
import com.csgo.domain.plus.envelop.RedEnvelopRecord;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.user.User;
import com.csgo.framework.exception.BizClientException;
import com.csgo.service.OrderRecordService;
import com.csgo.service.envelop.RedEnvelopItemService;
import com.csgo.service.envelop.RedEnvelopRecordService;
import com.csgo.service.face.RealNameService;
import com.csgo.service.user.UserService;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.envelop.ReceiveEnvelopRequest;
import com.csgo.web.response.envelop.RedEnvelopItemResponse;
import com.csgo.web.response.envelop.RedEnvelopRecordResponse;
import com.csgo.web.support.SiteContext;
import com.csgo.web.support.UserInfo;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Api
@RequireSession
@RequestMapping("/red/envelop")
@Slf4j
@io.swagger.annotations.Api(tags = "红包领取接口")
public class RedEnvelopController {

    @Autowired
    private SiteContext siteContext;
    @Autowired
    private RedEnvelopItemService redEnvelopItemService;
    @Autowired
    private OrderRecordService orderRecordService;
    @Autowired
    private RedEnvelopRecordService redEnvelopRecordService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private UserService userService;
    @Autowired
    private RealNameService realNameService;

    /**
     * 用户领取红包
     *
     * @return
     */
    @PostMapping(value = "/receive")
    @RequireSession
    @LoginRequired
    public BaseResponse<BigDecimal> receipted(@Valid @RequestBody ReceiveEnvelopRequest request) {
        UserInfo currentUser = siteContext.getCurrentUser();
        if (request.getRedEnvelopId() == null && StringUtils.isEmpty(request.getToken())) {
            throw BizClientException.of(CommonBizCode.RED_ENVELOP_NOT_EXIST);
        }
        RedEnvelopItem item = request.getRedEnvelopId() != null ? redEnvelopItemService.getByEnvelopId(request.getRedEnvelopId()) : redEnvelopItemService.get(request.getToken());
        if (item == null) {
            throw BizClientException.of(CommonBizCode.RED_ENVELOP_NOT_EXIST);
        }
        //活动时间判断
        Date date = new Date();
        if (item.getEffectiveStartTime().after(date)) {
            throw BizClientException.of(CommonBizCode.RED_ENVELOP_NOT_START);
        }
        if (item.getEffectiveEndTime().before(date)) {
            throw BizClientException.of(CommonBizCode.RED_ENVELOP_NOT_END);
        }
        //实名认证判断
        realNameService.checkRealNameVerifyPass(currentUser.getId());
        //充值条件判断
        List<OrderRecord> orderRecords = orderRecordService.findRecharge(currentUser.getId(), item.getEffectiveStartTime(), item.getEffectiveEndTime(), "2");
        BigDecimal rechargeAmount = orderRecords.stream().map(OrderRecord::getOrderAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (item.getLimitAmount().compareTo(rechargeAmount) > 0) {
            log.info("recharge amount:{}", rechargeAmount);
            throw BizClientException.of(CommonBizCode.RED_LIMIT_AMOUNT_NO_ACHIEVE, new Object[]{item.getLimitAmount().subtract(rechargeAmount)});
        }
        String envelopItemId = "envelopItemId:" + item.getId();
        RLock rLock = redissonClient.getLock(envelopItemId);
        BigDecimal amount = BigDecimal.ZERO;
        boolean isLock = false;
        try {
            isLock = rLock.tryLock(5, TimeUnit.SECONDS);
            if (!isLock) {
                throw BizClientException.of(CommonBizCode.COMMON_BUSY);
            }
            //领取条件判断
            List<RedEnvelopRecord> records = redEnvelopRecordService.findReceive(currentUser.getId(), item.getEnvelopId(), item.getEffectiveStartTime());
            if (!CollectionUtils.isEmpty(records)) {
                throw BizClientException.of(CommonBizCode.RED_ENVELOP_RECEIPTED);
            }
            //红包剩余判断
            int receiveCount = redEnvelopRecordService.getReceiveCount(item.getId());
            if (receiveCount >= item.getNum()) {
                throw BizClientException.of(CommonBizCode.RED_ENVELOP_LEAK);
            }
            double minAmount = item.getMinAmount().doubleValue();
            if (item.getMinAmount().compareTo(BigDecimal.ZERO) <= 0) {
                minAmount = 0.01;
            }
            double randomAmount = ThreadLocalRandom.current().nextDouble(minAmount, item.getMaxAmount().doubleValue());
            amount = BigDecimal.valueOf(randomAmount).setScale(2, BigDecimal.ROUND_HALF_EVEN);
            BigDecimal lastAmount = new BigDecimal("0.01");
            if (amount.compareTo(lastAmount) < 0) {
                log.info("red envelop random amount :{}", randomAmount); //金额错误，查看random的值
                log.info("red envelop amount :{}", amount); //金额错误，查看转换的金额值
                amount = lastAmount;
            }
            redEnvelopRecordService.insert(currentUser.getId(), amount, item.getId());
        } catch (InterruptedException ex) {
            throw BizClientException.of(CommonBizCode.COMMON_BUSY);
        } finally {
            if (isLock) {
                rLock.unlock();
            }
        }
        return BaseResponse.<BigDecimal>builder().data(amount).get();
    }

    @GetMapping
    public BaseResponse<List<RedEnvelopItemResponse>> findAll() {
        UserInfo currentUser = siteContext.getCurrentUser();
        List<RedEnvelopItemDTO> redEnvelopItems = redEnvelopItemService.findAll();
        return BaseResponse.<List<RedEnvelopItemResponse>>builder().data(redEnvelopItems.stream().map(redEnvelopItem -> {
            RedEnvelopItemResponse response = new RedEnvelopItemResponse();
            BeanUtilsEx.copyProperties(redEnvelopItem, response);
            List<RedEnvelopRecord> records = redEnvelopRecordService.find(null, redEnvelopItem.getId(), redEnvelopItem.getEffectiveStartTime());
            int surplus = redEnvelopItem.getNum() - records.size();
            response.setNum(surplus);
            if (redEnvelopItem.getLimitAmount() != null) {
                if (currentUser != null) {
                    List<OrderRecord> orderRecord = orderRecordService.findRedRecharge(currentUser.getId(), redEnvelopItem.getEffectiveStartTime(), redEnvelopItem.getEffectiveEndTime(), "2");
                    BigDecimal rechargeAmount = orderRecord.stream().map(OrderRecord::getOrderAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal enoughAmount = redEnvelopItem.getLimitAmount().subtract(rechargeAmount);
                    response.setEnoughAmount(enoughAmount.compareTo(BigDecimal.ZERO) >= 0 ? enoughAmount : BigDecimal.ZERO);
                } else {
                    response.setEnoughAmount(redEnvelopItem.getLimitAmount());
                }
            }
            if (!redEnvelopItem.isShowNum()) {
                response.setShowMsg(surplus > 100 ? "数量充足" : "即将领完");
            }
            //判断领取状态
            if (currentUser != null) {
                //活动时间判断
                Date date = new Date();
                if (redEnvelopItem.getEffectiveStartTime().after(date)) {
                    return response;
                }
                if (redEnvelopItem.getEffectiveEndTime().before(date)) {
                    return response;
                }
                //充值条件判断
                List<OrderRecord> orderRecords = orderRecordService.findRecharge(currentUser.getId(), redEnvelopItem.getEffectiveStartTime(), redEnvelopItem.getEffectiveEndTime(), "2");
                BigDecimal rechargeAmount = orderRecords.stream().map(OrderRecord::getOrderAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (redEnvelopItem.getLimitAmount().compareTo(rechargeAmount) > 0) {
                    return response;
                }
                //领取条件判断
                List<RedEnvelopRecord> receiveRecords = redEnvelopRecordService.findReceive(currentUser.getId(), redEnvelopItem.getEnvelopId(), redEnvelopItem.getEffectiveStartTime());
                if (!CollectionUtils.isEmpty(receiveRecords)) {
                    response.setReceiveStatus(YesOrNoEnum.YES.getCode());
                } else {
                    response.setReceiveStatus(YesOrNoEnum.NO.getCode());
                }
            }
            return response;
        }).collect(Collectors.toList())).get();
    }

    @GetMapping("/record")
    public BaseResponse<List<RedEnvelopRecordResponse>> redEnvelopRecord() {
        List<RedEnvelopRecord> records = redEnvelopRecordService.findRecord(DateUtils.addDays(new Date(), -1));
        return BaseResponse.<List<RedEnvelopRecordResponse>>builder().data(records.stream().map(record -> {
            RedEnvelopRecordResponse response = new RedEnvelopRecordResponse();
            BeanUtilsEx.copyProperties(record, response);
            User user = userService.queryUserId(record.getUserId());
            if (null != user) {
                String userName = user.getName();
                if (userName.length() > 2) {
                    userName = userName.substring(0, 2) + "***";
                }
                response.setUserName(userName);
            }
            return response;
        }).collect(Collectors.toList())).get();
    }

    /**
     * 是否弹出红包列表对话框
     *
     * @return
     */
    @LoginRequired
    @GetMapping("/popUp")
    public BaseResponse<Boolean> popUp() {
        boolean isPopUp = false;
        UserInfo currentUser = siteContext.getCurrentUser();
        List<RedEnvelopItemDTO> items = redEnvelopItemService.findAll();
        if (!CollectionUtils.isEmpty(items)) {
            for (RedEnvelopItemDTO item : items) {
                //活动时间判断
                Date date = new Date();
                if (item.getEffectiveStartTime().after(date)) {
                    continue;
                }
                if (item.getEffectiveEndTime().before(date)) {
                    continue;
                }
                //充值条件判断
                List<OrderRecord> orderRecords = orderRecordService.findRecharge(currentUser.getId(), item.getEffectiveStartTime(), item.getEffectiveEndTime(), "2");
                BigDecimal rechargeAmount = orderRecords.stream().map(OrderRecord::getOrderAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (item.getLimitAmount().compareTo(rechargeAmount) > 0) {
                    continue;
                }
                //领取条件判断
                List<RedEnvelopRecord> records = redEnvelopRecordService.findReceive(currentUser.getId(), item.getEnvelopId(), item.getEffectiveStartTime());
                if (!CollectionUtils.isEmpty(records)) {
                    continue;
                }
                //红包剩余判断
                List<RedEnvelopRecord> redEnvelopRecords = redEnvelopRecordService.find(null, item.getId(), null);
                if (redEnvelopRecords != null && redEnvelopRecords.size() >= item.getNum()) {
                    continue;
                }
                isPopUp = true;
                break;
            }
        }
        return BaseResponse.<Boolean>builder().data(isPopUp).get();
    }
}
