package com.csgo.mapper;

import com.csgo.domain.report.AdminUserDTO;
import com.csgo.domain.user.AdminUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AdminUser record);

    AdminUser selectByPrimaryKey(Integer id);

    List<AdminUser> selectList(AdminUser record);

    int updateByPrimaryKeySelective(AdminUser record);

    AdminUser selectOne(AdminUser user);

    /**
     * 获取管理用户列表
     *
     * @param dataScope
     * @return
     */
    List<AdminUserDTO> selectListByDataScope(String dataScope);

}