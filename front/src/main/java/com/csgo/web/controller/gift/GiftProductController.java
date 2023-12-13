package com.csgo.web.controller.gift;

import cn.hutool.core.collection.CollUtil;
import com.csgo.domain.GiftProduct;
import com.csgo.domain.GiftProductRecord;
import com.csgo.domain.ProductFilterCategoryDO;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.user.User;
import com.csgo.domain.user.UserMessageGift;
import com.csgo.mapper.GiftProductRecordMapper;
import com.csgo.mapper.UserMessageGiftMapper;
import com.csgo.service.GiftProductService;
import com.csgo.service.UserMessageItemRecordService;
import com.csgo.service.UserMessageRecordService;
import com.csgo.service.UserMessageService;
import com.csgo.service.ZbtProductFiltersService;
import com.csgo.service.lottery.support.LuckyGiftProduct;
import com.csgo.service.user.UserService;
import com.csgo.support.GlobalConstants;
import com.csgo.support.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Api(tags = "礼包商品信息")
@RestController
@RequestMapping("/gift/product")
@Slf4j
public class GiftProductController {

    @Autowired
    private GiftProductService service;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private UserMessageGiftMapper messageMapper;
    @Autowired
    private UserMessageRecordService userMessageRecordService;
    @Autowired
    private UserMessageItemRecordService userMessageItemRecordService;
    @Autowired
    private GiftProductRecordMapper giftProductRecordMapper;
    @Autowired
    private ZbtProductFiltersService zbtProductFiltersService;

    @GetMapping("getAllZbtProductFilters")
    @ApiOperation("获取所有分类")
    public Result getAllZbtProductFilters() {
        List<ProductFilterCategoryDO> productFilterCategoryDOS = zbtProductFiltersService.getAllZbtProductFilters();
        return new Result().result(productFilterCategoryDOS);
    }

    /**
     * 根据ID查询商品信息
     *
     * @return
     */
    @ApiOperation("根据ID查询到商品信息（后台管理）")
    @RequestMapping(value = "queryById", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID")
    })
    public Result queryById(int id) {
        GiftProduct giftGrade = service.queryById(id);
        return new Result().result(giftGrade);
    }

    /**
     * 根据礼包ID查询到对应所有的礼包商品信息
     *
     * @return
     */
    @ApiOperation("根据礼包ID查询到对应所有的礼包商品信息（前台抽奖）")
    @RequestMapping(value = "queryByGiftId", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "giftId", value = "礼包id")
    })
    public Result queryByGiftId(Integer giftId) {
        if (giftId == null || giftId == 0) {
            return new Result().fairResult("gift_id不能为空");
        }

        GiftProductRecord giftProductr = new GiftProductRecord();
        giftProductr.setGiftId(giftId);
        List<GiftProductRecord> s = giftProductRecordMapper.getListOrderPrice(giftProductr);
        if (s == null || s.size() < 1) {
            return new Result().fairResult("没有找到该礼包中的商品，请确认后再试");
        }

        List<GiftProduct> giftProduct1 = new ArrayList<>();
//        List<GiftProduct> giftProduct2 = new ArrayList<>();
//        List<GiftProduct> giftProduct3 = new ArrayList<>();


        for (GiftProductRecord gfs : s) {
            GiftProduct giftProduct = new GiftProduct();
            giftProduct.setId(gfs.getGiftProductId());
            List<GiftProduct> giftProducts2s = service.getList(giftProduct);
            if (giftProducts2s != null && giftProducts2s.size() > 0) {
                GiftProduct sss = giftProducts2s.get(0);
                sss.setIsdefault(gfs.getIsdefault());
                sss.setOutProbability(gfs.getOutProbability());
                sss.setWithinProbability(gfs.getWithinProbability());
                sss.setShow_probability(gfs.getShow_probability());
                giftProduct1.add(sss);
            }
        }
        fillWinRate(giftProduct1);
        //List<GiftProduct> giftProduct = giftProduct1.stream().sorted(Comparator.comparing(GiftProduct::getGrade)).collect(Collectors.toList());
        return new Result().result(giftProduct1);
    }

    private void fillWinRate(List<GiftProduct> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }

        BigDecimal totalPrice = list.stream().map(GiftProduct::getPrice).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        for (GiftProduct product : list) {
            BigDecimal weight = totalPrice.divide(product.getPrice(), 6, RoundingMode.UP);
            product.setWinWeight(weight);
        }
        BigDecimal totalWeight = list.stream().map(GiftProduct::getWinWeight).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        for (GiftProduct product : list) {
            BigDecimal rate = product.getWinWeight().divide(totalWeight, 6, RoundingMode.UP);
            product.setWinRate(rate);
        }
    }

    /**
     * 根据礼包ID查询到对应所有的礼包商品信息[打乱顺序]
     *
     * @return
     */
    @ApiOperation("根据礼包ID查询到对应所有的礼包商品信息[打乱顺序]（前台抽奖）")
    @RequestMapping(value = "queryByGiftIdChaos", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "giftId", value = "礼包id")
    })
    public Result queryByGiftIdChaos(Integer giftId) {
        if (giftId == null || giftId == 0) {
            return new Result().fairResult("gift_id不能为空");
        }

        GiftProductRecord giftProductr = new GiftProductRecord();
        giftProductr.setGiftId(giftId);
        List<GiftProductRecord> s = giftProductRecordMapper.getListOrderPrice(giftProductr);
        if (s == null || s.size() < 1) {
            return new Result().fairResult("没有找到该礼包中的商品，请确认后再试");
        }

        List<GiftProduct> giftProduct1 = new ArrayList<>();
        //Set<GiftProduct> set = new HashSet();
        for (GiftProductRecord gfs : s) {
            GiftProduct giftProduct = new GiftProduct();
            giftProduct.setId(gfs.getGiftProductId());
            List<GiftProduct> giftProducts2s = service.getList(giftProduct);
            if (giftProducts2s != null && giftProducts2s.size() > 0) {
                GiftProduct sss = giftProducts2s.get(0);
                sss.setIsdefault(gfs.getIsdefault());
                sss.setOutProbability(gfs.getOutProbability());
                sss.setWithinProbability(gfs.getWithinProbability());
                sss.setShow_probability(gfs.getShow_probability());
                giftProduct1.add(sss);
            }
            //set.add(giftProduct);
        }

        Collections.shuffle(giftProduct1);

        //List<GiftProduct> giftProduct = giftProduct1.stream().sorted(Comparator.comparing(GiftProduct::getGrade)).collect(Collectors.toList());
        return new Result().result(giftProduct1);
    }

}
