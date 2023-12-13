package com.csgo.mapper;

import com.csgo.domain.CustomerService;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerServiceMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(CustomerService record);

    int insertSelective(CustomerService record);

    CustomerService selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomerService record);

    int updateByPrimaryKey(CustomerService record);

    List<CustomerService> queryAll();

    List<CustomerService> queryAllLimit(CustomerService record);

    CustomerService selectOne(CustomerService service);
}