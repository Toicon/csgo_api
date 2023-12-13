package com.csgo.web.controller.admin;

import com.csgo.condition.user.SearchAdminUserCondition;
import com.csgo.domain.plus.role.Role;
import com.csgo.domain.plus.user.AdminUserPlus;
import com.csgo.service.AdminUserService;
import com.csgo.service.RoleService;
import com.csgo.support.DataConverter;
import com.csgo.support.StandardExceptionCode;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.user.EditAdminUserRequest;
import com.csgo.web.request.user.FrozenAdminUserRequest;
import com.csgo.web.request.user.SearchAdminUserRequest;
import com.csgo.web.response.user.AdminUserResponse;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

@Api
@RequestMapping("/admin")
public class AdminUserController extends BackOfficeController {

    @Autowired
    private AdminUserService service;
    @Autowired
    private RoleService roleService;


    @PostMapping("/{id}/frozen")
    public BaseResponse<Void> frozen(@PathVariable("id") int id, @Valid @RequestBody FrozenAdminUserRequest request) {
        AdminUserPlus adminUser = service.get(id);
        adminUser.setFrozen(request.isFrozen());
        adminUser.setUt(new Date());
        service.update(adminUser);
        return BaseResponse.<Void>builder().get();
    }

    @PostMapping("/pagination")
    public PageResponse<AdminUserResponse> pagination(@Valid @RequestBody SearchAdminUserRequest request) {
        SearchAdminUserCondition condition = DataConverter.to(SearchAdminUserCondition.class, request);
        return service.pagination(condition);
    }

    @PostMapping
    public BaseResponse<Void> add(@Valid @RequestBody EditAdminUserRequest request) {
        if (StringUtils.isEmpty(request.getPassword())) {
            throw new ApiException(StandardExceptionCode.ADD_ADMIN_USER_FAILURE, "密码不能为空");
        }
        if (StringUtils.isEmpty(request.getUsername())) {
            throw new ApiException(StandardExceptionCode.ADD_ADMIN_USER_FAILURE, "用户名不能为空");
        }
        if (StringUtils.isEmpty(request.getName())) {
            throw new ApiException(StandardExceptionCode.ADD_ADMIN_USER_FAILURE, "昵称不能为空");
        }
        if (null == request.getDeptId()) {
            throw new ApiException(StandardExceptionCode.ADD_ADMIN_USER_FAILURE, "部门不能为空");
        }
        if (null == request.getUserType()) {
            throw new ApiException(StandardExceptionCode.ADD_ADMIN_USER_FAILURE, "账号类型不能为空");
        }
        if (null == request.getRoleId()) {
            throw new ApiException(StandardExceptionCode.ADD_ADMIN_USER_FAILURE, "角色不能为空");
        }
        AdminUserPlus exists = service.getByUserName(request.getUsername());
        if (exists != null) {
            throw new ApiException(StandardExceptionCode.ADD_ADMIN_USER_FAILURE, "该用户名已存在");
        }
        AdminUserPlus adminUser = new AdminUserPlus();
        adminUser.setName(request.getName());
        adminUser.setPassword(request.getPassword());
        adminUser.setRoleId(request.getRoleId());
        adminUser.setUserType(request.getUserType());
        adminUser.setUsername(request.getUsername());
        adminUser.setPhone(request.getPhone());
        adminUser.setCapRestrictions(request.getCapRestrictions());
        adminUser.setDeptId(request.getDeptId());
        Role role = roleService.queryById(request.getRoleId());
        if (null != role) {
            adminUser.setRoleName(role.getName());
        }
        adminUser.setCt(new Date());
        service.insert(adminUser);
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("/{id}")
    public BaseResponse<Void> update(@PathVariable("id") int id, @Valid @RequestBody EditAdminUserRequest request) {
        if (null == request.getDeptId()) {
            throw new ApiException(StandardExceptionCode.ADD_ADMIN_USER_FAILURE, "部门不能为空");
        }
        //判断修改用户名称是否存在
        AdminUserPlus exists = service.getByUserName(request.getUsername());
        if (exists != null && exists.getId().intValue() != id) {
            throw new ApiException(StandardExceptionCode.ADD_ADMIN_USER_FAILURE, "该用户名已存在");
        }
        AdminUserPlus adminUser = service.get(id);
        adminUser.setName(request.getName());
        adminUser.setUserType(request.getUserType());
        adminUser.setPassword(request.getPassword());
        adminUser.setUsername(request.getUsername());
        adminUser.setPhone(request.getPhone());
        Role role = roleService.queryById(request.getRoleId());
        if (null != role) {
            adminUser.setRoleName(role.getName());
        }
        adminUser.setRoleId(request.getRoleId());
        adminUser.setCapRestrictions(request.getCapRestrictions());
        adminUser.setUt(new Date());
        adminUser.setDeptId(request.getDeptId());
        service.update(adminUser);
        return BaseResponse.<Void>builder().get();
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> delete(@PathVariable("id") int id) {
        service.delete(id);
        return BaseResponse.<Void>builder().get();
    }

}
