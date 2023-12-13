package com.csgo.web.controller.blindbox;

import com.csgo.domain.BlindBoxType;
import com.csgo.service.blind.BlindBoxTypeService;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blindBox/blindBoxType")
@Api(tags = "盲盒类型")
public class AdminBlindBoxTypeController extends BackOfficeController {

    @Autowired
    private BlindBoxTypeService blindBoxTypeService;


    /**
     * 查询所有对应的礼包类型
     *
     * @return
     */
    @ApiOperation("查询所有对应的盲盒类型（后台管理）")
    @RequestMapping(value = "queryAll", method = RequestMethod.GET)
    @Log(desc = "查询所有盲盒类型")
    public Result queryAll() {
        List<BlindBoxType> types = blindBoxTypeService.getList();
        return new Result().result(types);
    }


    @ApiOperation("添加接口")
    @PostMapping("add")
    @Log(desc = "添加盲盒类型")
    public Result add(@RequestBody BlindBoxType blindBoxType) {

        if (StringUtils.isEmpty(blindBoxType.getName())) {
            return new Result().fairResult("名称不能为空");
        }
        int add = blindBoxTypeService.add(blindBoxType);

        if (add > 0) {
            return new Result().result(true);
        }
        return new Result().fairResult("类型名称已存在");
    }

    @ApiOperation("删除接口")
    @DeleteMapping("deleteBath")
    @Log(desc = "删除盲盒类型")
    public Result deleteBath(@RequestBody List<Integer> ids) {
        blindBoxTypeService.deleteBath(ids);
        return new Result().result(true);
    }

}
