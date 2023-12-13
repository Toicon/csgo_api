package com.csgo.service.face;

import cn.hutool.core.util.IdcardUtil;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserCertdocCertverifyConsultRequest;
import com.alipay.api.request.AlipayUserCertdocCertverifyPreconsultRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserCertdocCertverifyConsultResponse;
import com.alipay.api.response.AlipayUserCertdocCertverifyPreconsultResponse;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.csgo.config.properties.AliProperties;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.user.UserPlatformRewardRecordDO;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.exception.ServiceErrorException;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.user.UserPlatformRewardRecordService;
import com.csgo.service.user.UserService;
import com.csgo.support.GlobalConstants;
import com.csgo.util.IdCardUtil;
import com.csgo.util.StringUtil;
import com.csgo.web.request.face.AliRealNameAuthCheckRequest;
import com.csgo.web.request.face.AliRealNameAuthInfo;
import com.csgo.web.request.face.AliRealNameAuthRequest;
import com.csgo.web.request.face.AliRealNameCheckResponse;
import com.echo.framework.util.Messages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 阿里人脸识别
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliRealNameAuthService {
    @Autowired
    private AliProperties aliProperties;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    //支付宝人脸识别网关地址
    private final static String ALI_GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
    //人脸识别三次过期1天
    private static final Long VERIFY_EXPIRE_IN_SECOND = 60 * 60 * 2L;
    @Autowired
    private UserService userService;

    private final RealNameService realNameService;
    private final UserPlatformRewardRecordService userPlatformRewardRecordService;

    /**
     * 实名认证初始化
     *
     * @param aliRealNameAuthRequest
     * @return
     */
    private String init(AliRealNameAuthRequest aliRealNameAuthRequest) {
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(ALI_GATEWAY_URL, aliProperties.getAppId(),
                    aliProperties.getPrivateKey(), "json", "UTF-8", aliProperties.getAliPayPublicKey(), "RSA2");
            AlipayUserCertdocCertverifyPreconsultRequest request = new AlipayUserCertdocCertverifyPreconsultRequest();
            JSONObject identityParam = new JSONObject();
            identityParam.put("cert_type", "IDENTITY_CARD");
            identityParam.put("user_name", aliRealNameAuthRequest.getName());
            identityParam.put("cert_no", aliRealNameAuthRequest.getIdNo());
            request.setBizContent(identityParam.toString());
            AlipayUserCertdocCertverifyPreconsultResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                return response.getVerifyId();
            } else {
                throw new ServiceErrorException(response.getMsg());
            }
        } catch (Exception e) {
            log.error("实名认证初始化失败:{}", e);
            throw new ServiceErrorException("实名认证初始化失败:" + e.getMessage());
        }
    }

    /**
     * 获取实名认证地址
     *
     * @param aliRealNameAuthRequest
     * @return
     */
    public String getH5Url(AliRealNameAuthRequest aliRealNameAuthRequest, int flag) {
        if (GlobalConstants.INTERNAL_USER_FLAG == flag) {
            throw new ServiceErrorException("用户已经实名认证通过，不能重复认证");
        }
        if (aliRealNameAuthRequest.getIdNo().length() != 18) {
            throw new ServiceErrorException("身份证长度不正确，长度应为18位");
        }
        aliRealNameAuthRequest.setIdNo(aliRealNameAuthRequest.getIdNo().toUpperCase());
        boolean checkIdNum = IdCardUtil.isIDNumber(aliRealNameAuthRequest.getIdNo());
        if (!checkIdNum) {
            throw new ServiceErrorException("身份证格式不正确");
        }
        int age = IdcardUtil.getAgeByIdCard(aliRealNameAuthRequest.getIdNo(), new Date());
        if (age < 18) {
            throw new ServiceErrorException("身份证未满18周岁，无法实名认证");
        }
        UserPlus user = userService.get(aliRealNameAuthRequest.getUserId());
        if (user != null && YesOrNoEnum.YES.getCode().equals(user.getRealNameState())) {
            throw new ServiceErrorException("用户已经实名认证通过，不能重复认证");
        }
        this.checkIdNoExit(aliRealNameAuthRequest.getIdNo());
        //人脸核验初始化信息
        String verifyId = init(aliRealNameAuthRequest);
        aliRealNameAuthRequest.setVerifyId(verifyId);
        //缓存实名认证信息
        String jsonData = JSON.toJSONString(aliRealNameAuthRequest);
        redisTemplateFacde.set(this.getRealNameAuthKey(verifyId), jsonData, VERIFY_EXPIRE_IN_SECOND);
        return String.format("https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=%s&scope=id_verify&redirect_uri=%s&cert_verify_id=%s",
                aliProperties.getAppId(), aliProperties.getRealNameCallUrl(), verifyId);
    }

    /**
     * 获取实名认证地址
     *
     * @param userId
     * @return
     */
    public AliRealNameAuthInfo getUserInfo(int userId, int flag) {
        AliRealNameAuthInfo aliRealNameAuthInfo = new AliRealNameAuthInfo();
        if (GlobalConstants.RETAIL_USER_FLAG == flag) {
            //散户
            UserPlus user = userService.get(userId);
            if (user != null) {
                aliRealNameAuthInfo.setRealNameState(user.getRealNameState());

                // 实名认证过
                if (user.getRealNameState() == 1) {
                    Optional.ofNullable(userPlatformRewardRecordService.getRelaNameRecord(userId)).ifPresent(i -> aliRealNameAuthInfo.setMoney(i.getMoney()));
                }
            } else {
                aliRealNameAuthInfo.setRealNameState(YesOrNoEnum.NO.getCode());
            }
        } else {
            //测试账号
            aliRealNameAuthInfo.setRealNameState(YesOrNoEnum.YES.getCode());
        }

        return aliRealNameAuthInfo;
    }

    /**
     * 实名认证
     */
    @Transactional(rollbackFor = Exception.class)
    public AliRealNameCheckResponse check(AliRealNameAuthCheckRequest vm) {
        AliRealNameCheckResponse result = new AliRealNameCheckResponse();

        String code = vm.getCode();
        String verifyId = vm.getVerifyId();

        boolean checkPass = false;
        try {
            String userData = redisTemplateFacde.get(this.getRealNameAuthKey(verifyId));
            if (StringUtil.isEmpty(userData)) {
                throw new ServiceErrorException("实名认证信息不存在,请刷新二维码");
            }
            String accessToken = this.getToken(code);
            AlipayClient alipayClient = new DefaultAlipayClient(ALI_GATEWAY_URL, aliProperties.getAppId(),
                    aliProperties.getPrivateKey(), "json", "UTF-8", aliProperties.getAliPayPublicKey(), "RSA2");

            JSONObject identityParam = new JSONObject();
            identityParam.put("verify_id", verifyId);

            AlipayUserCertdocCertverifyConsultRequest request = new AlipayUserCertdocCertverifyConsultRequest();
            request.setBizContent(identityParam.toString());

            AlipayUserCertdocCertverifyConsultResponse response = alipayClient.execute(request, accessToken);
            if (response.isSuccess()) {
                if (response.getPassed().equals("T")) {
                    checkPass = true;
                    //修改用户信息
                    AliRealNameAuthRequest aliRealNameAuthRequest = JSON.parseObject(userData, AliRealNameAuthRequest.class);
                    UserPlus user = userService.get(aliRealNameAuthRequest.getUserId());
                    if (user == null) {
                        throw new ServiceErrorException("用户信息不存在");
                    }
                    realNameService.updateRealNameInfo(user, aliRealNameAuthRequest.getIdNo(), aliRealNameAuthRequest.getName());

                    UserPlatformRewardRecordDO rewardRecord = userPlatformRewardRecordService.doRealNameVerifySuccessReward(aliRealNameAuthRequest.getUserId());
                    if (rewardRecord != null) {
                        result.setMoney(rewardRecord.getMoney());
                    }
                } else {
                    throw new ServiceErrorException(response.getFailReason());
                }
            } else {
                throw new ServiceErrorException(response.getMsg());
            }
        } catch (Exception e) {
            log.error("实名认证失败:" + e.getMessage(), e);
            throw new ServiceErrorException("实名认证校验失败: " + e.getMessage());
        } finally {
            redisTemplateFacde.delete(this.getRealNameAuthKey(verifyId));
        }

        result.setCheckPass(checkPass);
        return result;
    }

    private String getToken(String code) {
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(ALI_GATEWAY_URL, aliProperties.getAppId(),
                    aliProperties.getPrivateKey(), "json", "UTF-8", aliProperties.getAliPayPublicKey(), "RSA2");
            AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
            request.setGrantType("authorization_code");
            request.setCode(code);
            AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                return response.getAccessToken();
            } else {
                throw new ServiceErrorException(response.getMsg());
            }
        } catch (Exception e) {
            log.error("实名认证获取令牌失败:" + e.getMessage(), e);
            throw new ServiceErrorException("实名认证获取令牌失败:" + e.getMessage());
        }
    }

    /**
     * 判断当前实名认证绑定账号个数
     *
     * @param idNo
     */
    public void checkIdNoExit(String idNo) {
        //判断当前实名认证绑定账号个数，大于等于2个就不让其绑定
        List<UserPlus> list = userService.findRealNameAuthByIdNo(idNo);
        if (list != null && list.size() >= 2) {
            throw new ServiceErrorException("您当前实名认证绑定账号已达上限");
        }
    }

    /**
     * 实名认证申的redis key
     *
     * @return
     */
    private String getRealNameAuthKey(String verifyId) {
        return Messages.format("user:auth:{}", verifyId);
    }

}
