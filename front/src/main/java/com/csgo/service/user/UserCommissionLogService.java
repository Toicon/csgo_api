package com.csgo.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.user.SearchUserCommissionLogCondition;
import com.csgo.domain.plus.user.UserCommissionLogDTO;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.UserBalance;
import com.csgo.domain.user.UserCommissionLog;
import com.csgo.mapper.UserBalanceMapper;
import com.csgo.mapper.UserCommissionLogMapper;
import com.csgo.mapper.UserMapper;
import com.csgo.mapper.plus.order.OrderRecordMapper;
import com.csgo.mapper.plus.user.UserCommissionLogPlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserCommissionLogService {

    @Autowired
    private UserCommissionLogMapper commissionLogMapper;
    @Autowired
    private UserCommissionLogPlusMapper commissionLogPlusMapper;
    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserBalanceMapper userBalanceMapper;
    @Autowired
    private OrderRecordMapper orderRecordMapper;

    public Page<UserCommissionLogDTO> pagination(SearchUserCommissionLogCondition condition) {
        return commissionLogPlusMapper.pagination(condition.getPage(), condition);
    }

    public Map<String, Object> getPromotionCenter(Integer userId) {
        Map<String, Object> map = new HashMap<>();
        int count = userPlusMapper.countRecommend(userId);
        map.put("count", count);

        int rechargeCount = orderRecordMapper.getRechargeCount(userId);
        map.put("rechargeCount", rechargeCount);

        UserCommissionLog userCommissionLog = commissionLogMapper.getRecommendCommission(userId);
        map.put("userCommissionLog", userCommissionLog);

        return map;
    }

    public List<UserCommissionLog> getUserCommissionAmount(Integer userId, String date) {
        return commissionLogMapper.getUserCommissionAmount(userId, date);
    }

    @Transactional
    public void updateStatusByDateAndUserId(UserPlus user, BigDecimal reduce, String date) {
        addUserBalance(user, reduce);
        commissionLogMapper.updateStatusByDateAndUserId(user.getId(), date);
    }

    @Transactional
    public void updateCommissionUserId(int userId, int commissionUserId) {
        commissionLogMapper.updateCommissionUserId(commissionUserId, userId);
        //修改用户未挂推广码并且成功支付订单
        orderRecordMapper.updateParentIdByUserId(userId,commissionUserId);
    }

    private void addUserBalance(UserPlus user, BigDecimal amount) {
        user.setBalance(user.getBalance().add(amount));
        user.setUpdatedAt(new Date());
        userPlusMapper.updateById(user);

        UserBalance userBalance = new UserBalance();
        userBalance.setAddTime(new Date());
        userBalance.setAmount(amount);
        userBalance.setType(1); //收入
        userBalance.setRemark("领取推广费用");
        userBalance.setCurrentAmount(user.getBalance());
        userBalance.setUserId(user.getId());
        userBalance.setBalanceNumber(StringUtil.randomNumber(15));
        userBalanceMapper.add(userBalance);
    }
}
