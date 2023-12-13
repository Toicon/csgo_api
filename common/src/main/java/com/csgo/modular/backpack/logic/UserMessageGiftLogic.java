package com.csgo.modular.backpack.logic;

import com.csgo.domain.plus.user.UserMessageGiftPlus;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.user.UserMessageGiftPlusMapper;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMessageGiftLogic {

    private final UserMessagePlusMapper userMessagePlusMapper;
    private final UserMessageGiftPlusMapper userMessageGiftPlusMapper;

    public UserMessageGiftPlus add(UserPlus player, UserMessagePlus message) {
        UserMessageGiftPlus entity = new UserMessageGiftPlus();
        Date now = new Date();
        entity.setCt(now);
        entity.setUt(now);
        entity.setState(0);

        entity.setUserMessageId(message.getId());
        entity.setGiftProductId(message.getGiftProductId());
        entity.setGiftProductName(message.getProductName());
        entity.setMoney(message.getMoney());
        entity.setImg(message.getImg());
        entity.setGameName(message.getGameName());
        entity.setGiftType(message.getGiftType());

        entity.setUserId(player.getId());
        entity.setPhone(player.getUserName());
        userMessageGiftPlusMapper.insert(entity);
        return entity;
    }

    public void sell(UserMessagePlus message) {
        UserMessageGiftPlus messageGift = userMessageGiftPlusMapper.findByMessageId(message.getId());
        messageGift.setState(1);
        messageGift.setSellMoney(BigDecimal.ZERO);
        userMessageGiftPlusMapper.updateById(messageGift);

        message.setState("1");
        userMessagePlusMapper.updateById(message);
    }

}
