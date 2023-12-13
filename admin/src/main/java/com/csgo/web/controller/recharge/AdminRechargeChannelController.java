package com.csgo.web.controller.recharge;

import com.csgo.domain.plus.recharge.RechargeChannel;
import com.csgo.domain.plus.recharge.RechargeChannelPriceItem;
import com.csgo.domain.plus.recharge.RechargeChannelType;
import com.csgo.domain.plus.recharge.RechargeMethod;
import com.csgo.service.recharge.RechargeService;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.recharge.EditRechargeChannelRequest;
import com.csgo.web.request.recharge.EditRechargeChannelPriceItemRequest;
import com.csgo.web.response.recharge.RechargeChannelPriceItemResponse;
import com.csgo.web.response.recharge.RechargeChannelResponse;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Api
@RequestMapping("/recharge/channel")
public class AdminRechargeChannelController extends BackOfficeController {
    @Autowired
    private RechargeService rechargeService;

    @PostMapping
    public BaseResponse<Void> insert(@Valid @RequestBody EditRechargeChannelRequest request) {
        RechargeChannel rechargeChannel = new RechargeChannel();
        rechargeChannel.setType(RechargeChannelType.valueOf(request.getType()));
        rechargeChannel.setMethod(RechargeMethod.valueOf(request.getMethod()));
        rechargeChannel.setSortId(1);
        rechargeService.insert(rechargeChannel, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }

    @PostMapping("/{channelId}")
    public BaseResponse<Void> insertPriceItem(@PathVariable("channelId") int channelId, @Valid @RequestBody EditRechargeChannelPriceItemRequest request) {
        RechargeChannelPriceItem item = new RechargeChannelPriceItem();
        BeanUtils.copyProperties(request, item);
        item.setChannelId(channelId);
        rechargeService.insertPriceItem(item, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }

    @GetMapping
    public BaseResponse<List<RechargeChannelResponse>> findAll() {
        List<RechargeChannel> rechargeChannels = rechargeService.findAll();
        List<Integer> channelIds = rechargeChannels.stream().map(RechargeChannel::getId).collect(Collectors.toList());
        Map<Integer, List<RechargeChannelPriceItem>> rechargeChannelPriceItemListMap = rechargeService.findPriceItemByChannelIds(channelIds).stream().collect(Collectors.groupingBy(RechargeChannelPriceItem::getChannelId));
        List<RechargeChannelResponse> responses = rechargeChannels.stream().map(rechargeChannel -> {
            RechargeChannelResponse response = new RechargeChannelResponse();
            BeanUtils.copyProperties(rechargeChannel, response);
            if (rechargeChannelPriceItemListMap.containsKey(rechargeChannel.getId())) {
                response.setPrices(rechargeChannelPriceItemListMap.get(rechargeChannel.getId()).stream().map(RechargeChannelPriceItem::getPrice).collect(Collectors.toList()));
            }
            response.setTypeDis(rechargeChannel.getType().getDis());
            response.setType(rechargeChannel.getType().name());
            response.setMethodDis(rechargeChannel.getMethod().getDis());
            response.setMethod(rechargeChannel.getMethod().name());
            return response;
        }).collect(Collectors.toList());
        return BaseResponse.<List<RechargeChannelResponse>>builder().data(responses).get();
    }

    @GetMapping("/{channelId}")
    public BaseResponse<List<RechargeChannelPriceItemResponse>> findAllPriceItem(@PathVariable("channelId") int channelId) {
        List<RechargeChannelPriceItem> rechargeChannelPriceItems = rechargeService.findPriceItemByChannelIds(Collections.singletonList(channelId));
        return BaseResponse.<List<RechargeChannelPriceItemResponse>>builder().data(
                rechargeChannelPriceItems.stream().map(rechargeChannelPriceItem -> {
                    RechargeChannelPriceItemResponse response = new RechargeChannelPriceItemResponse();
                    BeanUtils.copyProperties(rechargeChannelPriceItem, response);
                    return response;
                }).collect(Collectors.toList())
        ).get();
    }

    @PutMapping("/{channelId}")
    public BaseResponse<Void> update(@PathVariable("channelId") int channelId, @Valid @RequestBody EditRechargeChannelRequest request) {
        RechargeChannel rechargeChannel = rechargeService.get(channelId);
        if (request.getSortId() != null) {
            rechargeChannel.setSortId(request.getSortId());
        }
        if (request.getHidden() != null) {
            rechargeChannel.setHidden(request.getHidden());
        }
        if (request.getType() != null) {
            rechargeChannel.setType(RechargeChannelType.valueOf(request.getType()));
        }
        if (request.getMethod() != null) {
            rechargeChannel.setMethod(RechargeMethod.valueOf(request.getMethod()));
        }
        rechargeService.update(rechargeChannel, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("/{channelId}/{priceItemId}")
    public BaseResponse<Void> updatePriceItem(@PathVariable("priceItemId") int priceItemId, @Valid @RequestBody EditRechargeChannelPriceItemRequest request) {
        RechargeChannelPriceItem item = rechargeService.getPriceItem(priceItemId);
        BeanUtilsEx.copyProperties(request, item);
        rechargeService.updatePriceItem(item, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }

    @DeleteMapping("/{channelId}")
    public BaseResponse<Void> delete(@PathVariable("channelId") int channelId) {
        rechargeService.delete(channelId);
        return BaseResponse.<Void>builder().get();
    }

    @DeleteMapping("/{channelId}/{priceItemId}")
    public BaseResponse<Void> deletePriceItem(@PathVariable("priceItemId") int priceItemId) {
        rechargeService.deletePriceItem(priceItemId);
        return BaseResponse.<Void>builder().get();
    }
}
