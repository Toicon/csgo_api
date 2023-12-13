package com.csgo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.UserBalance;
import com.csgo.mapper.UserBalanceMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.support.PageInfo;
import com.csgo.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class UserBalanceService {

    @Autowired
    private UserBalanceMapper userBalanceMapper;
    @Autowired
    private UserPlusMapper userPlusMapper;

    @Transactional
    public void addUserBalance(UserPlus user, BigDecimal amount, String remark) {
        UserBalance userBalance = new UserBalance();
        userBalance.setAddTime(new Date());
        userBalance.setAmount(amount);
        userBalance.setType(1); //收入
        userBalance.setRemark(remark);
        userBalance.setCurrentAmount(user.getBalance());
        userBalance.setUserId(user.getId());
        userBalance.setBalanceNumber(StringUtil.randomNumber(15));
        userBalanceMapper.add(userBalance);
    }
    
    @Transactional
    public void add(UserPlus user, BigDecimal amount, String remark) {
        user.setBalance(user.getBalance().add(amount));
        user.setUpdatedAt(new Date());
        userPlusMapper.updateById(user);

        UserBalance userBalance = new UserBalance();
        userBalance.setAddTime(new Date());
        userBalance.setAmount(amount);
        userBalance.setType(1); //收入
        userBalance.setRemark(remark);
        userBalance.setCurrentAmount(user.getBalance());
        userBalance.setUserId(user.getId());
        userBalance.setBalanceNumber(StringUtil.randomNumber(15));
        userBalanceMapper.add(userBalance);
    }


    @Transactional
    public int add(UserBalance userBalance) {
        return userBalanceMapper.add(userBalance);
    }

    public PageInfo<UserBalance> pageList(Integer pageNum, Integer pageSize, String keywords, Long startTime, Long endTime) {
        Page<UserBalance> page = new Page<>(pageNum, pageSize);
        Page<UserBalance> userBalanceList = userBalanceMapper.pageList(page, keywords, startTime, endTime, null);
        return new PageInfo<>(userBalanceList);
    }
}
