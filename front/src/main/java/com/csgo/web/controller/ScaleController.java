package com.csgo.web.controller;

import com.csgo.mapper.ScaleMapper;
import com.csgo.domain.Scale;
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
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Api(tags = "出售折算设置")
@RestController
@RequestMapping("/scale")
@Slf4j
public class ScaleController {

    @Autowired
    private ScaleMapper scaleMapper;

    /**
     * 根据ID查询到对应的出售折算设置
     *
     * @return
     */
    @ApiOperation("根据ID查询到对应的出售折算设置（后台管理）")
    @RequestMapping(value = "queryById", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID")
    })
    public Result queryById(Integer id) {
        Scale paySet = scaleMapper.selectByPrimaryKey(id);
        return new Result().result(paySet);
    }

    /**
     * 查询所有对应的出售折算设置
     *
     * @return
     */
    @ApiOperation("查询所有对应的出售折算设置（后台管理）")
    @RequestMapping(value = "queryAll", method = RequestMethod.GET)
    public Result queryAll() {
        List<Scale> list = scaleMapper.selectList(new Scale());
        return new Result().result(list);
    }

}

