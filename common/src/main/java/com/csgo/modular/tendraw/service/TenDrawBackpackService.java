package com.csgo.modular.tendraw.service;


import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.modular.backpack.BackpackFromSourceConstant;
import com.csgo.modular.backpack.logic.UserMessageGiftLogic;
import com.csgo.modular.backpack.logic.UserMessageLogic;
import com.csgo.modular.backpack.logic.UserPrizeLogic;
import com.csgo.modular.backpack.service.UserBackpackService;
import com.csgo.modular.tendraw.domain.TenDrawGameDO;
import com.csgo.modular.tendraw.domain.TenDrawGamePlayDO;
import com.csgo.modular.tendraw.enums.TenDrawGameStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenDrawBackpackService {

    private final UserPlusMapper userPlusMapper;
    private final GiftProductPlusMapper giftProductPlusMapper;

    private final UserBackpackService userBackpackService;
    private final UserMessageLogic userMessageLogic;
    private final UserMessageGiftLogic userMessageGiftLogic;
    private final UserPrizeLogic userPrizeLogic;

    public UserMessagePlus inBackpack(TenDrawGameDO game, TenDrawGamePlayDO reward) {
        UserPlus player = userPlusMapper.selectById(game.getUserId());

        String giftType = "Arms Mall";
        String gameName = "Arms Mall";
        String source = "Arms Mall";

        // 增加用户背包信息
        UserMessagePlus message = userMessageLogic.add(player, item -> {
            item.setGameName(gameName);
            item.setGiftType(giftType);

            item.setGiftProductId(reward.getRewardProductId());
            item.setProductName(reward.getRewardProductName());
            item.setMoney(reward.getRewardProductPrice());
            item.setImg(reward.getRewardProductImg());
            item.setFromSource(BackpackFromSourceConstant.TEN_DRAW);

            GiftProductPlus product = giftProductPlusMapper.selectById(reward.getRewardProductId());

            userMessageLogic.commonProcess(item, product);
        });

        // 背包流水记录
        userBackpackService.inPackage(player.getId(), source, message);
        // 背包流水详情记录
        userMessageGiftLogic.add(player, message);

        userPrizeLogic.add(player, message, item -> {
            item.setGiftGradeG("0");
            item.setGiftId(0);
            item.setGiftType(giftType);
            item.setGiftName(gameName);
        });

        game.setState(TenDrawGameStatus.FINISHED.getCode());
        game.setRewardProductId(reward.getRewardProductId());
        game.setRewardProductName(reward.getRewardProductName());
        game.setRewardProductImg(reward.getRewardProductImg());
        game.setRewardProductPrice(reward.getRewardProductPrice());
        game.setRewardDate(new Date());
        game.updateById();
        return message;
    }

}

