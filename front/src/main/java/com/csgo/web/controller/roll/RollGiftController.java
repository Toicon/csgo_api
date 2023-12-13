package com.csgo.web.controller.roll;

import com.csgo.domain.plus.roll.RollCoins;
import com.csgo.domain.plus.roll.RollGiftPlus;
import com.csgo.domain.plus.roll.RollUserPlus;
import com.csgo.domain.response.RollGiftResponse;
import com.csgo.service.roll.RollCoinsService;
import com.csgo.service.roll.RollGiftService;
import com.csgo.service.roll.RollUserService;
import com.csgo.util.BeanUtilsEx;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.google.inject.internal.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Api
@RestController
@RequestMapping("/roll/gift")
@Slf4j
@Configuration
public class RollGiftController {

    @Autowired
    private RollCoinsService rollCoinsService;

    @Autowired
    private RollGiftService rollGiftService;

    @Autowired
    private RollUserService rollUserService;

    /**
     * 根据Roll房间ID查询到对应的商品信息
     *
     * @return
     */
    @GetMapping("/queryByRollId/{rollId}")
    public BaseResponse<List<RollGiftResponse>> queryByRollId(@PathVariable("rollId") Integer rollId) {
        List<RollGiftPlus> rollGiftPluses = rollGiftService.find(rollId);
        if (CollectionUtils.isEmpty(rollGiftPluses)) {
            return BaseResponse.<List<RollGiftResponse>>builder().get();
        }
        List<RollGiftResponse> responses = rollGiftPluses.stream().map(rollGift -> {
            RollGiftResponse response = new RollGiftResponse();
            BeanUtilsEx.copyProperties(rollGift, response);
            return response;
        }).collect(Collectors.toList());

        List<RollUserPlus> rollUserPluses = rollUserService.find(rollId, "1");

        Set<String> giftNames = new HashSet<>();
        AtomicInteger coins = new AtomicInteger(0);

        Set<Integer> userRewardGiftIdSet = Sets.newHashSet();
        if (!CollectionUtils.isEmpty(rollUserPluses)) {
            rollUserPluses.forEach(rollUserPlus -> {
                if (rollUserPlus.getRollgiftId() != null) {
                    userRewardGiftIdSet.add(rollUserPlus.getRollgiftId());
                }
                if (StringUtils.hasText(rollUserPlus.getRollgiftName())) {
                    giftNames.add(rollUserPlus.getRollgiftName());
                }
                if ("金币".equals(rollUserPlus.getRollgiftName())) {
                    coins.getAndIncrement();
                }
            });
        }

        List<RollCoins> rollCoins = rollCoinsService.find(rollId);
        if (!CollectionUtils.isEmpty(rollCoins)) {
            rollCoins = rollCoins.stream().filter(item -> item.getUserId() == null).collect(Collectors.toList());
            rollCoins.forEach(coin -> {
                RollGiftResponse response = new RollGiftResponse();
                response.setPrice(coin.getAmount());
                response.setProductname("金币");
                response.setGrade("#eb4b4b");
                response.setImg(coin.getImg());
                responses.add(response);
            });
        }
        responses.sort(Comparator.comparing(RollGiftResponse::getPrice).reversed());

        if (!CollectionUtils.isEmpty(userRewardGiftIdSet)) {
            List<RollGiftResponse> list = responses.stream().filter(response -> !userRewardGiftIdSet.contains(response.getId())).collect(Collectors.toList());
            return BaseResponse.<List<RollGiftResponse>>builder().data(list).get();
        }
        return BaseResponse.<List<RollGiftResponse>>builder().data(responses).get();
    }
}
