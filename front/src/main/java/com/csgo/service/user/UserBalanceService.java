package com.csgo.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.plus.user.UserBalancePlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.UserBalance;
import com.csgo.framework.mo.RecentDateMO;
import com.csgo.mapper.UserBalanceMapper;
import com.csgo.mapper.plus.user.UserBalancePlusMapper;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.support.GlobalConstants;
import com.csgo.support.PageInfo;
import com.csgo.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class UserBalanceService {

    @Autowired
    private UserBalancePlusMapper userBalancePlusMapper;
    @Autowired
    private UserBalanceMapper userBalanceMapper;
    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private UserMessagePlusMapper userMessagePlusMapper;

    public BigDecimal accurateBalance(UserPlus user) {
        BigDecimal balance = user.getBalance() == null ? BigDecimal.ZERO : user.getBalance();
        BigDecimal diamondBalance = user.getDiamondBalance() == null ? BigDecimal.ZERO : user.getDiamondBalance();
        BigDecimal inventoryBalance = userMessagePlusMapper.findByUserId(user.getId(), GlobalConstants.USER_MESSAGE_INVENTORY).stream().map(UserMessagePlus::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        return balance.add(diamondBalance).add(inventoryBalance);
    }

    public UserBalancePlus cost(UserPlus user, BigDecimal balance, BigDecimal diamondBalance) {
        user.setBalance(user.getBalance().subtract(balance));
        userPlusMapper.updateById(user);

        UserBalancePlus userBalance = new UserBalancePlus();
        userBalance.setAddTime(new Date());
        userBalance.setAmount(balance);
        userBalance.setType(2); //支出
        userBalance.setRemark("幸运宝箱");
        userBalance.setCurrentAmount(user.getBalance());
        userBalance.setUserId(user.getId());
        userBalance.setBalanceNumber(StringUtil.randomNumber(15));
        userBalance.setDiamondAmount(diamondBalance);
        userBalance.setCurrentDiamondAmount(user.getDiamondBalance());
        userBalancePlusMapper.insert(userBalance);
        return userBalance;
    }

    public UserBalancePlus add(UserPlus user, BigDecimal price, String remark, int type) {
        user.setBalance(user.getBalance().add(price));
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

    public int add(UserBalance userBalance) {
        return userBalanceMapper.add(userBalance);
    }

    public PageInfo<UserBalance> pageBalanceList(Integer pageNum, Integer pageSize, Integer userId) {
        Page<UserBalance> page = new Page<>(pageNum, pageSize);
        Page<UserBalance> userBalanceList = userBalanceMapper.pageList(page, null, null, null, userId);
        return new PageInfo<>(userBalanceList);
    }

    public PageInfo<UserBalance> pageBalanceList(Integer pageNum, Integer pageSize, Integer userId, Integer dateRangeType) {
        RecentDateMO.Period period = RecentDateMO.getPeriod(dateRangeType);

        Page<UserBalance> page = new Page<>(pageNum, pageSize);
        Page<UserBalance> userBalanceList = userBalanceMapper.getFrontPage(page, userId, period != null ? period.getStart().toDate() : null);
        return new PageInfo<>(userBalanceList);
    }

}
