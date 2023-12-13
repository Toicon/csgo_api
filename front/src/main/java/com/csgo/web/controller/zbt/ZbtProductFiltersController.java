package com.csgo.web.controller.zbt;

import com.csgo.domain.ProductFilterCategoryDO;
import com.csgo.service.ZbtProductFiltersService;
import com.csgo.support.Result;
import com.csgo.util.PageResult;
import com.csgo.util.PingYinUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/zbt/product/filters")
@Api(tags = "后台管理用户")
public class ZbtProductFiltersController {

    @Autowired
    private ZbtProductFiltersService zbtProductFiltersService;


    @ApiOperation("查询出所有的商品信息（后台管理）")
    @RequestMapping(value = "getPageList", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "一页多少条数据"),
            @ApiImplicitParam(name = "keywords", value = "分类名称（模糊查询）")
    })
    public Result getPageList(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize, String keywords) {
        PageResult<ProductFilterCategoryDO> pageResult = zbtProductFiltersService.getPageList(pageNum, pageSize, keywords);
        return new Result().result(pageResult);
    }

    @ApiOperation("通过传递中文获取英文")
    @RequestMapping(value = "getPingYin", method = RequestMethod.GET)
    public Result getPingYin(@RequestParam(required = false, defaultValue = "") String keywords) {
        String pinyin = PingYinUtil.getPingYin(keywords);
        return new Result().result(pinyin);
    }

    @ApiOperation("新增分类")
    @PostMapping("addCategory")
    public Result addCategory(@RequestBody @Validated ProductFilterCategoryDO productFilterCategoryDO) {
        zbtProductFiltersService.addCategory(productFilterCategoryDO);
        return new Result().result(true);
    }

    @ApiOperation("修改分类")
    @PostMapping("updateCategory")
    public Result updateCategory(@RequestBody @Validated ProductFilterCategoryDO productFilterCategoryDO) {
        return new Result().result(zbtProductFiltersService.updateCategory(productFilterCategoryDO));
    }

    @ApiOperation("批量删除分类")
    @DeleteMapping("deleteBach")
    public Result deleteBach(@RequestBody List<Integer> ids) {
        zbtProductFiltersService.deleteBach(ids);
        return new Result().result(true);
    }


    @RequestMapping(value = "getProductFilters", method = RequestMethod.GET)
    @ApiOperation("获取筛选条件列表")
    public Result getProductFilters() {
        return new Result().result(zbtProductFiltersService.updateRefresh());
    }


    @RequestMapping(value = "getCategoryListByParentKey", method = RequestMethod.GET)
    @ApiOperation("获取筛选条件列表")
    public Result getCategoryListByParentKey(@RequestParam("parentKey") String parentKey) {
        return new Result().result(zbtProductFiltersService.getCategoryListByParentKey(parentKey));
    }

}
