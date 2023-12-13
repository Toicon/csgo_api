package com.csgo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.role.SearchRoleCondition;
import com.csgo.domain.plus.role.Role;
import com.csgo.domain.plus.role.RoleAuthorize;
import com.csgo.domain.plus.user.AdminUserPlus;
import com.csgo.mapper.plus.role.RoleAuthorizePlusMapper;
import com.csgo.mapper.plus.role.RolePlusMapper;
import com.csgo.mapper.plus.user.AdminUserPlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RolePlusMapper mapper;
    @Autowired
    private RoleAuthorizePlusMapper roleAuthorizePlusMapper;
    @Autowired
    private AdminUserPlusMapper adminUserPlusMapper;

    public Page<Role> pagination(SearchRoleCondition condition) {
        return mapper.pagination(condition);
    }


    @Transactional
    public void add(Role role, List<String> codes) {
        mapper.insert(role);
        codes.forEach(code -> {
            RoleAuthorize roleAuthorize = new RoleAuthorize();
            roleAuthorize.setCode(code);
            roleAuthorize.setRoleId(role.getId());
            roleAuthorizePlusMapper.insert(roleAuthorize);
        });
    }

    @Transactional
    public void update(Role role, List<String> codes) {
        mapper.updateById(role);
        roleAuthorizePlusMapper.deleteByRoleId(role.getId());

        codes.forEach(code -> {
            RoleAuthorize roleAuthorize = new RoleAuthorize();
            roleAuthorize.setCode(code);
            roleAuthorize.setRoleId(role.getId());
            roleAuthorizePlusMapper.insert(roleAuthorize);
        });
    }

    @Transactional
    public void update(Role role) {
        mapper.updateById(role);
    }

    public Role queryById(int id) {
        return mapper.selectById(id);
    }

    @Transactional
    public void delete(int id) {
        List<AdminUserPlus> adminUserList = adminUserPlusMapper.findByRoleId(id);
        adminUserList.forEach(adminUserPlus -> {
            adminUserPlus.setRoleId(null);
            adminUserPlus.setRoleName("");
            adminUserPlusMapper.updateById(adminUserPlus);
        });
        mapper.deleteById(id);
        roleAuthorizePlusMapper.deleteByRoleId(id);
    }

    public Role queryByName(String name) {
        return mapper.findByName(name);
    }

    public List<Role> findAll() {
        return mapper.selectList(null);
    }
}
