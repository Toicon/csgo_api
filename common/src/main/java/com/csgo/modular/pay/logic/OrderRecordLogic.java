package com.csgo.modular.pay.logic;

import com.csgo.mapper.plus.order.OrderRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRecordLogic {

    private final OrderRecordMapper orderRecordMapper;

    public BigDecimal getSumSuccessOrderAmount(Integer userId) {
        return orderRecordMapper.getSumSuccessOrderAmount(userId, null, null);
    }

}
