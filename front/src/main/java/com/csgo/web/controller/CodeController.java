package com.csgo.web.controller;

import com.csgo.mapper.CodeInfoMapper;
import com.csgo.domain.CodeInfo;
import com.csgo.support.Result;
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
public class CodeController {

    @Autowired
    private CodeInfoMapper service;

    /**
     * 根据ID查询到对应的邀请码
     *
     * @return
     */
    @ApiOperation("根据ID查询到对应的邀请码（后台管理）")
    @RequestMapping(value = "queryById", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID")
    })
    public Result queryById(int id) {
        CodeInfo co = service.selectByPrimaryKey(id);
        return new Result().result(co);
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

}
