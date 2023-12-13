package com.csgo.service.user;

import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.user.UserInnerRechargeLimit;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.UserBalance;
import com.csgo.mapper.UserBalanceMapper;
import com.csgo.mapper.plus.user.UserInnerRechargeLimitMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class UserBalanceService {

    @Autowired
    private UserBalanceMapper userBalanceMapper;
    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private UserInnerRechargeLimitMapper userInnerRechargeLimitMapper;

    @Transactional
    public void add(UserPlus user, BigDecimal amount, String remark) {
        user.setBalance(user.getBalance().add(amount));
        user.setUpdatedAt(new Date());
        userPlusMapper.updateById(user);

        UserBalance userBalance = new UserBalance();
        userBalance.setAddTime(new Date());
        userBalance.setAmount(amount);
        userBalance.setDiamondAmount(BigDecimal.ZERO);
        userBalance.setType(1); //收入
        userBalance.setRemark(remark);
        userBalance.setCurrentAmount(user.getBalance());
        userBalance.setCurrentDiamondAmount(user.getDiamondBalance());
        userBalance.setUserId(user.getId());
        userBalance.setBalanceNumber(StringUtil.randomNumber(15));
        userBalanceMapper.add(userBalance);
    }

    public List<UserInnerRechargeLimit> findNeedRemoveInnerRecharge(Date date) {
        return userInnerRechargeLimitMapper.findNeedRemoveRecord(date);
    }

    public void removeInnerRecharge(UserInnerRechargeLimit rechargeLimit) {
        rechargeLimit.setOvertime(true);
        BaseEntity.updated(rechargeLimit, "job");
        userInnerRechargeLimitMapper.updateById(rechargeLimit);
    }

}
