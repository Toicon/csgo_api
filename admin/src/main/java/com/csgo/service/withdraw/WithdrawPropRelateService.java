package com.csgo.service.withdraw;

import com.csgo.domain.plus.withdraw.WithdrawPropRelate;
import com.csgo.domain.plus.withdraw.WithdrawPropRelateDTO;
import com.csgo.mapper.plus.withdraw.WithdrawPropRelateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 2021/5/22
 */
@Service
public class WithdrawPropRelateService {
    @Autowired
    private WithdrawPropRelateMapper relateMapper;

    public List<WithdrawPropRelate> findByPropId(Integer propId) {
        return relateMapper.findRelateByPropId(propId);
    }

    public WithdrawPropRelateDTO findWithdrawAll(String userName, Integer flag, Date startDate, Date endDate) {
        return relateMapper.findWithdrawAll(userName, flag, startDate, endDate);
    }
}
