package com.csgo.modular.verify.service;

import com.csgo.constants.CommonBizCode;
import com.csgo.framework.exception.BizClientException;
import com.csgo.modular.verify.config.GeetestConfig;
import com.csgo.modular.verify.model.BehaviorRegisterVO;
import com.csgo.modular.verify.model.BehaviorValidateVM;
import com.csgo.modular.verify.sdk.enums.DigestmodEnum;
import com.csgo.modular.verify.sdk.utils.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeetestService {

    private final GeetestConfig geetestConfig;
    private final GeetestApiService geetestApiService;

    private static final boolean NEW_CAPTCHA = true;

    public BehaviorRegisterVO register() {
        DigestmodEnum digestmodEnum = DigestmodEnum.MD5;

        Map<String, String> paramMap = new HashMap<>(8);
        paramMap.put("digestmod", digestmodEnum.getName());
        paramMap.put("client_type", "web");

        String originChallenge = geetestApiService.requestRegister(paramMap);
        return buildRegisterResult(originChallenge, digestmodEnum);
    }

    /**
     * 构建验证初始化接口返回数据
     */
    private BehaviorRegisterVO buildRegisterResult(String originChallenge, DigestmodEnum digestmodEnum) {
        // origin_challenge为空或者值为0代表失败
        if (originChallenge == null || originChallenge.isEmpty() || "0".equals(originChallenge)) {
            // 本地随机生成32位字符串
            String challenge = UUID.randomUUID().toString().replaceAll("-", "");

            BehaviorRegisterVO vo = new BehaviorRegisterVO();
            vo.setGt(geetestConfig.getGeetestId());
            vo.setChallenge(challenge);
            vo.setNew_captcha(NEW_CAPTCHA);
            vo.setOffline(true);

            log.error("[行为验证] 初始化接口失败，后续流程走宕机模式");
            return vo;
        } else {
            String challenge = null;
            if (DigestmodEnum.MD5.equals(digestmodEnum)) {
                challenge = EncryptionUtils.md5_encode(originChallenge + geetestConfig.getGeetestKey());
            } else if (DigestmodEnum.SHA256.equals(digestmodEnum)) {
                challenge = EncryptionUtils.sha256_encode(originChallenge + geetestConfig.getGeetestKey());
            } else if (DigestmodEnum.HMAC_SHA256.equals(digestmodEnum)) {
                challenge = EncryptionUtils.hmac_sha256_encode(originChallenge, geetestConfig.getGeetestKey());
            } else {
                challenge = EncryptionUtils.md5_encode(originChallenge + geetestConfig.getGeetestKey());
            }

            BehaviorRegisterVO vo = new BehaviorRegisterVO();
            vo.setGt(geetestConfig.getGeetestId());
            vo.setChallenge(challenge);
            vo.setNew_captcha(NEW_CAPTCHA);
            vo.setOffline(false);
            return vo;
        }
    }

    public void validate(BehaviorValidateVM vm) {
        if (vm == null) {
            log.error("参数challenge、validate、seccode不可为空");
            throw BizClientException.of(CommonBizCode.CODE_VERIFY_PARAM_ILLEGAL);
        }
        String challenge = vm.getChallenge();
        String validate = vm.getValidate();
        String seccode = vm.getSeccode();
        if (!this.checkParam(challenge, validate, seccode)) {
            log.error("参数challenge、validate、seccode不可为空");
            throw BizClientException.of(CommonBizCode.CODE_VERIFY_PARAM_ILLEGAL);
        }
        doValidate(challenge, validate, seccode);
    }

    /**
     * 正常流程下（即验证初始化成功），二次验证
     */
    public void doValidate(String challenge, String validate, String seccode) {
        Map<String, String> paramMap = new HashMap<>(8);
        String response_seccode = geetestApiService.requestValidate(challenge, validate, seccode, paramMap);
        if (response_seccode == null || response_seccode.isEmpty()) {
            log.error("[行为验证][二次验证] 请求异常 response_seccode is empty");
            throw BizClientException.of(CommonBizCode.CODE_VERIFY_ERROR);
        } else if ("false".equals(response_seccode)) {
            throw BizClientException.of(CommonBizCode.CODE_VERIFY_ERROR);
        }
    }

    /**
     * 校验二次验证的三个参数，校验通过返回true，校验失败返回false
     */
    private boolean checkParam(String challenge, String validate, String seccode) {
        return !(challenge == null || challenge.trim().isEmpty() || validate == null || validate.trim().isEmpty() || seccode == null || seccode.trim().isEmpty());
    }

}
