package com.csgo.web.controller;

import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.user.User;
import com.csgo.service.OrderRecordService;
import com.csgo.service.user.UserService;
import com.csgo.support.Result;
import com.csgo.util.SignUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "支付")
@RestController
@RequestMapping("/pay")
@Slf4j
@Configuration
public class PayController {
    @Autowired
    private OrderRecordService orderRecordService;
    @Autowired
    private UserService userService;

    /**
     * 生成签名信息
     *
     * @return
     */
    @ApiOperation("支付生成签名信息")
    @RequestMapping(value = "sign", method = RequestMethod.GET)
    @ResponseBody
    public String sign(HttpServletRequest request) {
        return SignUtil.sign(request);
    }

    /**
     * 通过用户id查询用户充值记录
     *
     * @return
     */
    @ApiOperation("通过用户id查询用户充值记录（前台抽奖）")
    @RequestMapping(value = "queryAllByUserid", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id"),
            @ApiImplicitParam(name = "state", value = "状态：1：支付中 2：支付成功 3：支付失败 ")
    })
    public Result queryAllByUserid(Integer userId, String state) {
        if (userId == null || userId == 0) {
            return new Result().fairResult("userId不能为空");
        }
        User user = userService.queryUserId(userId);
        if (user == null) {
            return new Result().fairResult("用户信息为空，请登录后再试");
        }
        List<OrderRecord> psss = orderRecordService.findRecharge(userId, state);
        return new Result().result(psss);
    }
}
