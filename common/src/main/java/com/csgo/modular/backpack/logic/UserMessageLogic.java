package com.csgo.modular.backpack.logic;

import com.csgo.constants.SystemConstant;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import com.csgo.modular.product.enums.ProductCsgoTypeEnums;
import com.csgo.modular.product.enums.ProductKindEnums;
import com.csgo.support.GlobalConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Consumer;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMessageLogic {

    private final UserMessagePlusMapper userMessagePlusMapper;

    public UserMessagePlus add(UserPlus player, Consumer<UserMessagePlus> consumer) {
        UserMessagePlus message = new UserMessagePlus();
        message.setDrawDare(new Date());
        message.setState(GlobalConstants.USER_MESSAGE_INVENTORY);
        message.setGiftStatus("0");
        message.setUserId(player.getId());

        consumer.accept(message);

        userMessagePlusMapper.insert(message);
        return message;
    }

    public void commonProcess(UserMessagePlus item, GiftProductPlus product) {
        processGiftKeyProduct(item, product.getCsgoType());
    }

    public void processGiftKeyProduct(UserMessagePlus item, String csgoType) {
        if (ProductCsgoTypeEnums.GIFT_KEY.getType().equals(csgoType)) {
            item.setProductKind(ProductKindEnums.GIFT_KEY.getCode());
        }
    }

    public void simpleProcessGiftKeyProduct(UserMessagePlus item, String productName) {
        if (StringUtils.isBlank(productName)) {
            return;
        }
        if (productName.contains(SystemConstant.GIFT_KET_PRODUCT_NAME)) {
            item.setProductKind(ProductKindEnums.GIFT_KEY.getCode());
        }
    }

    public void changeState(UserMessagePlus um) {
        um.setState("1");
        userMessagePlusMapper.updateById(um);
    }

}
