package com.csgo.web.controller.balance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.user.UserBalance;
import com.csgo.framework.mybatis.util.MyBatisUtils;
import com.csgo.mapper.plus.order.OrderRecordMapper;
import com.csgo.service.OrderRecordService;
import com.csgo.service.user.UserBalanceService;
import com.csgo.support.DataConverter;
import com.csgo.support.PageInfo;
import com.csgo.framework.mo.RecentDateMO;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.balance.SearchOrderRecordeRequest;
import com.csgo.web.response.pay.OrderRecordResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api
@RequireSession
@LoginRequired
@RequestMapping("/api/balance")
public class BalanceController {

    @Autowired
    private UserBalanceService userBalanceService;
    @Autowired
    private OrderRecordService orderRecordService;
    @Autowired
    private SiteContext siteContext;
    @Autowired
    private OrderRecordMapper orderRecordMapper;

    /**
     * 账本充值
     */
    @GetMapping("pageList")
    @ApiOperation("余额记录分页列表")
    public BaseResponse<PageInfo<UserBalance>> pageList(
            @RequestParam(required = false, name = "dateRangeType") Integer dateRangeType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Integer userId = siteContext.getCurrentUser().getId();

        PageInfo<UserBalance> userBalancePageInfo = userBalanceService.pageBalanceList(pageNum, pageSize, userId, dateRangeType);
        return BaseResponse.<PageInfo<UserBalance>>builder().data(userBalancePageInfo).get();
    }

    /**
     * 账本变动
     */
    @PostMapping("/pagination")
    public PageResponse<OrderRecordResponse> pagination(
            @RequestParam(required = false, name = "dateRangeType") Integer dateRangeType,
            @Valid @RequestBody SearchOrderRecordeRequest request) {
        Integer userId = siteContext.getCurrentUser().getId();

        Page<OrderRecord> frontPage = getOrderFrontPage(userId, request, dateRangeType);

        return DataConverter.to(orderRecord -> {
            OrderRecordResponse response = new OrderRecordResponse();
            BeanUtils.copyProperties(orderRecord, response);
            return response;
        }, frontPage);
    }

    private Page<OrderRecord> getOrderFrontPage(Integer userId, SearchOrderRecordeRequest request, Integer dateRangeType) {
        LambdaQueryWrapper<OrderRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderRecord::getUserId, userId);
        if (dateRangeType != null) {
            RecentDateMO.Period period = RecentDateMO.getPeriod(dateRangeType);
            if (period != null) {
                wrapper.ge(OrderRecord::getCreateTime, period.getStart().toDate());
            }
        }
        wrapper.orderByDesc(OrderRecord::getCreateTime);
        return orderRecordMapper.selectPage(MyBatisUtils.buildPage(request), wrapper);
    }

}
