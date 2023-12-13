package com.csgo.web.controller.user;


import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.user.UserBalanceBackend;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.framework.exception.BizClientException;
import com.csgo.mapper.plus.user.UserBalanceBackendMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.user.UserBalanceUpdateRequest;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.support.jackson.json.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 修改用户余额
 *
 * @author admin
 */
@Api
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AdminFrontUserBalanceController extends BackOfficeController {

    private final UserPlusMapper userPlusMapper;

    private final UserBalanceBackendMapper userBalanceBackendMapper;

    private final RedisTemplateFacde redisTemplateFacde;

    private static final String USER_BALANCE_UPDATE_LOCK_KEY = "lock:user:balance:%s";

    @Log(desc = "修改用户余额")
    @PostMapping("/balance")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Boolean> updateBalance(@Valid @RequestBody UserBalanceUpdateRequest req) {
        log.info("[修改用户余额] user id:{} req:{} opUserId:{}", req.getUserId(), JSON.toJSON(req), siteContext.getCurrentUser().getId());

        Integer userId = req.getUserId();
        BigDecimal amount = req.getAmount();

        String key = String.format(USER_BALANCE_UPDATE_LOCK_KEY, userId);
        if (!org.springframework.util.StringUtils.isEmpty(redisTemplateFacde.get(key))) {
            throw new BizClientException(CommonBizCode.COMMON_BUSY);
        }

        try {
            redisTemplateFacde.set(key, JSON.toJSON(req), 60);

            UserPlus user = userPlusMapper.selectById(userId);
            boolean result = false;
            switch (req.getType()) {
                case 1:
                    result = userPlusMapper.addBalance(userId, amount);
                    if (result) {
                        createRecord(userId, 0, amount, user);
                    }
                    break;
                case 2:
                    result = userPlusMapper.subBalance(userId, amount);
                    if (result) {
                        createRecord(userId, 0, amount.negate(), user);
                    }
                    break;
                case 3:
                    result = userPlusMapper.addDiamondBalance(userId, amount);
                    if (result) {
                        createRecord(userId, 1, amount, user);
                    }
                    break;
                case 4:
                    result = userPlusMapper.subDiamondBalance(userId, amount);
                    if (result) {
                        createRecord(userId, 1, amount.negate(), user);
                    }
                    break;
                default:
                    throw BizClientException.of(CommonBizCode.COMMON_TYPE_NOT_SUPPORT);
            }
            return BaseResponse.<Boolean>builder().data(result).get();
        } finally {
            redisTemplateFacde.delete(key);
        }
    }

    private void createRecord(Integer userId, Integer type, BigDecimal amount, UserPlus user) {
        Date now = new Date();

        UserBalanceBackend entity = new UserBalanceBackend();
        entity.setUserId(userId);
        entity.setType(type);
        entity.setAmount(amount);

        entity.setBalance(user.getBalance());
        entity.setDiamondBalance(user.getDiamondBalance());
        entity.setCreateDate(now);
        entity.setUpdateDate(now);
        entity.setCreateBy(siteContext.getCurrentUser().getName());
        userBalanceBackendMapper.insert(entity);
    }

}
