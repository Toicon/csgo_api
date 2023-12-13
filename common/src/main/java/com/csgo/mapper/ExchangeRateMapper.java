package com.csgo.mapper;

import com.csgo.domain.ExchangeRate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRateMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ExchangeRate record);

    int insertSelective(ExchangeRate record);

    ExchangeRate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ExchangeRate record);

    int updateByPrimaryKey(ExchangeRate record);

    List<ExchangeRate> queryAll();

    List<ExchangeRate> getList(ExchangeRate record);

    List<ExchangeRate> queryAllLimit(ExchangeRate record);

    ExchangeRate getById(@Param("id") Integer id);
}