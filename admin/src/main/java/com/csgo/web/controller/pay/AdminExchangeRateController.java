package com.csgo.web.controller.pay;

import com.csgo.domain.ExchangeRate;
import com.csgo.service.ExchangeRateService;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
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
public class AdminExchangeRateController extends BackOfficeController {

    @Autowired
    private ExchangeRateService service;

    /**
     * 新增汇率信息
     *
     * @param
     * @return
     */
    @ApiOperation("新增汇率信息（后台管理）")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "exchangeRate", value = "汇率")
    })
    @Log(desc = "新增汇率")
    public Result add(@RequestParam String exchangeRate) {
        ExchangeRate rate = new ExchangeRate();
        rate.setExchangeRate(exchangeRate);
        rate.setCt(new Date());
        rate.setFlag(0);
        int num = service.add(rate);
        return new Result().result(num);
    }

    /**
     * 编辑汇率信息
     *
     * @return
     */
    @ApiOperation("编辑汇率信息（后台管理）")
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "汇率ID"),
            @ApiImplicitParam(name = "exchangeRate", value = "汇率"),
            @ApiImplicitParam(name = "flag", value = "是否启用"),
            @ApiImplicitParam(name = "extractMoney", value = "提取金额"),
            @ApiImplicitParam(name = "upsAndDowns", value = "提取zbk的最大金额的浮动"),
            @ApiImplicitParam(name = "pay_give_money", value = "支付赠送初始金额"),
            @ApiImplicitParam(name = "spill_price", value = "溢值"),
            @ApiImplicitParam(name = "firstCommission", value = "一级佣金比例"),
            @ApiImplicitParam(name = "secondCommission", value = "二级佣金比例"),
            @ApiImplicitParam(name = "luckyValue", value = "幸运值"),
    })
    @Log(desc = "修改汇率")
    public Result update(@RequestParam int id, int flag, String exchangeRate,
                         BigDecimal extractMoney, String upsAndDowns, BigDecimal pay_give_money,
                         BigDecimal spill_price, BigDecimal firstCommission, BigDecimal secondCommission, Integer luckyValue) {
//        if(flag == 1){
//            ExchangeRate rate = service.queryFlag();
//            if(rate != null ){
//                return new Result().fairResult("启用的汇率只能存在一个");
//            }
//        }
        ExchangeRate rate = service.queryById(id);
        if (!StringUtils.isEmpty(flag)) {
            rate.setFlag(flag);
        }
        if (!StringUtils.isEmpty(exchangeRate)) {
            rate.setExchangeRate(exchangeRate);
        }
        if (!StringUtils.isEmpty(extractMoney)) {
            rate.setExtractMoney(extractMoney);
        }
        if (!StringUtils.isEmpty(upsAndDowns)) {
            rate.setUpsAndDowns(upsAndDowns);
        }
        if (!StringUtils.isEmpty(pay_give_money)) {
            rate.setPay_give_money(pay_give_money);
        }
        if (!StringUtils.isEmpty(spill_price)) {
            rate.setSpill_price(spill_price);
        }
        if (firstCommission != null) {
            rate.setFirstCommission(firstCommission);
        }
        if (secondCommission != null) {
            rate.setSecondCommission(secondCommission);
        }
        if (luckyValue != null) {
            rate.setLuckyValue(luckyValue);
        }
        rate.setFlag(1);
        rate.setUt(new Date());
        int num = service.update(id, rate);
        return new Result().result(num);
    }

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
    @Log(desc = "根据ID查询汇率")
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
    @Log(desc = "查询汇率列表")
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

    /**
     * 根据ID删除对应的汇率信息
     *
     * @return
     */
    @ApiOperation("根据ID删除对应的汇率信息（后台管理）")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "汇率id")
    })
    @Log(desc = "删除汇率")
    public Result delete(@RequestParam int id) {
        ExchangeRate exchangeRate = service.queryById(id);
        if (exchangeRate.getFlag() == 1) {
            return new Result().fairResult("这个汇率在使用中");
        }
        int num = service.delete(id);
        return new Result().result(num);
    }


}
