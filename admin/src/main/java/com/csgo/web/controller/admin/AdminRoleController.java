package com.csgo.web.controller.admin;

import com.csgo.condition.role.SearchRoleCondition;
import com.csgo.domain.plus.role.Role;
import com.csgo.domain.plus.user.AdminUserPlus;
import com.csgo.service.AdminUserService;
import com.csgo.service.AuthorizeService;
import com.csgo.service.RoleAuthorizeService;
import com.csgo.service.RoleService;
import com.csgo.support.DataConverter;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.role.EditRoleRequest;
import com.csgo.web.request.role.SearchRoleRequest;
import com.csgo.web.response.role.RoleAuthorizeResponse;
import com.csgo.web.response.role.RoleResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Api
@RequestMapping("/role")
@Slf4j
public class AdminRoleController extends BackOfficeController {

    @Autowired
    private RoleService serviceService;
    @Autowired
    private RoleAuthorizeService roleAuthorizeService;
    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private AuthorizeService authorizeService;


    /**
     * 查询所有角色
     *
     * @return
     */
    @PostMapping(value = "/pagination")
    public PageResponse<RoleResponse> pagination(@Validated @RequestBody SearchRoleRequest request) {
        SearchRoleCondition condition = DataConverter.to(SearchRoleCondition.class, request);
        return DataConverter.to(record -> {
            RoleResponse response = new RoleResponse();
            BeanUtilsEx.copyProperties(record, response);
            return response;
        }, serviceService.pagination(condition));
    }


    /**
     * 新增角色
     *
     * @param
     * @return
     */
    @PostMapping
    @Log(desc = "新增角色")
    public BaseResponse<Void> add(@Validated @RequestBody EditRoleRequest request) {
        if (serviceService.queryByName(request.getName()) != null) {
            throw new ApiException(StandardExceptionCode.ROLE_PROCESS_FAILURE, "角色已存在");
        }
        Role role = new Role();
        BeanUtilsEx.copyProperties(request, role);
        role.setCt(new Date());
        serviceService.add(role, request.getCodes());
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 编辑角色信息
     *
     * @return
     */
    @PutMapping("/{id}")
    @Log(desc = "编辑角色")
    public BaseResponse<Void> update(@PathVariable("id") int id, @Validated @RequestBody EditRoleRequest request) {
        Role role = serviceService.queryById(id);
        if (null == role) {
            throw new ApiException(StandardExceptionCode.ROLE_PROCESS_FAILURE, "角色不存在");
        }
        Role oldRole = serviceService.queryByName(request.getName());
        if (oldRole != null && !oldRole.getName().equals(role.getName())) {
            throw new ApiException(StandardExceptionCode.ROLE_PROCESS_FAILURE, "角色已存在");
        }
        BeanUtilsEx.copyProperties(request, role);
        role.setUt(new Date());
        List<AdminUserPlus> adminUsers = adminUserService.findByRoleId(id);
        if (!CollectionUtils.isEmpty(adminUsers)) {
            adminUsers.forEach(user -> {
                user.setRoleName(request.getName());
                adminUserService.update(user);
            });
        }
        serviceService.update(role, request.getCodes());
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 根据ID删除对应的角色信息
     *
     * @return
     */
    @DeleteMapping("{id}")
    @Log(desc = "删除角色")
    public BaseResponse<Void> delete(@PathVariable("id") int id) {
        serviceService.delete(id);
        return BaseResponse.<Void>builder().get();
    }

    @GetMapping("/list")
    public BaseResponse<List<RoleResponse>> list() {
        return BaseResponse.<List<RoleResponse>>builder().data(serviceService.findAll().stream().map(role -> {
            RoleResponse response = new RoleResponse();
            BeanUtilsEx.copyProperties(role, response);
            return response;
        }).collect(Collectors.toList())).get();
    }

    @GetMapping("/authorize/{roleId}")
    public BaseResponse<List<RoleAuthorizeResponse>> getAuthorize(@PathVariable("roleId") int roleId) {
        return BaseResponse.<List<RoleAuthorizeResponse>>builder().data(roleAuthorizeService.find(roleId, null).stream().map(code -> {
            RoleAuthorizeResponse response = new RoleAuthorizeResponse();
            response.setCode(code.getCode());
            response.setLast(CollectionUtils.isEmpty(authorizeService.findByParent(code.getCode())));
            return response;
        }).collect(Collectors.toList())).get();
    }

    @PostMapping("/gantDataScope/{id}")
    @Log(desc = "新增数据权限")
    public BaseResponse<Void> dataScope(@PathVariable("id") int id, @Validated @RequestBody EditRoleRequest request) {
        Role role = serviceService.queryById(id);
        if (role == null) {
            throw new ApiException(StandardExceptionCode.ROLE_PROCESS_FAILURE, "角色不存在");
        }
        role.setDataScope(request.getDataScope());
        role.setUt(new Date());
        serviceService.update(role);
        return BaseResponse.<Void>builder().get();
    }

}
