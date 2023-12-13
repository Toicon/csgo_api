package com.csgo.web.controller.pay;

import com.csgo.domain.ExchangeRate;
import com.csgo.domain.PaySet;
import com.csgo.mapper.PaySetMapper;
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
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Api(tags = "支付金额设置")
@RestController
@RequestMapping("/payset")
@Slf4j
public class AdminPaySetController extends BackOfficeController {

    @Autowired
    private PaySetMapper paySetMapper;

    @Autowired
    private ExchangeRateService exchangeRateService;


    /**
     * 新增支付金额设置
     *
     * @return
     */
    @ApiOperation("新增支付金额设置（后台管理）")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "price", value = "金额")
    })
    @Log(desc = "新增支付金额设置")
    public Result add(BigDecimal price) {
        PaySet paySet = new PaySet();
        paySet.setPrice(price);
        paySet.setCt(new Date());
        //paySet.setValue(value);
        int num = paySetMapper.insertSelective(paySet);
        return new Result().result(num);
    }

    /**
     * 编辑支付金额设置
     *
     * @return
     */
    @ApiOperation("编辑支付金额设置（后台管理）")
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "price", value = "金额"),
            @ApiImplicitParam(name = "id", value = "礼包等级id")
    })
    @Log(desc = "修改支付金额设置")
    public Result update(BigDecimal price, Integer id) {
        PaySet paySet = new PaySet();
        if (StringUtils.isEmpty(price)) {
            return new Result().fairResult("支付金额不能为空");
        }
        paySet.setPrice(price);
        paySet.setUt(new Date());
        paySet.setId(id);
        int num = paySetMapper.updateByPrimaryKeySelective(paySet);
        return new Result().result(num);
    }

    /**
     * 根据ID查询到对应的支付金额设置
     *
     * @return
     */
    @ApiOperation("根据ID查询到对应的支付金额设置（后台管理）")
    @RequestMapping(value = "queryById", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "支付金额设置的ID")
    })
    public Result queryById(Integer id) {
        PaySet paySet = paySetMapper.selectByPrimaryKey(id);
        return new Result().result(paySet);
    }

    /**
     * 查询所有对应的支付金额设置
     *
     * @return
     */
    @ApiOperation("查询所有对应的支付金额设置（后台管理）")
    @RequestMapping(value = "queryAll", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页有几条数据"),
    })
    @Log(desc = "查询支付金额设置列表")
    public Result queryAll(Integer pageNum, Integer pageSize) {
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if (pageNum == null || pageNum == 0) {
            pageNum = 1;
        }

        int p = pageNum;
        pageNum = (pageNum - 1) * pageSize;


        PaySet order = new PaySet();
        order.setPageNum(pageNum);
        order.setPageSize(pageSize);

        List<PaySet> list = paySetMapper.selectListLt(order);

        String hl = "";

        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setFlag(1);
        List<ExchangeRate> ss = exchangeRateService.getList(exchangeRate);
        if (ss != null && ss.size() > 0) {
            ExchangeRate ee = ss.get(0);
            hl = ee.getExchangeRate();
        } else {
            return new Result().fairResult("汇率为空，请稍后再试");
        }

        if (list != null && list.size() > 0) {
            for (PaySet ps : list) {
                ps.setValue(ps.getPrice().multiply(new BigDecimal(hl)));
            }
            list.get(0).setTotal(paySetMapper.selectList(order).size());
        }

        return new Result().result(list);
    }


    /**
     * 根据ID删除对应的支付金额设置
     *
     * @return
     */
    @ApiOperation("根据ID删除对应的支付金额设置（后台管理）")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "支付金额设置的ID")
    })
    @Log(desc = "删除支付金额设置")
    public Result delete(Integer id) {
        int num = paySetMapper.deleteByPrimaryKey(id);
        return new Result().result(num);
    }
}
