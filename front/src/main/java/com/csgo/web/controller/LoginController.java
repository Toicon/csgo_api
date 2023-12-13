package com.csgo.web.controller;

import com.csgo.config.properties.SteamProperties;
import com.csgo.constants.CommonBizCode;
import com.csgo.constants.SystemConfigConstants;
import com.csgo.constants.UserConstants;
import com.csgo.domain.ExchangeRate;
import com.csgo.domain.plus.config.SystemConfig;
import com.csgo.domain.plus.membership.Membership;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.User;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.exception.BizServerException;
import com.csgo.mapper.ExchangeRateMapper;
import com.csgo.modular.user.logic.UserLogic;
import com.csgo.modular.verify.service.GeetestService;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.OrderRecordService;
import com.csgo.service.config.SystemConfigService;
import com.csgo.service.membership.MembershipService;
import com.csgo.service.steam.SteamValidateService;
import com.csgo.service.user.UserLoginOutRecordService;
import com.csgo.service.user.UserLoginRecordService;
import com.csgo.service.user.UserService;
import com.csgo.sms.SendShortMessage;
import com.csgo.sms.SmsService;
import com.csgo.support.GlobalConstants;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.DateUtilsEx;
import com.csgo.util.IpUtils;
import com.csgo.util.SecuritySHA1Utils;
import com.csgo.util.StringUtil;
import com.csgo.web.request.LoginRequest;
import com.csgo.web.request.LoginSmsRequest;
import com.csgo.web.response.user.InvitedUserResponse;
import com.csgo.web.response.user.UserResponse;
import com.csgo.web.support.SiteContext;
import com.csgo.web.support.UserInfo;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Api
@RequireSession
@Slf4j
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private SiteContext siteContext;
    @Autowired
    private ExchangeRateMapper exchangeRateMapper;
    @Value("${baseExtensionUrl}")
    private String baseExtensionUrl;
    @Value("${faceUrl}")
    private String faceUrl;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private SteamValidateService steamValidateService;
    @Autowired
    private SteamProperties steamProperties;
    @Autowired
    private UserLoginRecordService userLoginRecordService;
    @Autowired
    private UserLoginOutRecordService userLoginOutRecordService;
    @Autowired
    private MembershipService membershipService;
    @Autowired
    private OrderRecordService orderRecordService;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private GeetestService geetestService;
    @Autowired
    private UserLogic userLogic;
    private static final String REDIS_IMAGE_CODE_KEY = "loginImageCode:";

    /**
     * 用户授权获取steamid
     *
     * @param request
     * @return
     */
    @GetMapping("/steam")
    public void auth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String steamId = steamValidateService.verify(request.getRequestURL().toString(), request.getParameterMap());
        if (!StringUtils.isEmpty(steamId)) {
            UserPlus user = userService.getBySteamId(steamId);
            if (user == null) {
                user = userService.registerBySteamId(steamId);
            }
            UserInfo userInfo = new UserInfo();
            BeanUtils.copyProperties(user, userInfo);
            userInfo.setAppointAmount(appointAmount(user.getPayMoney()));
            Membership membership = membershipService.findByUserId(user.getId());
            userInfo.setMembershipLevel(membership.getGrade());
            userInfo.setMembershipImg(membership.getImg());
            siteContext.login(userInfo);

            userLoginRecordService.insert(user.getId(), IpUtils.getIp(request), request.getRemotePort(), isApp(request));
        }
        response.sendRedirect(steamProperties.getFrontUrl());
    }

    /**
     * 获取用户推广码
     *
     * @return
     */
    @GetMapping("/user/extension-code")
    public BaseResponse<String> getUserExtensionCode(@RequestParam("extensionCode") String extensionCode) {
        User user = userService.getUserByExtensionUrl(baseExtensionUrl + extensionCode);
        if (user == null) {
            return BaseResponse.<String>builder().data("").get();
        }
        return BaseResponse.<String>builder().data(user.getExtensionCode()).get();
    }


    @GetMapping("/user/info")
    public BaseResponse<UserResponse> info() {
        if (siteContext.getCurrentUser() == null) {
            return BaseResponse.<UserResponse>builder().get();
        }
        UserPlus user = userService.get(siteContext.getCurrentUser().getId());
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        response.setAppointAmount(appointAmount(user.getPayMoney()));

        Membership membership = membershipService.findByUserId(user.getId());
        response.setMembershipLevel(membership.getGrade());
        response.setMembershipImg(membership.getImg());
        response.setIsReal(null != membership.getBirthday());
        //排除默认注册为未分类主播id
        if (user.getParentId() != null && !user.getParentId().equals(UserConstants.UNCLASSIFIED_USER_ID)) {
            UserPlus parent = userService.get(user.getParentId());
            if (parent != null) {
                InvitedUserResponse invitedUser = new InvitedUserResponse();
                BeanUtils.copyProperties(parent, invitedUser);
                response.setInvitedUser(invitedUser);
            }
        } else {
            Date now = new Date();
            Date limit = DateUtilsEx.calDateByHour(user.getCreatedAt(), 24);
            if (now.before(limit)) {
                response.setCanInviteCode(true);
                response.setCountDown(DateUtilsEx.getDifferSecond(now, limit).intValue());
            }
        }
        response.setIsFirstRecharge(orderRecordService.findFirst(user.getId()) == null);
        response.setRechargeOpen(userService.validateInnerRecharge(user.getId()));
        response.setLYFOpen(BigDecimal.ZERO.compareTo(orderRecordService.getSumUserPaySuccess(user.getId())) != 0);
        return BaseResponse.<UserResponse>builder().data(response).get();
    }

    /**
     * 用户账号登录
     */
    @PostMapping(value = "/login")
    public BaseResponse<UserInfo> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        User list = userService.getUserByUserName(request.getUsername());
        if (list == null) {
            throw BizClientException.of(CommonBizCode.USER_LOGIN_FAIL);
        }
        if (StringUtils.isEmpty(request.getUsername())) {
            throw BizClientException.of(CommonBizCode.USER_NAME_BLANK);
        }
        if (StringUtils.isEmpty(request.getPassword())) {
            throw BizClientException.of(CommonBizCode.USER_PASSWORD_BLANK);
        }

        //判断验证码是否正确
        this.checkCodeVerify(request);

        User user = userService.login(request.getUsername(), request.getPassword());
        if (user == null) {
            throw BizClientException.of(CommonBizCode.USER_LOGIN_FAIL);
        }
        if (user.getFrozen()) {
            throw BizClientException.of(CommonBizCode.USER_ACCOUNT_FROZEN);
        }
        if (user.getIsDelete() == 0) {
            throw BizClientException.of(CommonBizCode.USER_ACCOUNT_DELETED);
        }
        userLogic.checkStatus(user);

        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setAppointAmount(appointAmount(user.getPay_money()));
        Membership membership = membershipService.findByUserId(user.getId());
        userInfo.setMembershipLevel(membership.getGrade());
        userInfo.setMembershipImg(membership.getImg());
        userInfo.setIsReal(null != membership.getBirthday());

        userInfo.setIsFirstRecharge(orderRecordService.findFirst(user.getId()) == null);
        siteContext.login(userInfo);

        userLoginRecordService.insert(user.getId(), IpUtils.getIp(servletRequest), servletRequest.getRemotePort(), isApp(servletRequest));
        log.info("用户账号密码登录：账号>>{},ip地址>>{},端口号>>{},时间>>{}", userInfo.getUserName(), IpUtils.getIp(servletRequest), servletRequest.getRemotePort(), DateUtilsEx.getDateStr(new Date()));
        userInfo.setSessionId(siteContext.getSessionContext().getId());
        return BaseResponse.<UserInfo>builder().data(userInfo).get();
    }

    private boolean appointAmount(BigDecimal payMoney) {
        SystemConfig systemConfig = systemConfigService.get(SystemConfigConstants.BLIND_BOX_RECHARGE_LIMIT);
        if (null != systemConfig) {
            BigDecimal limit = new BigDecimal(systemConfig.getValue());
            return null != payMoney && payMoney.compareTo(limit) >= 0;
        }
        return false;
    }

    /**
     * 用户验证码登录
     */
    @PostMapping(value = "/loginBySmsCode")
    public BaseResponse<UserInfo> loginBySmsCode(@Valid @RequestBody LoginSmsRequest request, HttpServletRequest servletRequest) {
        User user = userService.getUserByUserName(request.getPhone());
        if (user == null) {
            throw BizClientException.of(CommonBizCode.USER_NOT_FOUND);
        }
        if (StringUtils.isEmpty(request.getPhone())) {
            throw BizClientException.of(CommonBizCode.USER_MOBILE_BLANK);
        }
        if (StringUtils.isEmpty(request.getSmsCode())) {
            throw BizClientException.of(CommonBizCode.USER_SMS_CODE_BLANK);
        }
//        boolean isValidSmsCode = smsModule.match(Scenes.YZM.getKey(), request.getPhone(), TimeLength.minutes(2), request.getSmsCode());
        boolean isValidSmsCode = smsService.isValidSmsCode(request.getPhone(), request.getSmsCode());
        if (!isValidSmsCode) {
            throw BizClientException.of(CommonBizCode.USER_SMS_CODE_ERROR);
        }
        if (user.getFrozen()) {
            throw BizClientException.of(CommonBizCode.USER_ACCOUNT_FROZEN);
        }
        if (user.getIsDelete() == 0) {
            throw BizClientException.of(CommonBizCode.USER_ACCOUNT_DELETED);
        }
        userLogic.checkStatus(user);
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setAppointAmount(appointAmount(user.getPay_money()));
        Membership membership = membershipService.findByUserId(user.getId());
        userInfo.setMembershipLevel(membership.getGrade());
        userInfo.setMembershipImg(membership.getImg());
        userInfo.setIsReal(null != membership.getBirthday());
        userInfo.setIsFirstRecharge(orderRecordService.findFirst(user.getId()) == null);
        siteContext.login(userInfo);

        userLoginRecordService.insert(user.getId(), IpUtils.getIp(servletRequest), servletRequest.getRemotePort(), isApp(servletRequest));
        log.info("用户短信验证码登录：账号>>{},ip地址>>{},端口号>>{},时间>>{}", userInfo.getUserName(), IpUtils.getIp(servletRequest), servletRequest.getRemotePort(), DateUtilsEx.getDateStr(new Date()));
        userInfo.setSessionId(siteContext.getSessionContext().getId());
        return BaseResponse.<UserInfo>builder().data(userInfo).get();
    }

    /**
     * 短信验证码接口
     *
     * @param phone 手机号
     * @return
     */
    @GetMapping("/verification-code")
    public BaseResponse<Void> phone(@RequestParam("phone") String phone) {
        String verificationCode = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        boolean b = new SendShortMessage().senddx(phone, verificationCode);
        if (!b) {
            throw BizClientException.of(CommonBizCode.USER_SMS_CODE_SEND_ERROR);
        }
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 注册用户
     *
     * @param
     * @param verificationCode 短信验证码
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<UserResponse> register(String phone, String password,
                                               String verificationCode, String extensionCode) {
        if (StringUtils.isEmpty(phone)) {
            throw BizClientException.of(CommonBizCode.USER_MOBILE_BLANK);
        }
        if (StringUtils.isEmpty(password)) {
            throw BizClientException.of(CommonBizCode.USER_PASSWORD_BLANK);
        }
        if (StringUtils.isEmpty(verificationCode)) {
            throw BizClientException.of(CommonBizCode.USER_SMS_CODE_BLANK);
        }
        User list = userService.getUserByUserName(phone);
        if (list != null) {
            userLogic.checkStatus(list);
            throw BizClientException.of(CommonBizCode.USER_REGISTER_MOBILE_ALREADY);
        }
        boolean isValidSmsCode = smsService.isValidSmsCode(phone, verificationCode);
        if (!isValidSmsCode) {
            throw BizClientException.of(CommonBizCode.USER_SMS_CODE_ERROR);
        }
        UserPlus user = new UserPlus();
        if (!StringUtils.isEmpty(extensionCode)) {
            log.info("推广码注册进来的------------------【" + extensionCode + "】----");
            User userBean = userService.getUserByExtensionCode(extensionCode.trim());
            if (userBean == null) {
                throw BizClientException.of(CommonBizCode.USER_PROMOTION_CODE_ERROR);
            }
            user.setParentId(userBean.getId());
            user.setInvitedDate(new Date());


            ExchangeRate rate = exchangeRateMapper.getById(1);
            if (rate != null) {
                log.info("设置幸运值------------------【" + rate.getLuckyValue() + "】----");
                user.setLuckyValue(rate.getLuckyValue());
            }
        } else {
            //设置未分类主播id
            user.setParentId(UserConstants.UNCLASSIFIED_USER_ID);
        }
        user.setPassword(password);
        user.setPhone(phone);
        user.setUserName(phone);
        user.setUserNumber(SecuritySHA1Utils.shaEncode(phone));
        String phoneNumber = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        user.setName(phoneNumber);
        user.setType(StringUtils.isEmpty(extensionCode) ? 0 : 1);
        user.setFlag(GlobalConstants.RETAIL_USER_FLAG);
        user.setIsroll(false);
        user.setCreatedAt(new Date());

        user.setExtraNum(0);
        user.setImg(faceUrl);

        user.setIsDelete(1);
        int num = userService.register(user);
        if (num == 0) {
            throw BizClientException.of(CommonBizCode.USER_REGISTER_ERROR);
        }
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return BaseResponse.<UserResponse>builder().data(response).get();
    }

    /**
     * 忘记密码
     */
    @PostMapping("/forget-password")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号"),
            @ApiImplicitParam(name = "password", value = "密码"),
            @ApiImplicitParam(name = "verificationCode", value = "验证码")
    })
    public BaseResponse forgetPassword(String phone, String password,
                                       String verificationCode) {
        if (StringUtils.isEmpty(phone)) {
            throw BizClientException.of(CommonBizCode.USER_MOBILE_BLANK);
        }
        if (StringUtils.isEmpty(password)) {
            throw BizClientException.of(CommonBizCode.USER_PASSWORD_BLANK);
        }
        if (StringUtils.isEmpty(verificationCode)) {
            throw BizClientException.of(CommonBizCode.USER_SMS_CODE_BLANK);
        }
        boolean isValidSmsCode = smsService.isValidSmsCode(phone, verificationCode);
        if (!isValidSmsCode) {
            throw BizClientException.of(CommonBizCode.USER_SMS_CODE_ERROR);
        }
        User user = userService.getUserByUserName(phone);
        if (user == null) {
            throw BizServerException.of(CommonBizCode.USER_NOT_FOUND);
        }
        UserPlus userPlus = userService.get(user.getId());
        userPlus.setPassword(password);
        userPlus.setUpdatedAt(new Date());
        userService.update(userPlus);
        return BaseResponse.<User>builder().data(user).get();
    }

    @PostMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest request) {
        if (siteContext.getCurrentUser() != null) {
            userLoginOutRecordService.insert(siteContext.getCurrentUser().getId(), IpUtils.getIp(request),
                    request.getRemotePort(), isApp(request));
            log.info("用户退出：账号>>{},ip地址>>{},端口号>>{},时间>>{}", siteContext.getCurrentUser().getUserName(), IpUtils.getIp(request), request.getRemotePort(), DateUtilsEx.getDateStr(new Date()));
        }
        siteContext.logout();
        return BaseResponse.<Void>builder().get();
    }


    private Boolean isApp(HttpServletRequest request) {
        // 从浏览器获取请求头信息
        String info = request.getHeader("user-agent");
        return info.contains("Android") || info.contains("iPhone") || info.contains("iPad");
    }

    private void checkCodeVerify(LoginRequest request) {
        if ("0".equals(request.getV())) {
            if (StringUtils.isEmpty(request.getImageCode())) {
                throw BizClientException.of(CommonBizCode.CODE_IMAGE_BLANK);
            }
            this.checkImageCode(request.getImageCode());
            return;
        }
        geetestService.validate(request.getBehavior());
    }

    private void checkImageCode(String imageCode) {
        String key = REDIS_IMAGE_CODE_KEY + imageCode.toLowerCase();
        String code = redisTemplateFacde.get(key);
        if (StringUtil.isEmpty(code)) {
            throw BizClientException.of(CommonBizCode.CODE_IMAGE_EXPIRE);
        }
        if (!imageCode.toLowerCase().equals(code.toLowerCase())) {
            throw BizClientException.of(CommonBizCode.CODE_IMAGE_ERROR);
        }
        redisTemplateFacde.delete(key);
    }
}
