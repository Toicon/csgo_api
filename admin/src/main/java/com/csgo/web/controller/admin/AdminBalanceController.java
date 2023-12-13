package com.csgo.web.controller.admin;

import com.csgo.domain.user.UserBalance;
import com.csgo.service.UserBalanceService;
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
@Api(tags = "用户余额记录（后台）")
@RequestMapping("/balance")
public class AdminBalanceController extends BackOfficeController {

    @Autowired
    private UserBalanceService userBalanceService;

    @GetMapping("pageList")
    @ApiOperation("余额记录分页列表")
    @Log(desc = "查询用户余额记录")
    public Result pageList(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           String keywords, Long startTime, Long endTime) {

        if (startTime != null) {
            startTime = startTime / 1000;
        }
        if (endTime != null) {
            endTime = endTime / 1000;
        }
        PageInfo<UserBalance> userBalancePageInfo = userBalanceService.pageList(pageNum, pageSize, keywords, startTime, endTime);
        return new Result().result(userBalancePageInfo);
    }
}
