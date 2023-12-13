package com.csgo.modular.user.logic;

import com.csgo.constants.CommonBizCode;
import com.csgo.constants.LockConstant;
import com.csgo.domain.plus.user.UserBalancePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.exception.BizServerException;
import com.csgo.framework.service.LockService;
import com.csgo.framework.util.BigDecimalUtil;
import com.csgo.mapper.plus.user.UserBalancePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserBalanceLogic {

    private final UserPlusMapper userPlusMapper;
    private final UserBalancePlusMapper userBalancePlusMapper;

    private final LockService lockService;

    @Transactional(rollbackFor = Exception.class)
    public UserBalancePlus cost(Integer userId, BigDecimal price, String remark) {
        String lockKey = LockConstant.LOCK_BALANCE + userId;
        RLock rLock = null;
        try {
            rLock = lockService.acquire(lockKey, 3, TimeUnit.SECONDS);
            if (rLock == null) {
                throw new BizClientException(CommonBizCode.COMMON_BUSY);
            }

            UserPlus user = userPlusMapper.selectById(userId);
            if (BigDecimalUtil.lessEqualZero(price)) {
                throw BizServerException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
            }

            if (user.getBalance().compareTo(price) < 0) {
                throw BizClientException.of(CommonBizCode.USER_BALANCE_LACK);
            }

            BigDecimal balance = user.getBalance().subtract(price);
            BigDecimal diamondAmount = BigDecimal.ZERO;
            user.setBalance(balance);
            userPlusMapper.updateById(user);

            UserBalancePlus userBalance = new UserBalancePlus();
            userBalance.setAddTime(new Date());
            userBalance.setAmount(price);
            userBalance.setDiamondAmount(diamondAmount);
            userBalance.setCurrentAmount(user.getBalance());
            userBalance.setCurrentDiamondAmount(user.getDiamondBalance());
            //支出
            userBalance.setType(2);
            userBalance.setRemark(remark);
            userBalance.setUserId(user.getId());
            userBalance.setBalanceNumber(StringUtil.randomNumber(15));
            userBalancePlusMapper.insert(userBalance);
            return userBalance;
        } finally {
            lockService.releaseLock(lockKey, rLock);
        }
    }

}
