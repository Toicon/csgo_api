package com.csgo.modular.backpack.service;

import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.framework.exception.BizClientException;
import com.csgo.framework.exception.BizServerException;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.modular.backpack.logic.UserMessageGiftLogic;
import com.csgo.modular.backpack.logic.UserMessageLogic;
import com.csgo.modular.backpack.model.admin.AdminGiftKeyUserMessageVM;
import com.csgo.modular.product.enums.ProductCsgoTypeEnums;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户背包
 *
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserGiftKeyBackpackService {

    private final UserPlusMapper userPlusMapper;
    private final GiftProductPlusMapper giftProductPlusMapper;

    private final UserBackpackService userBackpackService;

    private final UserMessageLogic userMessageLogic;
    private final UserMessageGiftLogic userMessageGiftLogic;

    @Transactional(rollbackFor = Exception.class)
    public void addGiftKey(AdminGiftKeyUserMessageVM vm) {
        UserPlus player = userPlusMapper.selectById(vm.getUserId());
        if (player == null) {
            throw BizServerException.of(CommonBizCode.USER_NOT_FOUND);
        }
        GiftProductPlus product = giftProductPlusMapper.selectById(vm.getProductId());
        if (product == null) {
            throw BizServerException.of(CommonBizCode.PRODUCT_NOT_FOUND);
        }
        if (!ProductCsgoTypeEnums.GIFT_KEY.getType().equals(product.getCsgoType())) {
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }

        String gameName = "CSGO";
        String giftType = "Other";
        String source = "Other";

        Integer productNum = vm.getProductNum();

        List<UserMessagePlus> addList = Lists.newArrayList();
        for (int i = 0; i < productNum; i++) {
            // 增加用户背包信息
            UserMessagePlus message = userMessageLogic.add(player, item -> {
                item.setGameName(gameName);
                item.setGiftType(giftType);

                item.setGiftProductId(product.getId());
                item.setProductName(product.getName());
                item.setMoney(product.getPrice());
                item.setImg(product.getImg());
                userMessageLogic.commonProcess(item, product);
            });

            userMessageGiftLogic.add(player, message);

            addList.add(message);
        }
        userBackpackService.batchInPackage(player.getId(), source, addList);
    }

}
