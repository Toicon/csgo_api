package com.csgo.web.controller.gift;

import com.csgo.domain.CodeInfo;
import com.csgo.mapper.CodeInfoMapper;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Api(tags = "邀请码模块")
@RestController
@RequestMapping("/code")
@Slf4j
public class AdminCodeController extends BackOfficeController {

    @Autowired
    private CodeInfoMapper service;


    /**
     * 新增邀请码
     *
     * @param
     * @return
     */
    @ApiOperation("新增邀请码（后台管理）")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "邀请码"),
            @ApiImplicitParam(name = "holder", value = "持有人"),
            @ApiImplicitParam(name = "remarks", value = "备注")
    })
    @Log(desc = "新增邀请码")
    public Result add(String code, String holder, String remarks) {
        CodeInfo co = new CodeInfo();
        if (code != null && !code.equals("")) {
            co.setCode(code);
        }
        if (holder != null && !holder.equals("")) {
            co.setHolder(holder);
        }
        if (remarks != null && !remarks.equals("")) {
            co.setRemarks(remarks);
        }
        co.setUt(new Date());
        co.setCt(new Date());
        int num = service.insertSelective(co);
        if (num == 0) {
            return new Result().result("失败");
        }
        return new Result().result(num);
    }

    /**
     * 编辑邀请码
     *
     * @return
     */
    @ApiOperation("编辑邀请码（后台管理）")
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id"),
            @ApiImplicitParam(name = "code", value = "邀请码"),
            @ApiImplicitParam(name = "holder", value = "持有人"),
            @ApiImplicitParam(name = "remarks", value = "备注"),
            @ApiImplicitParam(name = "pay_money_x", value = "虚拟的充值金额"),
            @ApiImplicitParam(name = "extract_x", value = "虚拟的提取金额")
    })
    @Log(desc = "修改邀请码")
    public Result update(Integer id, String code, String holder, String remarks,
                         BigDecimal pay_money_x, BigDecimal extract_x) {
        CodeInfo co = service.selectByPrimaryKey(id);
        if (co == null) {
            return new Result().result("请填写正确的id");
        }
        if (code != null && !code.equals("")) {
            co.setCode(code);
        }
        if (holder != null && !holder.equals("")) {
            co.setHolder(holder);
        }
        if (remarks != null && !remarks.equals("")) {
            co.setRemarks(remarks);
        }
        if (pay_money_x != null) {
            co.setPayMoneyX(pay_money_x);
        }
        if (extract_x != null) {
            co.setExtractX(extract_x);
        }
        co.setUt(new Date());
        int num = service.updateByPrimaryKeySelective(co);
        if (num == 0) {
            return new Result().result("失败");
        }
        return new Result().result(num);
    }

    /**
     * 查询所有对应的邀请码
     *
     * @return
     */
    @ApiOperation("查询所有邀请码【可以通过 邀请码、持有人 模糊查询】（后台管理）")
    @RequestMapping(value = "queryAll", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "邀请码"),
            @ApiImplicitParam(name = "holder", value = "持有人"),
    })
    @Log(desc = "查询邀请码")
    public Result queryAll(String code, String holder) {
        CodeInfo co = new CodeInfo();
        if (code != null && !code.equals("")) {
            co.setCode(code);
        }
        if (holder != null && !holder.equals("")) {
            co.setHolder(holder);
        }
        List<CodeInfo> list = service.getListlike(co);
        return new Result().result(list);
    }


    /**
     * 根据ID删除对应的邀请码
     *
     * @return
     */
    @ApiOperation("根据ID删除对应的邀请码（后台管理）")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "礼包等级id")
    })
    @Log(desc = "删除邀请码")
    public Result delete(int id) {
        int num = service.deleteByPrimaryKey(id);
        return new Result().result(num);
    }
}
