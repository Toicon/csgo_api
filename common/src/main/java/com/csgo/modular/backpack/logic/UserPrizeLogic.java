package com.csgo.modular.backpack.logic;

import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.plus.user.UserPrizePlus;
import com.csgo.mapper.plus.user.UserPrizePlusMapper;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.util.DateUtils;
import com.echo.framework.support.jackson.json.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.function.Consumer;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPrizeLogic {

    private static final String RECENT_PRIZE_LIST = "recentPrizeList";

    private final RedisTemplateFacde redisTemplate;

    private final UserPrizePlusMapper userPrizePlusMapper;

    @Transactional(rollbackFor = Exception.class)
    public UserPrizePlus add(UserPlus player, UserMessagePlus message, Consumer<UserPrizePlus> consumer) {
        String userName = player.getName();
        String phoneNumber = userName;
        if (!StringUtils.isEmpty(userName) && userName.length() == 11) {
            phoneNumber = userName.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }

        UserPrizePlus userPrize = new UserPrizePlus();
        userPrize.setCt(DateUtils.now());
        userPrize.setUserId(player.getId());
        userPrize.setUserNameQ(userName);
        userPrize.setUserName(phoneNumber);

        userPrize.setGiftProductId(message.getId());
        userPrize.setGiftProductName(message.getProductName());
        userPrize.setPrice(message.getMoney());
        userPrize.setGiftProductImg(message.getImg());
        userPrize.setGameName(message.getGameName());

        consumer.accept(userPrize);
        userPrizePlusMapper.insert(userPrize);

        pushToRedis(userPrize);

        return userPrize;
    }

    private void pushToRedis(UserPrizePlus userPrize) {
        try {
            long push = redisTemplate.lPush(RECENT_PRIZE_LIST, JSON.toJSON(userPrize));
            if (push == 0) {
                log.info("recentPrizeList-redis-push-fail userPrizeId:{}", userPrize.getId());
                return;
            }
            redisTemplate.rPop(RECENT_PRIZE_LIST);
        } catch (Exception e) {
            log.error("[插入用户奖品][更新redis失败]", e);
        }
    }

}
