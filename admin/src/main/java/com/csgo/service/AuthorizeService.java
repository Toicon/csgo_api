package com.csgo.service;

import com.csgo.domain.plus.authorize.Authorize;
import com.csgo.mapper.plus.authorize.AuthorizeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorizeService {

    @Autowired
    private AuthorizeMapper mapper;

    public List<Authorize> query() {
        return mapper.query();
    }

    public List<Authorize> findByParent(String parentCode) {
        return mapper.findByParent(parentCode);
    }
}
