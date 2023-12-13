package com.csgo.web.controller.log;

import com.csgo.condition.user.SearchUserLoginRecordCondition;
import com.csgo.service.user.UserLoginRecordService;
import com.csgo.support.DataConverter;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.log.SearchUserLoginRecordRequest;
import com.csgo.web.response.log.UserLoginRecordResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.PageResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * 用户登录日志
 *
 * @author admin
 */
@Api
@RequestMapping("/log/login")
public class AdminLoginController extends BackOfficeController {

    @Autowired
    private UserLoginRecordService userLoginRecordService;

    @PostMapping("/pagination")
    @Log(desc = "分页查询用户登录日志")
    public PageResponse<UserLoginRecordResponse> luckyProductPagination(@Valid @RequestBody SearchUserLoginRecordRequest request) {
        SearchUserLoginRecordCondition condition = DataConverter.to(SearchUserLoginRecordCondition.class, request);
        return DataConverter.to(record -> {
            UserLoginRecordResponse response = new UserLoginRecordResponse();
            BeanUtils.copyProperties(record, response);
            return response;
        }, userLoginRecordService.pagination(condition));
    }

}
