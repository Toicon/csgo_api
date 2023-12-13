package com.csgo.mapper.plus.role;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.role.SearchRoleAuthorizeCondition;
import com.csgo.domain.plus.role.RoleAuthorize;
import com.csgo.domain.plus.role.RoleAuthorizeDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public interface RoleAuthorizePlusMapper extends BaseMapper<RoleAuthorize> {

    Page<RoleAuthorizeDTO> pagination(IPage<RoleAuthorize> page, @Param("condition") SearchRoleAuthorizeCondition condition);

    default int updateByPrimaryKey(RoleAuthorize record) {
        LambdaUpdateWrapper<RoleAuthorize> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(RoleAuthorize::getId, record.getId()).set(RoleAuthorize::getCode, record.getCode()).set(RoleAuthorize::getRoleId, record.getRoleId());
        return update(record, wrapper);
    }

    default List<RoleAuthorize> find(Integer roleId, String code) {
        LambdaQueryWrapper<RoleAuthorize> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(code)) {
            wrapper.eq(RoleAuthorize::getCode, code);
        }
        if (roleId != null) {
            wrapper.eq(RoleAuthorize::getRoleId, roleId);
        }
        return selectList(wrapper);
    }

    default void deleteByRoleId(int roleId) {
        LambdaQueryWrapper<RoleAuthorize> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleAuthorize::getRoleId, roleId);
        delete(wrapper);
    }
}

