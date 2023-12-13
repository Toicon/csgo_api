package com.csgo.service.envelop;

import com.csgo.domain.plus.envelop.RedEnvelopRecord;
import com.csgo.domain.user.User;
import com.csgo.domain.user.UserBalance;
import com.csgo.mapper.plus.envelop.RedEnvelopRecordMapper;
import com.csgo.service.user.UserBalanceService;
import com.csgo.service.user.UserService;
import com.csgo.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 2021/4/30
 */
@Service
public class RedEnvelopRecordService {
    @Autowired
    private RedEnvelopRecordMapper mapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserBalanceService userBalanceService;

    public List<RedEnvelopRecord> find(Integer userId, Integer envelopItemId, Date startTime) {
        return mapper.find(userId, envelopItemId, startTime);
    }

    public List<RedEnvelopRecord> findReceive(Integer userId, Integer envelopId, Date startTime) {
        return mapper.findReceive(userId, envelopId, startTime);
    }

    public List<RedEnvelopRecord> findRecord(Date startTime) {
        return mapper.findRecord(startTime);
    }

    /**
     * 获取红包领取数量
     *
     * @param envelopItemId
     * @return
     */
    public int getReceiveCount(Integer envelopItemId) {
        return mapper.getReceiveCount(envelopItemId);
    }

    @Transactional
    public void insert(Integer userId, BigDecimal amount, Integer envelopItemId) {
        RedEnvelopRecord record = new RedEnvelopRecord();
        record.setUserId(userId);
        record.setAmount(amount);
        record.setEnvelopItemId(envelopItemId);
        record.setCreateDate(new Date());
        mapper.insert(record);

        User user = userService.getUserById(userId);
        user.setBalance(user.getBalance().add(amount));
        user.setUpdatedAt(new Date());
        userService.update(user, user.getId());

        UserBalance userBalance = new UserBalance();
        userBalance.setAddTime(new Date());
        userBalance.setAmount(amount);
        userBalance.setType(1); //收入
        userBalance.setRemark("领取红包");
        userBalance.setCurrentAmount(user.getBalance());
        userBalance.setCurrentDiamondAmount(user.getDiamondBalance());

        userBalance.setUserId(user.getId());
        userBalance.setBalanceNumber(StringUtil.randomNumber(15));
        userBalanceService.add(userBalance);
    }
}
