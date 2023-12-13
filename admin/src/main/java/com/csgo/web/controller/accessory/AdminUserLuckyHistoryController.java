package com.csgo.web.controller.accessory;

import com.csgo.domain.user.UserLuckyHistory;
import com.csgo.service.UserLuckyHistoryService;
import com.csgo.support.PageInfo;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/luckyHistory")
@Api(tags = "后台幸运饰品历史记录")
public class AdminUserLuckyHistoryController extends BackOfficeController {

    @Autowired
    private UserLuckyHistoryService userLuckyHistoryService;


    @ApiOperation("幸运饰品历史记录")
    @RequestMapping(value = "pageList", method = RequestMethod.GET)
    @Log(desc = "幸运饰品历史记录")
    public Result pageList(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize, String keywords
            , Long start_time, Long end_time) {

        if (start_time != null) {
            start_time = start_time / 1000;
        }
        if (end_time != null) {
            end_time = end_time / 1000;
        }
        PageInfo<UserLuckyHistory> userLuckyHistoryPageInfo = userLuckyHistoryService.pageList(pageNum, pageSize, keywords, start_time, end_time);
        return new Result().result(userLuckyHistoryPageInfo);
    }


    @ApiOperation("删除接口")
    @DeleteMapping("deleteBath")
    @Log(desc = "删除幸运饰品历史记录")
    public Result deleteBath(@RequestBody List<Integer> ids) {
        userLuckyHistoryService.deleteBath(ids);
        return new Result().result(true);
    }


}
