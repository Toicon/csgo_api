package com.csgo.web.controller.pay;

import com.csgo.domain.Scale;
import com.csgo.mapper.ScaleMapper;
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

@Api(tags = "出售折算设置")
@RestController
@RequestMapping("/scale")
@Slf4j
public class AdminScaleController extends BackOfficeController {

    @Autowired
    private ScaleMapper scaleMapper;


    /**
     * 新增出售折算设置
     *
     * @return
     */
    @ApiOperation("新增出售折算设置（后台管理）")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "scale", value = "售出的比例 （0到1之间） 0.1就是10%")
    })
    @Log(desc = "新增出售折算设置")
    public Result add(BigDecimal scale) {
        Scale paySet = new Scale();
        paySet.setScale(scale);
        paySet.setCt(new Date());
        int num = scaleMapper.insertSelective(paySet);
        return new Result().result(num);
    }

    /**
     * 编辑出售折算设置
     *
     * @return
     */
    @ApiOperation("编辑出售折算设置（后台管理）")
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "scale", value = "售出的比例 （0到1之间） 0.1就是10%"),
            @ApiImplicitParam(name = "id", value = "id")
    })
    @Log(desc = "修改出售折算设置")
    public Result update(BigDecimal scale, Integer id) {
        Scale paySet = new Scale();
        if (StringUtils.isEmpty(scale)) {
            return new Result().fairResult("售出的比例不能为空");
        }
        paySet.setScale(scale);
        paySet.setUt(new Date());
        paySet.setId(id);
        int num = scaleMapper.updateByPrimaryKeySelective(paySet);
        return new Result().result(num);
    }

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
    @Log(desc = "查询所有出售折算设置")
    public Result queryAll() {
        List<Scale> list = scaleMapper.selectList(new Scale());
        return new Result().result(list);
    }


    /**
     * 根据ID删除对应的出售折算设置
     *
     * @return
     */
    @ApiOperation("根据ID删除对应的出售折算设置（后台管理）")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID")
    })
    @Log(desc = "删除出售折算设置")
    public Result delete(Integer id) {
        int num = scaleMapper.deleteByPrimaryKey(id);
        return new Result().result(num);
    }

}

