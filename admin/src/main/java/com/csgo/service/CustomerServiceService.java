package com.csgo.service;

import com.csgo.domain.CustomerService;
import com.csgo.mapper.CustomerServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceService {

    @Autowired
    private CustomerServiceMapper mapper;


    public int add(CustomerService service) {
        int num = mapper.insert(service);
        return num;
    }

    public int update(CustomerService service, int id) {
        service.setId(id);
        return mapper.updateByPrimaryKey(service);
    }

    public CustomerService queryById(int id) {
        return mapper.selectByPrimaryKey(id);
    }

    public List<CustomerService> queryAll() {
        return mapper.queryAll();
    }

    public List<CustomerService> queryAllLimit(CustomerService xx) {
        return mapper.queryAllLimit(xx);
    }

    public int delete(int id) {
        return mapper.deleteByPrimaryKey(id);
    }

    public CustomerService queryByIdNow() {
        CustomerService customerService = new CustomerService();
        customerService.setStatus("0");
        return mapper.selectOne(customerService);
    }
}
