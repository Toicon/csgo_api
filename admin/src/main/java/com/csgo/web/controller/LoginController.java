package com.csgo.web.controller;

import com.csgo.domain.plus.user.AdminUserPlus;
import com.csgo.modular.common.sms.logic.SmsLogic;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.AdminUserService;
import com.csgo.service.SmsService;
import com.csgo.support.BusinessException;
import com.csgo.support.ExceptionCode;
import com.csgo.support.StandardExceptionCode;
import com.csgo.web.request.LoginRequest;
import com.csgo.web.support.LoginRequired;
import com.csgo.web.support.SiteContext;
import com.csgo.web.support.UserInfo;
import com.csgo.web.support.captcha.SimpleCharVerifyCodeGen;
import com.csgo.web.support.captcha.VerifyCode;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.interceptor.session.SessionContext;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Base64;

@Api
@RequireSession
public class LoginController {

    @Autowired
    private AdminUserService service;
    @Autowired
    private SiteContext siteContext;
    @Autowired
    private SessionContext sessionContext;
    @Autowired
    private RedisTemplateFacde redisService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private SmsLogic smsLogic;

    @LoginRequired
    @GetMapping("/info")
    public BaseResponse<UserInfo> info() {
        return BaseResponse.<UserInfo>builder().data(siteContext.getCurrentUser()).get();
    }

    @GetMapping("/captchaImage")
    public BaseResponse<String> getVerificationCode() {
        VerifyCode verifyCode = SimpleCharVerifyCodeGen.generate(80, 28);
        redisService.setSimple(sessionContext.getId(), verifyCode.getCode());
        Base64.Encoder encoder = Base64.getEncoder();
        String image = encoder.encodeToString(verifyCode.getImgBytes());
        return BaseResponse.<String>builder().data(image).get();
    }

    @GetMapping("/getSmsCode")
    public BaseResponse<Void> getLoginSmsCode(@RequestParam("userName") String userName, @RequestParam("code") String code) {
        if (!StringUtils.hasText(code)) {
            throw new ApiException(StandardExceptionCode.LOGIN_FAILURE, "图形验证码错误");
        }
        String imgCode = redisService.getSimple(sessionContext.getId());
        if (!StringUtils.hasText(imgCode) || !imgCode.equalsIgnoreCase(code)) {
            throw new ApiException(StandardExceptionCode.LOGIN_FAILURE, "验证码错误");
        }
        sendSms(userName);
        return BaseResponse.<Void>builder().get();
    }

    @PostMapping("/login")
    public BaseResponse<UserInfo> login(@Valid @RequestBody LoginRequest request) {
        if (StringUtils.isEmpty(request.getVerificationCode())) {
            throw new ApiException(StandardExceptionCode.LOGIN_FAILURE, "短信验证码不能为空");
        }
        AdminUserPlus user = service.get(request.getUsername(), request.getPassword());
        if (user == null) {
            throw new ApiException(StandardExceptionCode.LOGIN_FAILURE, "用户名或密码错误");
        }
        boolean isValidSmsCode = smsService.isValidSmsCode(user.getPhone(), request.getVerificationCode());
        if (!isValidSmsCode) {
            throw new BusinessException(ExceptionCode.MSG_CODE_ERROR);
        }
        smsService.delSmsCode(user.getPhone());
        if (user.isFrozen()) {
            throw new ApiException(StandardExceptionCode.LOGIN_FAILURE, "用户被冻结");
        }
        siteContext.login(user);
        return BaseResponse.<UserInfo>builder().data(siteContext.getCurrentUser()).get();
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public BaseResponse<Void> logout() {
        siteContext.logout();
        return BaseResponse.<Void>builder().get();
    }

    private String sendSms(String userName) {
        AdminUserPlus adminUser = service.getByUserName(userName);
        if (null == adminUser) {
            throw new ApiException(StandardExceptionCode.LOGIN_FAILURE, "用户名不存在");
        }
        String smsCode = RandomStringUtils.randomNumeric(6);

        boolean response = smsLogic.sendSmsCode(adminUser.getPhone(), smsCode);

        if (response) {
            // redis存入缓存，并且5*60s过期
            String key = "admin-sms:" + adminUser.getPhone();
            redisTemplateFacde.set(key, smsCode, 5 * 60);
        }
        return String.valueOf(response);
    }

}
