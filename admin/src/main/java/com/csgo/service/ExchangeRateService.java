package com.csgo.service;

import com.csgo.domain.ExchangeRate;
import com.csgo.domain.plus.config.ExchangeRatePlus;
import com.csgo.mapper.ExchangeRateMapper;
import com.csgo.mapper.plus.config.ExchangeRatePlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExchangeRateService {

    @Autowired
    private ExchangeRateMapper mapper;
    @Autowired
    private ExchangeRatePlusMapper exchangeRatePlusMapper;

    public int add(ExchangeRate rate) {
        return mapper.insert(rate);
    }

    public ExchangeRatePlus get() {
        return exchangeRatePlusMapper.selectOne(null);
    }

    public ExchangeRate queryById(int id) {
        return mapper.selectByPrimaryKey(id);
    }

    public List<ExchangeRate> queryAll() {
        return mapper.queryAll();
    }

    public List<ExchangeRate> getList(ExchangeRate exchangeRate) {
        return mapper.getList(exchangeRate);
    }

    public int delete(int id) {
        return mapper.deleteByPrimaryKey(id);
    }

    public int update(int id, ExchangeRate rate) {
        rate.setId(id);
        return mapper.updateByPrimaryKey(rate);
    }

    @Transactional
    public void update(ExchangeRatePlus exchangeRatePlus) {
        exchangeRatePlusMapper.updateById(exchangeRatePlus);
    }

    public List<ExchangeRate> queryAllLimit(ExchangeRate exchangeRate) {
        return mapper.queryAllLimit(exchangeRate);
    }
}
