package com.csgo.service.face;

import cn.hutool.core.util.IdcardUtil;
import com.csgo.constants.CommonBizCode;
import com.csgo.constants.LockConstant;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.user.UserPlatformRewardRecordDO;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.exception.ServiceErrorException;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.exception.BizServerException;
import com.csgo.framework.service.LockService;
import com.csgo.modular.common.redis.counter.RedisCounterType;
import com.csgo.modular.common.redis.counter.RedisDailyCounterLogic;
import com.csgo.service.user.UserPlatformRewardRecordService;
import com.csgo.service.user.UserService;
import com.csgo.support.GlobalConstants;
import com.csgo.web.request.face.TencentRealNameCheckRequest;
import com.csgo.web.request.face.TencentRealNameCheckResponse;
import com.csgo.web.support.UserInfo;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.faceid.v20180301.FaceidClient;
import com.tencentcloudapi.faceid.v20180301.models.IdCardVerificationRequest;
import com.tencentcloudapi.faceid.v20180301.models.IdCardVerificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TencentRealNameService {

    private static final Integer MAX_DAILY_CHECK_COUNT = 5;

    private final UserPlatformRewardRecordService userPlatformRewardRecordService;
    private final RedisDailyCounterLogic redisDailyCounterLogic;
    private final RealNameService realNameService;

    @Autowired(required = false)
    private FaceidClient faceidClient;

    @Autowired
    private UserService userService;
    @Autowired
    private AliRealNameAuthService aliRealNameAuthService;
    @Autowired
    private LockService lockService;

    @Transactional(rollbackFor = Exception.class)
    public TencentRealNameCheckResponse idCardVerification(TencentRealNameCheckRequest vm, UserInfo currentUser) {
        Integer userId = currentUser.getId();
        Assert.notNull(userId, "用户ID不能为空");
        String lockKey = LockConstant.LOCK_USER + userId;
        RLock rLock = null;
        try {
            rLock = lockService.acquire(lockKey, 5, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new BizClientException(CommonBizCode.COMMON_BUSY);
            }
            return doIdCardVerification(vm, currentUser);
        } finally {
            lockService.releaseLock(lockKey, rLock);
        }
    }

    private TencentRealNameCheckResponse doIdCardVerification(TencentRealNameCheckRequest vm, UserInfo currentUser) {
        if (GlobalConstants.INTERNAL_USER_FLAG == currentUser.getFlag()) {
            throw new ServiceErrorException("用户已经实名认证通过，不能重复认证");
        }
        vm.setIdNo(vm.getIdNo().toUpperCase());
        if (vm.getIdNo().length() != 18) {
            throw new ServiceErrorException("身份证长度不正确，长度应为18位");
        }

        boolean validate = IdcardUtil.isValidCard(vm.getIdNo());
        if (!validate) {
            throw new ServiceErrorException("姓名和身份证号不一致");
        }
        int age = IdcardUtil.getAgeByIdCard(vm.getIdNo(), new Date());
        if (age < 18) {
            throw new ServiceErrorException("身份证未满18周岁，无法实名认证");
        }

        UserPlus user = userService.get(currentUser.getId());
        if (user == null) {
            throw BizServerException.of(CommonBizCode.USER_NOT_FOUND);
        }
        if (YesOrNoEnum.YES.getCode().equals(user.getRealNameState())) {
            throw new ServiceErrorException("用户已经实名认证通过，不能重复认证");
        }
        aliRealNameAuthService.checkIdNoExit(vm.getIdNo());

        Long count = redisDailyCounterLogic.getCount(RedisCounterType.REAL_NAME, user.getId());
        if (count >= MAX_DAILY_CHECK_COUNT) {
            throw new ServiceErrorException("超过实名认证次数");
        }

        TencentRealNameCheckResponse response = new TencentRealNameCheckResponse();

        boolean checkPass = false;
        try {
            IdCardVerificationRequest req = new IdCardVerificationRequest();
            req.setIdCard(vm.getIdNo());
            req.setName(vm.getName());
            IdCardVerificationResponse resp = faceidClient.IdCardVerification(req);
            if ("0".equals(resp.getResult())) {
                checkPass = true;
            } else {
                log.info("[身份验证] data:{}", IdCardVerificationResponse.toJsonString(resp));
                String description = resp.getDescription();
                response.setDescription(description);
            }
        } catch (TencentCloudSDKException e) {
            log.error("[身份验证] 腾讯服务错误", e);
            throw new ServiceErrorException("身份认证务系统错误，请联系管理人员");
        }
        if (checkPass) {
            realNameService.updateRealNameInfo(user, vm.getIdNo(), vm.getName());

            UserPlatformRewardRecordDO rewardRecord = userPlatformRewardRecordService.doRealNameVerifySuccessReward(currentUser.getId());
            if (rewardRecord != null) {
                response.setMoney(rewardRecord.getMoney());
            }
        }
        response.setCheckPass(checkPass);

        redisDailyCounterLogic.incrementCount(RedisCounterType.REAL_NAME, user.getId());

        return response;
    }


}
