package com.csgo.web.controller.mine;

import com.csgo.domain.GiftProduct;
import com.csgo.domain.MineProductDTO;
import com.csgo.service.GiftProductService;
import com.csgo.service.ZbtProductFiltersService;
import com.csgo.service.mine.MineProductService;
import com.csgo.support.PageInfo;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 扫雷活动--随机饰品
 */
@RestController
@RequestMapping("/mine/product")
@Api(tags = "扫雷活动-随机饰品")
public class AdminMineRandomProductController extends BackOfficeController {

    @Autowired
    private MineProductService mineProductService;

    @Autowired
    private GiftProductService giftProductService;
    @Autowired
    private ZbtProductFiltersService zbtProductFiltersService;

    @GetMapping("pageList")
    @ApiOperation("随机饰品分页列表")
    @Log(desc = "查看随机饰品")
    public Result pageList(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           String keywords, String csgoType) {
        PageInfo<MineProductDTO> productPageInfo = mineProductService.pageList(pageNum, pageSize, keywords, csgoType);
        productPageInfo.getList().forEach(items -> {
            items.setType(zbtProductFiltersService.getNameByCsgoType(items.getType()));
        });
        return new Result().result(productPageInfo);
    }

    @GetMapping("getRandomPageList")
    @ApiOperation("全部商品分页列表")
    @Log(desc = "查看随机饰品道具列表")
    public Result pageProductList(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  String keywords) {
        PageInfo<GiftProduct> productPageInfo = giftProductService.pageProductList(pageNum, pageSize, keywords, null);
        productPageInfo.getList().forEach(items -> {
            items.setCsgoTypeName(zbtProductFiltersService.getNameByCsgoType(items.getCsgoType()));
        });
        return new Result().result(productPageInfo);
    }

    @ApiOperation("批量新增饰品")
    @PostMapping("addBath")
    @Log(desc = "批量新增随机饰品")
    public Result addBath(@RequestBody List<Integer> giftProductIdList) {
        mineProductService.addBath(giftProductIdList);
        return new Result().result(true);
    }


    @ApiOperation("删除接口")
    @DeleteMapping("deleteBath")
    @Log(desc = "删除随机饰品")
    public Result deleteBath(@RequestBody List<Integer> ids) {
        mineProductService.deleteBath(ids);
        return new Result().result(true);
    }

}
