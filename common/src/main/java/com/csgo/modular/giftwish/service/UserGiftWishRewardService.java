package com.csgo.modular.giftwish.service;


import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.modular.backpack.BackpackFromSourceConstant;
import com.csgo.modular.backpack.logic.UserMessageGiftLogic;
import com.csgo.modular.backpack.logic.UserMessageLogic;
import com.csgo.modular.backpack.service.UserBackpackService;
import com.csgo.modular.giftwish.domain.UserGiftWishDO;
import com.csgo.modular.giftwish.domain.UserGiftWishRewardDO;
import com.csgo.modular.giftwish.mapper.UserGiftWishRewardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserGiftWishRewardService {

    private final UserPlusMapper userPlusMapper;

    private final UserGiftWishRewardMapper userGiftWishRewardMapper;

    private final UserBackpackService userBackpackService;
    private final UserMessageLogic userMessageLogic;
    private final UserMessageGiftLogic userMessageGiftLogic;

    public UserMessagePlus handleReward(UserGiftWishDO wish, GiftProductPlus product) {
        UserPlus player = userPlusMapper.selectById(wish.getUserId());

        String giftType = "心愿饰品";
        String gameName = "心愿饰品";
        String source = "心愿饰品";

        // 增加用户背包信息
        UserMessagePlus message = userMessageLogic.add(player, item -> {
            item.setGameName(gameName);
            item.setGiftType(giftType);

            item.setGiftProductId(product.getId());
            item.setProductName(product.getName());
            item.setMoney(product.getPrice());
            item.setImg(product.getImg());
            item.setFromSource(BackpackFromSourceConstant.BOX_WISH);
        });

        // 背包流水记录
        userBackpackService.inPackage(player.getId(), source, message);
        // 背包流水详情记录
        userMessageGiftLogic.add(player, message);

        createReward(wish, product);

        return message;
    }

    public void createReward(UserGiftWishDO wish, GiftProductPlus product) {
        UserGiftWishRewardDO entity = new UserGiftWishRewardDO();
        entity.setWishId(wish.getId());
        entity.setUserId(wish.getUserId());

        entity.setGiftProductId(product.getId());
        entity.setGiftProductImg(product.getImg());
        entity.setGiftProductName(product.getName());
        entity.setGiftProductPrice(product.getPrice());
        userGiftWishRewardMapper.insert(entity);
    }

}

