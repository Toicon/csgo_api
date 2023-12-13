package com.csgo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.user.SearchAdminUserCondition;
import com.csgo.constants.DataScopeConstants;
import com.csgo.constants.UserConstants;
import com.csgo.domain.SysDept;
import com.csgo.domain.plus.role.Role;
import com.csgo.domain.plus.anchor.AdminUserAnchorPlus;
import com.csgo.domain.plus.user.AdminUserPlus;
import com.csgo.domain.report.AdminUserDTO;
import com.csgo.domain.user.AdminUser;
import com.csgo.exception.ServiceErrorException;
import com.csgo.mapper.AdminUserMapper;
import com.csgo.mapper.plus.anchor.AdminUserAnchorPlusMapper;
import com.csgo.mapper.plus.user.AdminUserPlusMapper;
import com.csgo.service.dept.SysDeptService;
import com.csgo.support.DataConverter;
import com.csgo.util.BeanUtilsEx;
import com.csgo.util.StringUtil;
import com.csgo.web.response.user.AdminUserResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.web.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
public class AdminUserService {

    @Autowired
    private AdminUserMapper userMapper;
    @Autowired
    private AdminUserPlusMapper adminUserPlusMapper;

    @Autowired
    private SysDeptService deptService;

    @Autowired
    private RoleService roleService;

    @Autowired
    protected SiteContext siteContext;

    @Autowired
    private AdminUserAnchorPlusMapper adminUserAnchorPlusMapper;

    @Transactional
    public void insert(AdminUserPlus adminUser) {
        adminUserPlusMapper.insert(adminUser);
    }

    @Transactional
    public void update(AdminUserPlus user) {
        adminUserPlusMapper.updateById(user);
    }

    public int update(AdminUser user, int id) {
        user.setId(id);
        return userMapper.updateByPrimaryKeySelective(user);
    }

    public List<AdminUser> selectList(AdminUser adminUser) {
        return userMapper.selectList(adminUser);
    }

    public PageResponse<AdminUserResponse> pagination(SearchAdminUserCondition condition) {
        Page<AdminUserPlus> pagination = adminUserPlusMapper.pagination(condition);
        //获取部门id集合
        Set<Integer> deptIds = pagination.getRecords().stream().map(AdminUserPlus::getDeptId).collect(toSet());
        List<SysDept> deptList = new ArrayList<>();
        if (deptIds != null && deptIds.size() > 0) {
            deptList.addAll(deptService.selectDeptListInDeptIds(deptIds.toArray(new Integer[0])));
        }
        return DataConverter.to(user -> {
            AdminUserResponse response = new AdminUserResponse();
            BeanUtilsEx.copyProperties(user, response);
            if (user.getDeptId() != null) {
                SysDept dept = deptList.stream().filter(sysDept -> user.getDeptId().equals(sysDept.getId())).findFirst().orElse(null);
                if (dept != null) {
                    response.setDeptName(dept.getDeptName());
                }
            }
            return response;
        }, pagination);
    }

    public List<AdminUserPlus> findByRoleId(int roleId) {
        return adminUserPlusMapper.findByRoleId(roleId);
    }

    @Transactional
    public void delete(int id) {
        if (id == UserConstants.NO_DEPART_ADMIN_USER_ID) {
            throw new ServiceErrorException("未归属主播账号不能删除");
        }
        //删除用户则修改主播关系为未归属部门主播id(后台管理账号)
        this.updateAdminUserRelationAnchor(id);
        adminUserPlusMapper.deleteById(id);
    }

    public AdminUserPlus get(String username, String password) {
        return adminUserPlusMapper.get(username, password);
    }

    public AdminUserPlus getByUserName(String username) {
        return adminUserPlusMapper.getByUserName(username);
    }

    public AdminUserPlus get(int id) {
        return adminUserPlusMapper.selectById(id);
    }

    /**
     * 数据范围过滤
     *
     * @param deptAlias 部门别名
     * @return
     */
    public String getUserDataScope(String deptAlias) {
        Integer userId = siteContext.getCurrentUser().getId();
        AdminUserPlus adminUserPlus = adminUserPlusMapper.selectById(userId);
        if (adminUserPlus == null) {
            throw new ServiceErrorException("获取用户信息失败");
        }
        Integer roleId = adminUserPlus.getRoleId();
        Integer deptId = adminUserPlus.getDeptId();
        Role role = roleService.queryById(roleId);
        String dataScope = role.getDataScope();
        StringBuilder sqlString = new StringBuilder();
        if (DataScopeConstants.DATA_SCOPE_ALL.equals(dataScope)) {
            sqlString = new StringBuilder();
        } else if (DataScopeConstants.DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
            sqlString.append(StringUtil.format(
                    " AND {}.dept_id IN ( SELECT id FROM sys_dept WHERE id = {} or find_in_set( {} , ancestors ) )",
                    deptAlias, deptId, deptId));
        } else {
            sqlString.append(StringUtil.format(" AND {}.dept_id = 0", deptAlias));
        }
        return sqlString.toString();
    }

    /**
     * 数据范围过滤(没数据权限则返回当前部门)
     *
     * @param deptAlias 部门别名
     * @return
     */
    public String getUserDataScopeAndSelf(String deptAlias) {
        Integer userId = siteContext.getCurrentUser().getId();
        AdminUserPlus adminUserPlus = adminUserPlusMapper.selectById(userId);
        if (adminUserPlus == null) {
            throw new ServiceErrorException("获取用户信息失败");
        }
        Integer roleId = adminUserPlus.getRoleId();
        Integer deptId = adminUserPlus.getDeptId();
        Role role = roleService.queryById(roleId);
        String dataScope = role.getDataScope();
        StringBuilder sqlString = new StringBuilder();
        if (DataScopeConstants.DATA_SCOPE_ALL.equals(dataScope)) {
            sqlString = new StringBuilder();
        } else if (DataScopeConstants.DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
            sqlString.append(StringUtil.format(
                    " AND {}.dept_id IN ( SELECT id FROM sys_dept WHERE id = {} or find_in_set( {} , ancestors ) )",
                    deptAlias, deptId, deptId));
        } else {
            sqlString.append(StringUtil.format(" AND {}.dept_id = {}", deptAlias, deptId));
        }
        return sqlString.toString();
    }

    /**
     * 获取管理用户列表
     *
     * @return
     */
    public List<AdminUserDTO> selectListByDataScope() {
        String dataScope = getUserDataScope("adminUser");
        return userMapper.selectListByDataScope(dataScope);
    }

    /**
     * 修改后台管理账号
     *
     * @param oldAdminUserId
     */
    private void updateAdminUserRelationAnchor(Integer oldAdminUserId) {
        if (oldAdminUserId == null) {
            return;
        }
        Integer newAdminUserId = UserConstants.NO_DEPART_ADMIN_USER_ID;
        LambdaQueryWrapper<AdminUserAnchorPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUserAnchorPlus::getAdminUserId, oldAdminUserId);
        List<AdminUserAnchorPlus> adminUserAnchorPlusList = adminUserAnchorPlusMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(adminUserAnchorPlusList)) {
            return;
        } else {
            for (AdminUserAnchorPlus adminUserAnchorPlus : adminUserAnchorPlusList) {
                adminUserAnchorPlus.setAdminUserId(newAdminUserId);
                adminUserAnchorPlus.setUt(new Date());
                adminUserAnchorPlusMapper.updateById(adminUserAnchorPlus);
            }
        }
    }
}
