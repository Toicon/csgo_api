package com.csgo.web.controller.accessory;

import com.csgo.domain.GiftProduct;
import com.csgo.domain.LuckyProductDO;
import com.csgo.service.GiftProductService;
import com.csgo.service.LuckyProductService;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/lucky/product")
@Api(tags = "幸运饰品")
public class AdminLuckyProductController extends BackOfficeController {

    @Autowired
    private LuckyProductService luckyProductService;

    @Autowired
    private GiftProductService giftProductService;
    @Autowired
    private ZbtProductFiltersService zbtProductFiltersService;

    @GetMapping("pageList")
    @ApiOperation("幸运饰品分页列表")
    @Log(desc = "查看饰品列表")
    public Result pageList(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           String keywords, String csgoType, Integer typeId) {
        PageInfo<GiftProduct> productPageInfo = luckyProductService.pageList(pageNum, pageSize, keywords, csgoType, typeId);
        productPageInfo.getList().forEach(items -> items.setCsgoTypeName(zbtProductFiltersService.getNameByCsgoType(items.getCsgoType())));
        return new Result().result(productPageInfo);
    }

    @GetMapping("pageProductList")
    @ApiOperation("全部商品分页列表")
    @Log(desc = "查看饰品道具列表")
    public Result pageProductList(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  String keywords) {
        PageInfo<GiftProduct> productPageInfo = giftProductService.pageProductList(pageNum, pageSize, keywords, 1);
        productPageInfo.getList().forEach(items -> items.setCsgoTypeName(zbtProductFiltersService.getNameByCsgoType(items.getCsgoType())));
        return new Result().result(productPageInfo);
    }

    @ApiOperation("批量新增饰品")
    @PostMapping("addBath")
    @Log(desc = "批量新增饰品")
    public Result addBath(@RequestBody List<LuckyProductDO> luckyProductDOS) {
        luckyProductService.addBath(luckyProductDOS);
        return new Result().result(true);
    }

    @ApiOperation("修改接口")
    @PutMapping("update")
    @Log(desc = "修改饰品")
    public Result update(@RequestBody LuckyProductDO luckyProductDO) {

        if (luckyProductDO.getPrice() == null || luckyProductDO.getPrice().compareTo(BigDecimal.ZERO) < 1) {
            return new Result().fairResult("价格不能为空或小于0");
        }

        luckyProductService.update(luckyProductDO);
        return new Result().result(true);
    }

    @ApiOperation("删除接口")
    @DeleteMapping("deleteBath")
    @Log(desc = "删除饰品")
    public Result deleteBath(@RequestBody List<Integer> ids) {
        luckyProductService.deleteBath(ids);
        return new Result().result(true);
    }

}
