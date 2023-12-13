package com.csgo.web.controller.blindbox;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.csgo.domain.BlindBoxProduct;
import com.csgo.domain.GiftProduct;
import com.csgo.service.GiftProductService;
import com.csgo.service.ZbtProductFiltersService;
import com.csgo.service.blind.BlindBoxProductService;
import com.csgo.support.PageInfo;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/blindBoxPro/product")
@Api(tags = "盲盒道具")
public class AdminBlindBoxProductController extends BackOfficeController {

    @Autowired
    private BlindBoxProductService blindBoxProductService;

    @Autowired
    private GiftProductService giftProductService;
    @Autowired
    private ZbtProductFiltersService zbtProductFiltersService;

    @GetMapping("pageProductList")
    @ApiOperation("全部商品分页列表")
    @Log(desc = "查询道具列表")
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
    @Log(desc = "批量新增盲盒道具")
    public Result addBath(@RequestBody List<BlindBoxProduct> blindBoxProductS) {
        blindBoxProductService.addBath(blindBoxProductS);
        return new Result().result(true);
    }


    @ApiOperation("批量设置概率")
    @PostMapping("updateProbabilityBath")
    @Log(desc = "批量设置盲盒道具概率")
    public Result updateProbabilityBath(@RequestBody BlindBoxProduct blindBoxProduct) {

        if (blindBoxProduct.getIds() == null || blindBoxProduct.getIds().size() == 0) {
            return new Result().fairResult("请至少选择一条数据");
        }

        if (blindBoxProduct.getProbability() == null) {
            return new Result().fairResult("请输入要设置的概率");
        }
        blindBoxProductService.updateProbabilityBath(blindBoxProduct);
        return new Result().result(true);
    }

    @ApiOperation("修改接口")
    @PutMapping("update")
    @Log(desc = "修改盲盒道具")
    public Result update(@RequestBody BlindBoxProduct blindBoxProduct) {
        blindBoxProductService.update(blindBoxProduct);
        return new Result().result(true);
    }

    @ApiOperation("删除接口")
    @DeleteMapping("deleteBath")
    @Log(desc = "修改盲盒道具")
    public Result deleteBath(@RequestBody List<Integer> ids) {
        blindBoxProductService.deleteBath(ids);
        return new Result().result(true);
    }

}
