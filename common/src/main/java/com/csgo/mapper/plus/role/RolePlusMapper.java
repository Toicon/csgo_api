package com.csgo.mapper.plus.role;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.role.SearchRoleCondition;
import com.csgo.domain.plus.role.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePlusMapper extends BaseMapper<Role> {

    default Page<Role> pagination(SearchRoleCondition condition) {
        LambdaUpdateWrapper<Role> wrapper = new LambdaUpdateWrapper<>();
        wrapper.orderByDesc(Role::getCt);
        return selectPage(condition.getPage(), wrapper);
    }

    default Role findByName(String name) {
        LambdaUpdateWrapper<Role> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Role::getName, name);
        return selectOne(wrapper);
    }
}

