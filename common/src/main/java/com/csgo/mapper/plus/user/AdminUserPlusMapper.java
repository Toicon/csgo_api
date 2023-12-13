package com.csgo.mapper.plus.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.user.SearchAdminUserCondition;
import com.csgo.domain.plus.user.AdminUserPlus;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface AdminUserPlusMapper extends BaseMapper<AdminUserPlus> {

    default Page<AdminUserPlus> pagination(SearchAdminUserCondition condition) {
        LambdaQueryWrapper<AdminUserPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AdminUserPlus::getCt);
        return selectPage(condition.getPage(), wrapper);
    }

    default AdminUserPlus getByUserName(String username) {
        LambdaQueryWrapper<AdminUserPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUserPlus::getUsername, username);
        return selectOne(wrapper);
    }

    default AdminUserPlus get(String username, String password) {
        LambdaQueryWrapper<AdminUserPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUserPlus::getUsername, username);
        wrapper.eq(AdminUserPlus::getPassword, password);
        return selectOne(wrapper);
    }

    default List<AdminUserPlus> findByRoleId(int roleId) {
        LambdaQueryWrapper<AdminUserPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUserPlus::getRoleId, roleId);
        return selectList(wrapper);
    }
}
