package com.csgo.web.controller.accessory;

import com.csgo.domain.GiftProduct;
import com.csgo.domain.RandomProductDO;
import com.csgo.service.GiftProductService;
import com.csgo.service.RandomProductService;
import com.csgo.service.ZbtProductFiltersService;
import com.csgo.support.PageInfo;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/random/product")
@Api(tags = "随机饰品")
public class AdminRandomProductController extends BackOfficeController {

    @Autowired
    private RandomProductService randomProductService;

    @Autowired
    private GiftProductService giftProductService;
    @Autowired
    private ZbtProductFiltersService zbtProductFiltersService;

    @GetMapping("pageList")
    @ApiOperation("随机饰品分页列表")
    @Log(desc = "查看随机饰品")
    public Result pageList(@RequestParam(defaultValue = "1")Integer pageNum,
                           @RequestParam(defaultValue = "10")Integer pageSize,
                           String keywords,String csgoType,
                           @RequestParam("luckyId")Integer luckyId) {
        PageInfo<GiftProduct> productPageInfo= randomProductService.pageList(pageNum, pageSize, keywords,csgoType,luckyId);
        productPageInfo.getList().forEach(items -> {
            items.setCsgoTypeName(zbtProductFiltersService.getNameByCsgoType(items.getCsgoType()));
        });
        return new Result().result(productPageInfo);
    }

    @GetMapping("getRandomPageList")
    @ApiOperation("全部商品分页列表")
    @Log(desc = "查看随机饰品道具列表")
    public Result pageProductList(@RequestParam(defaultValue = "1")Integer pageNum,
                                  @RequestParam(defaultValue = "10")Integer pageSize,
                                  String keywords) {
        PageInfo<GiftProduct> productPageInfo= giftProductService.pageProductList(pageNum, pageSize, keywords,null);
        productPageInfo.getList().forEach(items -> {
            items.setCsgoTypeName(zbtProductFiltersService.getNameByCsgoType(items.getCsgoType()));
        });
        return new Result().result(productPageInfo);
    }

    @ApiOperation("批量新增饰品")
    @PostMapping("addBath")
    @Log(desc = "批量新增随机饰品")
    public Result addBath(@RequestBody List<RandomProductDO> productList) {
        randomProductService.addBath(productList);
        return new Result().result(true);
    }

    @ApiOperation("修改接口")
    @PutMapping("update")
    @Log(desc = "修改随机饰品")
    public Result update(@RequestBody RandomProductDO product) {
        randomProductService.update(product);
        return new Result().result(true);
    }

    @ApiOperation("删除接口")
    @DeleteMapping("deleteBath")
    @Log(desc = "删除随机饰品")
    public Result deleteBath(@RequestBody List<Integer> ids) {
        randomProductService.deleteBath(ids);
        return new Result().result(true);
    }

}
