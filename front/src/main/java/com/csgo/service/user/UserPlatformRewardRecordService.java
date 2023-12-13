package com.csgo.service.user;

import com.csgo.domain.plus.user.UserPlatformRewardRecordDO;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.user.UserPlatformRewardRecordMapper;
import com.csgo.support.ConcurrencyLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPlatformRewardRecordService {

    private static final BigDecimal REWARD_MONEY = new BigDecimal("0.5");

    private final UserPlatformRewardRecordMapper userPlatformRewardRecordMapper;

    private final UserBalanceService userBalanceService;

    private final UserService userService;

    @Transactional(rollbackFor = Exception.class)
    @ConcurrencyLimit
    public UserPlatformRewardRecordDO doRealNameVerifySuccessReward(Integer userId) {
        UserPlatformRewardRecordDO exist = userPlatformRewardRecordMapper.selectByTypeAndUserId(UserPlatformRewardRecordDO.TYPE_REAL_NAME_VERIFY, userId);
        if (exist != null) {
            log.info("[平台奖励][实名认证] 已获得奖励,忽略");
            return null;
        }
        log.info("[平台奖励][实名认证] 发放奖励 userId:{} money:{}", userId, REWARD_MONEY);

        UserPlus userPlus = userService.get(userId);
        UserPlatformRewardRecordDO entity = createRecord(UserPlatformRewardRecordDO.TYPE_REAL_NAME_VERIFY, userId, REWARD_MONEY);

        userBalanceService.add(userPlus, REWARD_MONEY, "实名认证奖励", 1);
        return entity;
    }

    public UserPlatformRewardRecordDO getRelaNameRecord(Integer userId) {
        return userPlatformRewardRecordMapper.selectByTypeAndUserId(UserPlatformRewardRecordDO.TYPE_REAL_NAME_VERIFY, userId);
    }

    public UserPlatformRewardRecordDO createRecord(Integer type, Integer userId, BigDecimal money) {
        Date now = new Date();
        UserPlatformRewardRecordDO entity = new UserPlatformRewardRecordDO();
        entity.setType(type);
        entity.setUserId(userId);
        entity.setMoney(money);
        entity.setCreateDate(now);
        entity.setUpdateDate(now);
        userPlatformRewardRecordMapper.insert(entity);
        return entity;
    }


}
