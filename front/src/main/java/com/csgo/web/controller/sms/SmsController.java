package com.csgo.web.controller.sms;


import cn.hutool.core.util.StrUtil;
import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.user.UserRegisterIp;
import com.csgo.domain.user.User;
import com.csgo.framework.exception.BizClientException;
import com.csgo.mapper.plus.user.UserRegisterIpMapper;
import com.csgo.modular.common.sms.logic.SmsLogic;
import com.csgo.modular.user.logic.UserLogic;
import com.csgo.modular.verify.model.BehaviorValidateVM;
import com.csgo.modular.verify.service.GeetestService;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.user.UserService;
import com.csgo.util.IpUtils;
import com.csgo.util.PhoneUtil;
import com.csgo.util.StringUtil;
import com.csgo.web.support.captcha.SimpleCharVerifyCodeGen;
import com.csgo.web.support.captcha.VerifyCode;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;


/**
 * @author huanghunbao
 */
@Api
@RequireSession
@RequestMapping("/api/sms")
@Slf4j
public class SmsController {


    @Autowired
    private UserService userService;

    @Autowired
    private SmsLogic smsLogic;

    @Autowired
    private RedisTemplateFacde redisTemplateFacde;

    @Autowired
    private UserRegisterIpMapper userRegisterIpMapper;

    @Autowired
    private GeetestService geetestService;
    @Autowired
    private UserLogic userLogic;

    private static final String REDIS_IMAGE_CODE_KEY = "loginImageCode:";


    @GetMapping("/getSmsCode")
    public BaseResponse<String> getLoginSmsCode(@RequestParam("phone") String phone,
                                                @RequestParam(value = "v", defaultValue = "0") String v,
                                                @RequestParam(value = "imageCode", required = false) String imageCode,
                                                BehaviorValidateVM vm,
                                                HttpServletRequest request) {
        //ip地址
        String ip = IpUtils.getIp(request);
        log.info("用户登陆短信接口IP地址>>{},手机号码>>{},图形验证码>>{}", ip, phone, imageCode);
        if (PhoneUtil.verificationPhone(phone)) {
            throw BizClientException.of(CommonBizCode.USER_MOBILE_ILLEGAL);
        }
        // 忘记密码的，注册过的才给发短信
        User user = userService.getUserByUserName(phone);
        if (user == null) {
            throw BizClientException.of(CommonBizCode.USER_NOT_FOUND);
        }
        //判断验证码是否正确
        if ("0".equals(v)) {
            this.checkImageCode(imageCode);
        } else {
            geetestService.validate(vm);
        }

        return BaseResponse.<String>builder().data(sendSms(phone)).get();
    }

    @GetMapping("/getRegisterSmsCode")
    public BaseResponse<String> getRegisterSmsCode(@RequestParam("phone") String phone,
                                                   @RequestParam(value = "v", defaultValue = "0") String v,
                                                   @RequestParam(value = "imageCode", required = false) String imageCode,
                                                   BehaviorValidateVM vm,
                                                   HttpServletRequest request) {
        //ip地址
        String ip = IpUtils.getIp(request);
        log.info("用户注册短信接口IP地址>>{},手机号码>>{},图形验证码>>{}", ip, phone, imageCode);
        if (PhoneUtil.verificationPhone(phone)) {
            throw BizClientException.of(CommonBizCode.USER_MOBILE_ILLEGAL);
        }

        //判断验证码是否正确
        if ("0".equals(v)) {
            this.checkImageCode(imageCode);
        } else {
            geetestService.validate(vm);
        }

        // 注册过的 ，不发短信
        User user = userService.getUserByUserName(phone);
        if (user != null) {
            userLogic.checkStatus(user);
            throw BizClientException.of(CommonBizCode.USER_REGISTER_MOBILE_ALREADY);
        }

        UserRegisterIp userRegisterIp = userRegisterIpMapper.getByIp(ip);
        if (userRegisterIp != null) {
            if (userRegisterIp.getCnt() >= 5) {
                //每日同一个ip地址注册短信超过5个将不允许注册
                throw BizClientException.of(CommonBizCode.USER_SMS_CODE_SEND_ERROR);
            }
            userRegisterIp.setUpdateDate(new Date());
            userRegisterIp.setCnt(userRegisterIp.getCnt() + 1);
            userRegisterIpMapper.updateById(userRegisterIp);
        } else {
            userRegisterIp = new UserRegisterIp();
            userRegisterIp.setIp(ip);
            userRegisterIp.setCnt(1);
            userRegisterIp.setRegDate(new Date());
            userRegisterIp.setCreateDate(new Date());
            userRegisterIpMapper.insert(userRegisterIp);
        }
        return BaseResponse.<String>builder().data(sendSms(phone)).get();
    }

    private String sendSms(String phone) {
        String smsCode = RandomStringUtils.randomNumeric(6);
        boolean response = smsLogic.sendSmsCode(phone, smsCode);
        if (response) {
            // redis存入缓存，并且5*60s过期
            String key = "sms:" + phone;
            redisTemplateFacde.set(key, smsCode, 5 * 60);
        }
        return String.valueOf(response);
    }

    @GetMapping("/getImageCode")
    public BaseResponse<String> getImageCode() {
        VerifyCode verifyCode = SimpleCharVerifyCodeGen.generate(80, 30);
        log.debug("image code:{}", verifyCode.getCode());
        String key = REDIS_IMAGE_CODE_KEY + verifyCode.getCode().toLowerCase();
        redisTemplateFacde.set(key, verifyCode.getCode().toLowerCase(), 2 * 60);
        Base64.Encoder encoder = Base64.getEncoder();
        String image = encoder.encodeToString(verifyCode.getImgBytes());
        return BaseResponse.<String>builder().data(image).get();
    }

    private void checkImageCode(String imageCode) {
        if (StrUtil.isBlank(imageCode)) {
            throw BizClientException.of(CommonBizCode.CODE_VERIFY_PARAM_ILLEGAL);
        }
        String key = REDIS_IMAGE_CODE_KEY + imageCode.toLowerCase();
        String code = redisTemplateFacde.get(key);
        if (StringUtil.isEmpty(code)) {
            throw BizClientException.of(CommonBizCode.USER_IMAGE_CODE_ERROR);
        }
        if (!imageCode.toLowerCase().equals(code.toLowerCase())) {
            throw BizClientException.of(CommonBizCode.USER_IMAGE_CODE_ERROR);
        }
        redisTemplateFacde.delete(key);
    }
}
