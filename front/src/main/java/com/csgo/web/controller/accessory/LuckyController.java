package com.csgo.web.controller.accessory;


import com.csgo.domain.user.UserLuckyHistory;
import com.csgo.service.UserLuckyHistoryService;
import com.csgo.support.PageInfo;
import com.csgo.support.Result;
import com.csgo.web.intecepter.LoginRequired;
import com.echo.framework.platform.interceptor.session.RequireSession;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@Api("幸运饰品")
@LoginRequired
@RequireSession
@RequestMapping("/api/lucky")
public class LuckyController {

    @Autowired
    private UserLuckyHistoryService userLuckyHistoryService;

    @ApiOperation("幸运饰品历史记录")
    @GetMapping("pageLuckyHistoryList")
    public Result pageList(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam("userId") Integer userId) {
        PageInfo<UserLuckyHistory> userLuckyHistoryPageInfo = userLuckyHistoryService.pageListByUser(pageNum, pageSize, userId);
        return new Result().result(userLuckyHistoryPageInfo);
    }

}
