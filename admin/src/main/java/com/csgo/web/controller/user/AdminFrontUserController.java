package com.csgo.web.controller.user;


import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.User;
import com.csgo.modular.user.enums.UserStatusEnums;
import com.csgo.modular.user.logic.UserLogic;
import com.csgo.service.UserService;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
@Slf4j
public class AdminFrontUserController extends BackOfficeController {

    @Autowired
    private UserService service;
    @Autowired
    private UserLogic userLogic;

    /**
     * 根据用户ID查询到对应的用户信息
     *
     * @return
     */
    @ApiOperation("根据用户ID查询到对应的用户信息（后台管理）")
    @RequestMapping(value = "queryByid", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id")
    })
    public Result queryUserId(int id) {
        User user = service.queryUserId(id);
        return new Result().result(user);
    }

    /**
     * 通过邀请码查询出用户列表
     *
     * @return
     */
    @ApiOperation("通过邀请码查询出用户列表（后台管理）")
    @RequestMapping(value = "queryAllByCode", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "邀请码")
    })
    public Result queryAllByCode(String code) {
        User user = new User();
        if (code != null && !code.equals("")) {
            user.setExtensionCode(code);
        }
        List<User> list = service.selectList(user);
        return new Result().result(list);
    }

    @PutMapping("/account/status/{userId}")
    @Log(desc = "更改用户状态")
    public BaseResponse<Void> status(@PathVariable("userId") Integer userId, @RequestParam("status") Integer status) {
        userLogic.changeStatus(userId, status);
        return BaseResponse.<Void>builder().get();
    }

}
