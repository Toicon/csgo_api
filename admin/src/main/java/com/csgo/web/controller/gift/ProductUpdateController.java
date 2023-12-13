package com.csgo.web.controller.gift;

import javax.validation.Valid;

import com.echo.framework.support.jackson.json.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.shop.SearchProductRecordCondition;
import com.csgo.domain.plus.gift.GiftProductUpdateRecord;
import com.csgo.service.GiftProductUpdateRecordService;
import com.csgo.support.DataConverter;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.shop.BatchDeleteShopRequest;
import com.csgo.web.request.withdraw.SearchWithdrawPropRequest;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ProductUpdateController extends BackOfficeController {

    @Autowired
    private GiftProductUpdateRecordService giftProductUpdateRecordService;

    @PostMapping(value = "/product/update/pagination")
    public PageResponse<GiftProductUpdateRecord> queryAll(@Valid @RequestBody SearchWithdrawPropRequest request) {
        log.info("[查询饰品更新记录] data:{}", JSON.toJSON(request));
        SearchProductRecordCondition condition = DataConverter.to(SearchProductRecordCondition.class, request);
        Page<GiftProductUpdateRecord> pagination = giftProductUpdateRecordService.pagination(condition);
        PageResponse<GiftProductUpdateRecord> responses = DataConverter.to(record -> record, pagination);
        return responses;
    }

    @GetMapping("/product/update/{recordId}")
    public BaseResponse<Void> update(@PathVariable("recordId") Integer recordId) {
        giftProductUpdateRecordService.update(recordId);
        return BaseResponse.<Void>builder().get();
    }

    @DeleteMapping("/product/batch/update")
    public BaseResponse<Void> batchUpdate(@Valid @RequestBody BatchDeleteShopRequest request) {
        giftProductUpdateRecordService.batchUpdate(request.getIds());
        return BaseResponse.<Void>builder().get();
    }
}
