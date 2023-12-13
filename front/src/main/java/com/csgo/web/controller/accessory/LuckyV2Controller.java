package com.csgo.web.controller.accessory;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.accessory.SearchLuckyProductCondition;
import com.csgo.domain.ProductFilterCategoryDO;
import com.csgo.domain.plus.accessory.LuckyProductDTO;
import com.csgo.domain.plus.accessory.LuckyProductType;
import com.csgo.domain.plus.lucky.LuckyProductDrawRecordDTO;
import com.csgo.service.ZbtProductFiltersService;
import com.csgo.service.accessory.LuckyProductService;
import com.csgo.service.accessory.LuckyProductTypeService;
import com.csgo.support.DataConverter;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.request.accessory.SearchLuckyProductRequest;
import com.csgo.web.response.accessory.LuckyProductResponse;
import com.csgo.web.response.lucky.LuckyProductRankingResponse;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Api
@RequireSession
@RequestMapping("/lucky")
@io.swagger.annotations.Api(tags = "升级相关接口")
public class LuckyV2Controller {

    @Autowired
    private LuckyProductService luckyProductService;
    @Autowired
    private ZbtProductFiltersService zbtProductFiltersService;
    @Autowired
    private LuckyProductTypeService luckyProductTypeService;

    @GetMapping("/category")
    public BaseResponse<List<ProductFilterCategoryDO>> getLuckyCategoryList() {
        List<ProductFilterCategoryDO> list = new ArrayList<>();
        //饰品自定义类型
        List<LuckyProductType> luckyProductTypes = luckyProductTypeService.find();
        if (!CollectionUtils.isEmpty(luckyProductTypes)) {
            luckyProductTypes.forEach(type -> {
                ProductFilterCategoryDO productFilterCategoryDO = new ProductFilterCategoryDO();
                productFilterCategoryDO.setKey("");
                productFilterCategoryDO.setImgUrl(type.getImg());
                productFilterCategoryDO.setName(type.getName());
                productFilterCategoryDO.setTypeId(type.getId());
                list.add(productFilterCategoryDO);
            });
        }
        list.addAll(luckyProductService.getLuckyCategoryList());

        list.forEach(item -> {
            List<ProductFilterCategoryDO> categoryListByParentKey = zbtProductFiltersService.getCategoryListByParentKey(item.getKey());
            item.setChildList(categoryListByParentKey);
            if (StringUtils.hasText(item.getSelfImg())) {
                item.setImgUrl(item.getSelfImg());
            }
        });

        return BaseResponse.<List<ProductFilterCategoryDO>>builder().data(list).get();
    }

    @PostMapping("/pagination")
    public PageResponse<LuckyProductResponse> pagination(@Valid @RequestBody SearchLuckyProductRequest request) {
        SearchLuckyProductCondition condition = DataConverter.to(SearchLuckyProductCondition.class, request);
        Page<LuckyProductDTO> pagination = luckyProductService.pagination(condition);
        return DataConverter.to(luckyProductDTO -> {
            LuckyProductResponse response = new LuckyProductResponse();
            BeanUtils.copyProperties(luckyProductDTO, response);
            return response;
        }, pagination);
    }

    @ApiOperation(value = "获取升级排行榜接口", notes = "获取升级排行榜接口")
    @GetMapping("/ranking/{id}")
    public BaseResponse<List<LuckyProductRankingResponse>> getRanking(@PathVariable("id") Integer id) {
        List<LuckyProductDrawRecordDTO> recordDTOS = luckyProductService.top15(id);
        if (CollectionUtils.isEmpty(recordDTOS)) {
            return BaseResponse.<List<LuckyProductRankingResponse>>builder().get();
        }
        return BaseResponse.<List<LuckyProductRankingResponse>>builder().data(recordDTOS.stream().map(record -> {
            LuckyProductRankingResponse response = new LuckyProductRankingResponse();
            BeanUtilsEx.copyProperties(record, response);
            return response;
        }).collect(Collectors.toList())).get();
    }

}
