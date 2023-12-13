package com.csgo.web.controller;

import com.csgo.domain.ExchangeRate;
import com.csgo.service.ExchangeRateService;
import com.csgo.support.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Api(tags = "汇率")
@RestController
@RequestMapping("/rate")
@Slf4j
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService service;

    /**
     * 根据ID查询到对应的汇率信息
     *
     * @return
     */
    @ApiOperation("根据ID查询到对应的汇率信息（后台管理）")
    @RequestMapping(value = "queryById", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "汇率id")
    })
    public Result queryById(@RequestParam int id) {
        ExchangeRate exchangeRate = service.queryById(id);
        return new Result().result(exchangeRate);
    }

    /**
     * 查询所有汇率信息
     *
     * @return
     */
    @ApiOperation("查询所有汇率信息（后台管理）")
    @RequestMapping(value = "queryAll", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页多少条数据")
    })
    public Result queryAll(Integer pageNum, Integer pageSize) {
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (pageNum == null || pageNum == 0) {
            pageNum = 1;
        }
        int p = pageNum;
        pageNum = (pageNum - 1) * pageSize;

        ExchangeRate exr = new ExchangeRate();
        exr.setPageNum(pageNum);
        exr.setPageSize(pageSize);
        List<ExchangeRate> list = service.queryAllLimit(exr);
        if (list != null && list.size() > 0) {
            list.get(0).setTotal(service.queryAll().size());
        }
        return new Result().result(list);
    }

}
