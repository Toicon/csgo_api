package com.csgo.web.controller.admin;

import com.csgo.domain.user.UserCommissionLog;
import com.csgo.service.UserCommissionLogService;
import com.csgo.support.PageInfo;
import com.csgo.support.Result;
import com.csgo.web.support.Log;
import com.csgo.web.controller.BackOfficeController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "用户分佣记录(后台)")
@RequestMapping("/commission")
public class AdminCommissionController extends BackOfficeController {

    @Autowired
    private UserCommissionLogService userCommissionLogService;

    @GetMapping("pageList")
    @ApiOperation("佣金记录分页列表")
    @Log(desc = "查询用户分佣记录")
    public Result pageList(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           String keywords, Long startTime, Long endTime) {

        if (startTime != null) {
            startTime = startTime / 1000;
        }
        if (endTime != null) {
            endTime = endTime / 1000;
        }
        PageInfo<UserCommissionLog> userCommissionLogPageInfo = userCommissionLogService.pageList(pageNum, pageSize, keywords, startTime, endTime);
        return new Result().result(userCommissionLogPageInfo);
    }
}
