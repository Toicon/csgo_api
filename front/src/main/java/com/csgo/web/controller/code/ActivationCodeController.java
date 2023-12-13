package com.csgo.web.controller.code;

import com.csgo.domain.plus.code.ActivationCode;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.code.ActivationCodeService;
import com.csgo.service.user.BalanceSupportService;
import com.csgo.support.BusinessException;
import com.csgo.support.ExceptionCode;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.code.ReceiveActivationCodeRequest;
import com.csgo.web.response.code.ActivationCodeResult;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author admin
 */
@Api
@LoginRequired
@RequireSession
@Slf4j
@io.swagger.annotations.Api(tags = "CDK相关接口")
public class ActivationCodeController {
    @Autowired
    private SiteContext siteContext;
    @Autowired
    private RedisTemplateFacde redisTemplate;
    @Autowired
    private ActivationCodeService activationCodeService;
    @Autowired
    private BalanceSupportService balanceSupportService;

    @ApiOperation(value = "领取CDK礼品接口", notes = "领取CDK礼品接口")
    @PostMapping("/activation/code/receive")
    public BaseResponse<ActivationCodeResult> receive(@RequestBody ReceiveActivationCodeRequest request) {
        List<ActivationCode> codes = activationCodeService.findByCdKey(request.getCdKey());
        if (CollectionUtils.isEmpty(codes)) {
            throw new BusinessException(ExceptionCode.CDK_NOT_FOUND);
        }
        if (codes.size() > 1) {
            throw new BusinessException(ExceptionCode.CDK_ERROR);
        }
        ActivationCode code = codes.get(0);
        if (null != code.getUserId() || StringUtils.hasText(code.getUserName()) || null != code.getReceiveDate()) {
            throw new BusinessException(ExceptionCode.CDK_IS_USED);
        }
        if (code.getTargetUserId() != null) {
            //判断CDK是否存在目标账号，如果存在则判断当前用户是不是目标账号
            Integer currentUserId = siteContext.getCurrentUser().getId();
            if (!currentUserId.equals(code.getTargetUserId())) {
                throw new BusinessException(ExceptionCode.CDK_NOT_FOUND);
            }
        }
        if (!redisTemplate.tryLock("CDK-" + code.getCdKey(), 60)) {
            throw new BusinessException(ExceptionCode.CDK_REPEAT);
        }
        try {
            ActivationCodeResult result = balanceSupportService.receiveCDK(siteContext.getCurrentUser().getId(), code);
            redisTemplate.delete("CDK-" + code.getCdKey());
            return BaseResponse.<ActivationCodeResult>builder().data(result).get();
        } catch (Exception e) {
            redisTemplate.delete("CDK-" + code.getCdKey());
            throw e;
        }
    }
}
