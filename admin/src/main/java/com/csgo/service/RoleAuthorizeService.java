package com.csgo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.role.SearchRoleAuthorizeCondition;
import com.csgo.domain.plus.role.RoleAuthorize;
import com.csgo.domain.plus.role.RoleAuthorizeDTO;
import com.csgo.mapper.plus.role.RoleAuthorizePlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleAuthorizeService {

    @Autowired
    private RoleAuthorizePlusMapper mapper;

    public Page<RoleAuthorizeDTO> pagination(SearchRoleAuthorizeCondition condition) {
        return mapper.pagination(condition.getPage(), condition);
    }

    @Transactional
    public int insert(RoleAuthorize roleAuthorize) {
        return mapper.insert(roleAuthorize);
    }

    public List<RoleAuthorize> find(Integer roleId, String code) {
        return mapper.find(roleId, code);
    }

    @Transactional
    public int delete(int id) {
        return mapper.deleteById(id);
    }
}
