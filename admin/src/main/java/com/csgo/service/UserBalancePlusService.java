package com.csgo.service;


import com.csgo.constants.UserConstants;
import com.csgo.domain.report.UserBalanceDTO;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author huanghunbao on 2021/4/30
 */
@Service
public class UserBalancePlusService {

    @Autowired
    private UserPlusMapper mapper;
    @Autowired
    private UserMessagePlusMapper userMessagePlusMapper;

    /**
     * @param deptId
     * @param endDate
     * @return
     */
    public UserBalanceDTO countBalance(Integer deptId, String endDate) {
        UserBalanceDTO result = new UserBalanceDTO();
        //余额(银币)
        BigDecimal diamondAmount = BigDecimal.ZERO;
        //余额(V币)
        BigDecimal balanceAmount = BigDecimal.ZERO;
        //背包饰品金额(银币)
        BigDecimal knapsackAmount = BigDecimal.ZERO;
        UserBalanceDTO userBalanceDTO = mapper.countBalance(deptId, endDate);
        diamondAmount = diamondAmount.add(userBalanceDTO.getDiamondAmount());
        balanceAmount = balanceAmount.add(userBalanceDTO.getBalanceAmount());
        knapsackAmount = knapsackAmount.add(userMessagePlusMapper.countPrice(deptId, endDate));
        if (deptId.equals(UserConstants.NO_DEPART_ID)) {
            UserBalanceDTO userBalanceDTONotOwner = mapper.countBalanceNotOwner(endDate);
            diamondAmount = diamondAmount.add(userBalanceDTONotOwner.getDiamondAmount());
            balanceAmount = balanceAmount.add(userBalanceDTONotOwner.getBalanceAmount());
            knapsackAmount = knapsackAmount.add(userMessagePlusMapper.countPriceNotOwner(endDate));
        }
        result.setBalanceAmount(balanceAmount);
        result.setDiamondAmount(diamondAmount.add(knapsackAmount));
        return result;
    }

    /**
     * @param endDate
     * @return
     */
    public UserBalanceDTO countBalance(String endDate) {
        UserBalanceDTO result = new UserBalanceDTO();
        //余额(银币)
        BigDecimal diamondAmount = BigDecimal.ZERO;
        //余额(V币)
        BigDecimal balanceAmount = BigDecimal.ZERO;
        //背包饰品金额(银币)
        BigDecimal knapsackAmount = BigDecimal.ZERO;
        UserBalanceDTO userBalanceDTO = mapper.countBalanceNotOwner(endDate);
        diamondAmount = diamondAmount.add(userBalanceDTO.getDiamondAmount());
        balanceAmount = balanceAmount.add(userBalanceDTO.getBalanceAmount());
        knapsackAmount = knapsackAmount.add(userMessagePlusMapper.countPriceNotOwner(endDate));
        result.setBalanceAmount(balanceAmount);
        result.setDiamondAmount(diamondAmount.add(knapsackAmount));
        return result;
    }
}
