package com.csgo.web.controller.zbt;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.stageProperty.SearchProductFilterCategoryPlusCondition;
import com.csgo.domain.plus.stageProperty.ProductFilterCategoryPlus;
import com.csgo.service.stageProperty.ProductFilterCategoryPlusService;
import com.csgo.support.DataConverter;
import com.csgo.support.Result;
import com.csgo.util.BeanUtilsEx;
import com.csgo.util.PingYinUtil;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.stageProperty.ProductFilterCategoryRequest;
import com.csgo.web.response.stageProperty.ProductFilterCategoryPlusResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import io.swagger.annotations.Api;
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
public class AdminZbtProductFiltersController extends BackOfficeController {
    @Autowired
    private ProductFilterCategoryPlusService productFilterCategoryPlusService;


    @ApiOperation("查询出所有的商品信息（后台管理）")
    @RequestMapping(value = "getPageList", method = RequestMethod.GET)
    @Log(desc = "查询道具类别列表")
    public PageResponse<ProductFilterCategoryPlusResponse> getPageList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                                       @RequestParam(defaultValue = "10") Integer pageSize, String keywords) {
        SearchProductFilterCategoryPlusCondition condition = new SearchProductFilterCategoryPlusCondition();
        condition.setName(keywords);
        condition.setPage(new Page<>(pageNum, pageSize));
        return DataConverter.to(category -> {
            ProductFilterCategoryPlusResponse response = new ProductFilterCategoryPlusResponse();
            BeanUtilsEx.copyProperties(category, response);
            return response;
        }, productFilterCategoryPlusService.pagination(condition));
    }

    @ApiOperation("通过传递中文获取英文")
    @RequestMapping(value = "getPingYin", method = RequestMethod.GET)
    public Result getPingYin(@RequestParam(required = false, defaultValue = "") String keywords) {
        String pinyin = PingYinUtil.getPingYin(keywords);
        return new Result().result(pinyin);
    }

    @ApiOperation("新增分类")
    @PostMapping("addCategory")
    @Log(desc = "新增道具类别")
    public BaseResponse<Void> addCategory(@RequestBody @Validated ProductFilterCategoryRequest request) {
        ProductFilterCategoryPlus productFilterCategoryPlus = new ProductFilterCategoryPlus();
        BeanUtilsEx.copyProperties(request, productFilterCategoryPlus);
        productFilterCategoryPlusService.insert(productFilterCategoryPlus);
        return BaseResponse.<Void>builder().get();
    }

    @ApiOperation("修改分类")
    @PostMapping("updateCategory")
    @Log(desc = "修改道具类别")
    public BaseResponse<Void> updateCategory(@RequestBody @Validated ProductFilterCategoryRequest request) {
        ProductFilterCategoryPlus productFilterCategoryPlus = productFilterCategoryPlusService.get(request.getId());
        BeanUtilsEx.copyProperties(request, productFilterCategoryPlus);
        productFilterCategoryPlusService.update(productFilterCategoryPlus);
        return BaseResponse.<Void>builder().get();
    }

    @ApiOperation("批量删除分类")
    @DeleteMapping("deleteBach")
    @Log(desc = "删除道具类别")
    public BaseResponse<Void> deleteBach(@RequestBody List<Integer> ids) {
        productFilterCategoryPlusService.deleteBach(ids);
        return BaseResponse.<Void>builder().get();
    }
}
