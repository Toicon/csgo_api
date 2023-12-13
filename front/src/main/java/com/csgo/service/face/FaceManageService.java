package com.csgo.service.face;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import com.csgo.config.properties.AliProperties;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.config.ExchangeRatePlus;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.plus.recharge.RechargeChannel;
import com.csgo.domain.plus.recharge.RechargeChannelPriceItem;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.exception.ServiceErrorException;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.ExchangeRateService;
import com.csgo.service.OrderRecordService;
import com.csgo.service.pay.IPaymentService;
import com.csgo.service.recharge.RechargeService;
import com.csgo.service.user.UserService;
import com.csgo.support.BusinessException;
import com.csgo.support.ExceptionCode;
import com.csgo.support.GlobalConstants;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.SignUtil;
import com.csgo.web.request.face.AliFaceCheckResult;
import com.csgo.web.request.face.AliFaceRequest;
import com.csgo.web.request.face.AliRechargeRequest;
import com.csgo.web.response.pay.OrderRecordResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.util.Messages;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 人脸识别管理
 */
@Slf4j
@Service
public class FaceManageService {


    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SiteContext siteContext;
    @Autowired
    private UserService userService;
    @Autowired
    private AliFaceService tencentFaceService;

    @Autowired
    private AliProperties aliProperties;

    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private OrderRecordService orderRecordService;

    /**
     * 检查是否满足人脸识别要求，未完善身份证信息需弹框提示用户完善身份信息
     * <p>
     * 1、累计充值100V币（历史充值+当前充值金额）需要开启实名认证。
     * 2、单笔充值超过300V币，需要开启实名认证
     *
     * @return
     */
    public Map<String, String> checkIdentityInfo(AliRechargeRequest request) {
        Map<String, String> resultMap = new HashMap<>();
        AliFaceRequest aliFaceRequest = new AliFaceRequest();
        Integer userId = siteContext.getCurrentUser().getId();
        UserPlus user = userService.get(userId);
        if (user == null || StringUtils.isEmpty(user.getIdNo()) || StringUtils.isEmpty(user.getRealName())) {
            String userInfoKey = getUserInfoKey(String.valueOf(userId));
            if (!redisTemplate.hasKey(userInfoKey)) {
                //补充身份信息
                resultMap.put("supplyIdentityInfo", YesOrNoEnum.YES.getCode().toString());
                return resultMap;
            } else {
                Map map = (Map) redisTemplate.opsForValue().get(userInfoKey);
                String certNo = String.valueOf(map.get("certNo"));
                String certName = String.valueOf(map.get("certName"));
                aliFaceRequest.setIdNo(certNo);
                aliFaceRequest.setName(certName);
            }
        } else {
            aliFaceRequest.setIdNo(user.getIdNo());
            aliFaceRequest.setName(user.getRealName());
        }
        //判断人脸识别错误是否超过3次
        String errorCount = redisTemplateFacde.get(getErrorFaceUserKey(String.valueOf(userId)));
        if (!StringUtils.isEmpty(errorCount)) {
            if (Integer.valueOf(errorCount) >= 3) {
                throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "很抱歉，您当前的人脸验证已超过三次，账号将被锁定24小时");
            }
        }
        request.setUserId(userId);
        //获取人脸识别地址
        String orderNo = UUID.randomUUID().toString().replace("-", "");
        aliFaceRequest.setOrderNo(orderNo);
        aliFaceRequest.setUserId(String.valueOf(userId));
        String callBackUrl = aliProperties.getFaceCallUrl() + "?orderNo=" + orderNo;
        String imgUrl = tencentFaceService.getH5FaceUrl(aliFaceRequest, callBackUrl);
        request.setCertifyId(aliFaceRequest.getCertifyId());
        resultMap.put("img_url", imgUrl);
        resultMap.put("tencentFace", YesOrNoEnum.YES.getCode().toString());
        redisTemplate.opsForValue().set(getPayInfoKey(orderNo), request, 20, TimeUnit.MINUTES);
        return resultMap;
    }

    /**
     * 检查人脸识别是否通过
     *
     * @param orderNo
     * @return
     */
    public AliFaceCheckResult checkFace(String orderNo) {
        if (StringUtils.isEmpty(orderNo)) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "订单编号不存在！");
        }
        AliRechargeRequest request;
        if (!redisTemplate.hasKey(getPayInfoKey(orderNo))) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "人脸识别已超时，请重新支付");
        } else {
            request = (AliRechargeRequest) redisTemplate.opsForValue().get(getPayInfoKey(orderNo));
        }
        RechargeChannel rechargeChannel = rechargeService.get(request.getChannelId());
        if (rechargeChannel == null) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "充值渠道有误，请联系管理员");
        }
        RechargeChannelPriceItem priceItem = rechargeService.getPriceItem(request.getPriceItemId());
        if (priceItem == null) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "充值面额有误，请联系管理员");
        }
        //获取汇率
        List<ExchangeRatePlus> exchangeRateList = exchangeRateService.findByFlag(1);
        if (CollectionUtils.isEmpty(exchangeRateList)) {
            throw new BusinessException(ExceptionCode.RATE_NOT_EXISTS);
        }
        Integer userId = request.getUserId();
        UserPlus user = userService.get(userId);
        if (user == null) {
            throw new ServiceErrorException("用户信息不存在");
        }
        //判断人脸认证是否通过
        boolean isPass = tencentFaceService.checkFace(request.getCertifyId(), userId);
        if (!isPass) {
            //不通过，删除用户信息缓存
            redisTemplate.delete(getUserInfoKey(String.valueOf(userId)));
            throw new ServiceErrorException("人脸核身验证失败");
        } else {
            //通过，去除识别记录
            redisTemplate.delete(getErrorFaceUserKey(String.valueOf(userId)));
        }
        //人脸通过保存身份证姓名
        Map userInfoMap = (Map) redisTemplate.opsForValue().get(getUserInfoKey(String.valueOf(request.getUserId())));
        if (userInfoMap != null && userInfoMap.size() > 0) {
            String certNo = String.valueOf(userInfoMap.get("certNo"));
            String certName = String.valueOf(userInfoMap.get("certName"));
            user.setIdNo(certNo);
            user.setRealName(certName);
            user.setFirstFaceState(YesOrNoEnum.YES.getCode());
            userService.update(user);
            redisTemplate.delete(getUserInfoKey(String.valueOf(userId)));
        }
        ExchangeRatePlus exchangeRate = exchangeRateList.get(0);
        String rate = exchangeRate.getExchangeRate();
        AliFaceCheckResult aliFaceCheckResult = new AliFaceCheckResult();
        aliFaceCheckResult.setCompanyName(aliProperties.getCompanyName());
        aliFaceCheckResult.setProductName(aliProperties.getProductName());
        aliFaceCheckResult.setPrice(priceItem.getPrice().multiply(BigDecimal.valueOf(Double.parseDouble(rate))));
        aliFaceCheckResult.setOrderNo(orderNo);
        return aliFaceCheckResult;
    }

    /**
     * 重新人脸识别
     *
     * @param oldOrderNo
     * @return
     */
    public Map<String, String> againFace(String oldOrderNo) {
        if (StringUtils.isEmpty(oldOrderNo)) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "订单编号不存在！");
        }
        AliRechargeRequest request;
        if (!redisTemplate.hasKey(getPayInfoKey(oldOrderNo))) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "人脸识别已超时，请重新支付");
        } else {
            request = (AliRechargeRequest) redisTemplate.opsForValue().get(getPayInfoKey(oldOrderNo));
        }
        Map<String, String> resultMap = new HashMap<>();
        AliFaceRequest aliFaceRequest = new AliFaceRequest();
        Integer userId = request.getUserId();
        //判断人脸识别错误是否超过3次
        String errorCount = redisTemplateFacde.get(getErrorFaceUserKey(String.valueOf(userId)));
        if (!StringUtils.isEmpty(errorCount)) {
            if (Integer.valueOf(errorCount) >= 3) {
                throw new ServiceErrorException("很抱歉，您当前的人脸验证已超过三次，账号将被锁定24小时");
            }
        }
        UserPlus user = userService.get(userId);
        if (user == null || StringUtils.isEmpty(user.getIdNo()) || StringUtils.isEmpty(user.getRealName())) {
            String userInfoKey = getUserInfoKey(String.valueOf(userId));
            if (!redisTemplate.hasKey(userInfoKey)) {
                //补充身份信息
                resultMap.put("supplyIdentityInfo", YesOrNoEnum.YES.getCode().toString());
                return resultMap;
            } else {
                Map map = (Map) redisTemplate.opsForValue().get(userInfoKey);
                String certNo = String.valueOf(map.get("certNo"));
                String certName = String.valueOf(map.get("certName"));
                aliFaceRequest.setIdNo(certNo);
                aliFaceRequest.setName(certName);
            }
        } else {
            aliFaceRequest.setIdNo(user.getIdNo());
            aliFaceRequest.setName(user.getRealName());
        }
        //获取人脸识别地址
        //生成新的订单id
        String newOrderNo = UUID.randomUUID().toString().replace("-", "");
        aliFaceRequest.setOrderNo(newOrderNo);
        aliFaceRequest.setUserId(String.valueOf(userId));
        String callBackUrl = aliProperties.getFaceCallUrl() + "?orderNo=" + newOrderNo;
        String imgUrl = tencentFaceService.getH5FaceUrl(aliFaceRequest, callBackUrl);
        request.setCertifyId(aliFaceRequest.getCertifyId());
        resultMap.put("img_url", imgUrl);
        resultMap.put("tencentFace", YesOrNoEnum.YES.getCode().toString());
        redisTemplate.opsForValue().set(getPayInfoKey(newOrderNo), request, 20, TimeUnit.MINUTES);
        redisTemplate.delete(getPayInfoKey(oldOrderNo));
        return resultMap;
    }

    /**
     * 订单支付
     *
     * @param orderNo
     * @param servletRequest
     * @return
     */
    public Map<String, String> pay(String orderNo, HttpServletRequest servletRequest) {
        if (StringUtils.isEmpty(orderNo)) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "订单编号不能为空！");
        }
        AliRechargeRequest request;
        if (!redisTemplate.hasKey(getPayInfoKey(orderNo))) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "支付已超时，请重新支付");
        } else {
            request = (AliRechargeRequest) redisTemplate.opsForValue().get(getPayInfoKey(orderNo));
        }
        RechargeChannel rechargeChannel = rechargeService.get(request.getChannelId());
        if (rechargeChannel == null) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "充值渠道有误，请联系管理员");
        }
        RechargeChannelPriceItem priceItem = rechargeService.getPriceItem(request.getPriceItemId());
        if (priceItem == null) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "充值面额有误，请联系管理员");
        }
        Integer userId = request.getUserId();
        if (org.springframework.util.StringUtils.hasText(redisTemplateFacde.get("RECHARGE" + userId))) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "请勿重复充值操作！");
        }
        Map<String, Object> map = new TreeMap<>();
        map.put("userId", userId);
        map.put("channelId", request.getChannelId());
        map.put("priceItemId", request.getPriceItemId());
        map.put("key", GlobalConstants.SIGN_KEY);
        map.put("isApp", request.getIsApp());
        String stringSign = SignUtil.sign(map);
        if (!stringSign.equals(request.getToken())) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "接口参数验签校验失败");
        }
        redisTemplateFacde.set("RECHARGE" + userId, JSON.toJSONString(map));
        try {
            UserPlus user = userService.get(userId);
            IPaymentService paymentService = rechargeService.getChannelByType(rechargeChannel.getType());
            return paymentService.mobilePay(user, rechargeChannel, priceItem, false, servletRequest);
        } finally {
            redisTemplateFacde.delete("RECHARGE" + userId);
        }
    }

    public OrderRecordResponse queryOrderNum(String orderNo) {
        if (StringUtils.isEmpty(orderNo)) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "订单编号不能为空！");
        }
        AliRechargeRequest request;
        if (!redisTemplate.hasKey(getPayInfoKey(orderNo))) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "支付已超时，请重新支付");
        } else {
            request = (AliRechargeRequest) redisTemplate.opsForValue().get(getPayInfoKey(orderNo));
        }
        OrderRecord orderRecord = orderRecordService.queryOrderNum(orderNo);
        if (!orderRecord.getUserId().equals(request.getUserId())) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "订单有误");
        }
        OrderRecordResponse response = new OrderRecordResponse();
        BeanUtils.copyProperties(orderRecord, response);
        response.setStyle(orderRecord.getStyle().getDescription());
        return response;
    }

    /**
     * 保存用户身份信息
     *
     * @param aliFaceRequest
     */
    public void saveUserIdentityInfo(AliFaceRequest aliFaceRequest) {
        if (StringUtils.isEmpty(aliFaceRequest.getIdNo())) {
            throw new ServiceErrorException("身份证号码不能为空");
        }
        if (StringUtils.isEmpty(aliFaceRequest.getName())) {
            throw new ServiceErrorException("姓名不能为空");
        }
        //判断身份证账号是否绑定三个账号
        this.checkIdNoExit(aliFaceRequest.getIdNo());
        String userId = String.valueOf(siteContext.getCurrentUser().getId());
        aliFaceRequest.setUserId(userId);
        tencentFaceService.checkUserIdentityInfo(aliFaceRequest);
        String redisKey = getUserInfoKey(userId);
        Map<String, String> map = new HashMap<>();
        map.put("certNo", aliFaceRequest.getIdNo());
        map.put("certName", aliFaceRequest.getName());
        redisTemplate.opsForValue().set(redisKey, map, 20, TimeUnit.MINUTES);
    }

    /**
     * 重新保存身份证信息（非登录）
     *
     * @param aliFaceRequest
     */
    public void againSaveUserIdentityInfo(AliFaceRequest aliFaceRequest) {
        if (StringUtils.isEmpty(aliFaceRequest.getIdNo())) {
            throw new ServiceErrorException("身份证号码不能为空");
        }
        if (StringUtils.isEmpty(aliFaceRequest.getName())) {
            throw new ServiceErrorException("姓名不能为空");
        }
        if (StringUtils.isEmpty(aliFaceRequest.getOrderNo())) {
            throw new ServiceErrorException("订单编号不能为空");
        }
        //判断身份证账号是否绑定三个账号
        this.checkIdNoExit(aliFaceRequest.getIdNo());
        AliRechargeRequest request;
        if (!redisTemplate.hasKey(getPayInfoKey(aliFaceRequest.getOrderNo()))) {
            throw new ApiException(StandardExceptionCode.RECHARGE_FAILURE, "人脸识别已超时，请重新支付");
        } else {
            request = (AliRechargeRequest) redisTemplate.opsForValue().get(getPayInfoKey(aliFaceRequest.getOrderNo()));
        }
        String userId = String.valueOf(request.getUserId());
        aliFaceRequest.setUserId(userId);
        tencentFaceService.checkUserIdentityInfo(aliFaceRequest);
        Map<String, String> map = new HashMap<>();
        map.put("certNo", aliFaceRequest.getIdNo());
        map.put("certName", aliFaceRequest.getName());
        redisTemplate.opsForValue().set(getUserInfoKey(userId), map, 20, TimeUnit.MINUTES);
    }

    /**
     * 判断身份证账号是否绑定三个账号
     *
     * @param idNo
     */
    private void checkIdNoExit(String idNo) {
        //判断身份证绑定账号个数，大于等于3个就不让其绑定
        List<UserPlus> list = userService.findByIdNo(idNo);
        if (list != null && list.size() >= 3) {
            throw new ServiceErrorException("身份证号码已经绑定过账号，不能重复绑定");
        }
    }

    /**
     * 用户身份信息的redis key
     *
     * @param userId
     * @return
     */
    private String getUserInfoKey(String userId) {
        return Messages.format("user:info:{}", userId);
    }

    /**
     * 用户支付信息的redis
     *
     * @param userId
     * @return
     */
    private String getPayInfoKey(String userId) {
        return Messages.format("user:order:{}", userId);
    }

    /**
     * 人脸失败用户的redis key
     *
     * @return
     */
    private String getErrorFaceUserKey(String userId) {
        return Messages.format("user:errorFaceUser:{}", userId);
    }

}
