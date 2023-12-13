package com.csgo.service.user;

import java.math.BigDecimal;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.csgo.domain.plus.user.UserBalancePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.user.UserBalancePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.util.StringUtil;

/**
 * @author admin
 */
@Service
public class BalanceHelper {
    @Autowired
    private UserBalancePlusMapper userBalancePlusMapper;
    @Autowired
    private UserPlusMapper userPlusMapper;


    public UserBalancePlus addDiamond(int userId, BigDecimal price, String remark, int type) {
        UserPlus user = userPlusMapper.selectById(userId);
        user.setDiamondBalance(user.getDiamondBalance().add(price));
        userPlusMapper.updateById(user);
        UserBalancePlus userBalance = new UserBalancePlus();
        userBalance.setAddTime(new Date());
        userBalance.setAmount(price);
        userBalance.setType(type);
        userBalance.setRemark(remark);
        userBalance.setCurrentAmount(user.getBalance());
        userBalance.setCurrentDiamondAmount(user.getDiamondBalance());
        userBalance.setUserId(user.getId());
        userBalance.setBalanceNumber(StringUtil.randomNumber(15));
        userBalancePlusMapper.insert(userBalance);
        return userBalance;
    }
}
