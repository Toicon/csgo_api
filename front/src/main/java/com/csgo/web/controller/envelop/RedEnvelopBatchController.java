package com.csgo.web.controller.envelop;

import cn.hutool.core.collection.CollUtil;
import com.csgo.constants.CommonBizCode;
import com.csgo.constants.LockConstant;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.service.LockService;
import com.csgo.framework.util.RUtil;
import com.csgo.service.envelop.RedEnvelopReceiveService;
import com.csgo.service.face.RealNameService;
import com.csgo.support.StandardExceptionCode;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.envelop.ReceiveEnvelopBatchRequest;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
@Api
@RequestMapping("/red/envelop")
@Slf4j
@io.swagger.annotations.Api(tags = "红包领取接口")
@RequireSession
@LoginRequired
public class RedEnvelopBatchController {

    @Autowired
    private SiteContext siteContext;
    @Autowired
    private LockService lockService;
    @Autowired
    private RealNameService realNameService;
    @Autowired
    private RedEnvelopReceiveService redEnvelopReceiveService;

    @ApiOperation("批量领取红包")
    @PostMapping(value = "/batch-receive")
    public BaseResponse<Void> batchReceive(@Valid @RequestBody ReceiveEnvelopBatchRequest vm) {
        Integer userId = siteContext.getCurrentUser().getId();
        if (vm == null || CollUtil.isEmpty(vm.getEnvelopIdList())) {
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        //实名认证判断
        realNameService.checkRealNameVerifyPass(userId);

        String lockKey = LockConstant.LOCK_USER + userId;
        RLock rLock = null;
        try {
            rLock = lockService.acquire(lockKey, 5, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new BizClientException(CommonBizCode.COMMON_BUSY);
            }
            Set<Integer> envelopIdList = new HashSet<>(vm.getEnvelopIdList());
            for (Integer envelopId : envelopIdList) {
                redEnvelopReceiveService.receiptedEnvelop(userId, envelopId);
            }
        } finally {
            lockService.releaseLock(lockKey, rLock);
        }
        return RUtil.ok();
    }

}
