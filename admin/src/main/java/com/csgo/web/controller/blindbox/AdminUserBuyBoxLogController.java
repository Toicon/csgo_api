package com.csgo.web.controller.blindbox;

import com.csgo.domain.user.UserBuyBoxLog;
import com.csgo.service.UserBuyBoxLogService;
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
@RequestMapping("/buy/box")
@Api(tags = "后台盲盒房间")
public class AdminUserBuyBoxLogController extends BackOfficeController {

    @Autowired
    private UserBuyBoxLogService userBuyBoxLogService;


    @ApiOperation("购买盲盒记录（后台管理）")
    @RequestMapping(value = "pageList", method = RequestMethod.GET)
    @Log(desc = "购买盲盒记录列表")
    public Result pageList(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize, String keywords
            , Long start_time, Long end_time) {

        if (start_time != null) {
            start_time = start_time / 1000;
        }
        if (end_time != null) {
            end_time = end_time / 1000;
        }
        PageInfo<UserBuyBoxLog> blindBoxList = userBuyBoxLogService.pageList(pageNum, pageSize, keywords, start_time, end_time);
        return new Result().result(blindBoxList);
    }


    @ApiOperation("删除接口")
    @DeleteMapping("deleteBath")
    @Log(desc = "删除购买盲盒记录")
    public Result deleteBath(@RequestBody List<Integer> ids) {
        userBuyBoxLogService.deleteBath(ids);
        return new Result().result(true);
    }


}
