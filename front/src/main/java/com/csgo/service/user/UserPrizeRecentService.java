package com.csgo.service.user;

import com.csgo.constants.CacheConstant;
import com.csgo.domain.plus.user.UserPrizePlus;
import com.csgo.mapper.plus.user.UserPrizePlusMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RDeque;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPrizeRecentService {

    private final RedissonClient redissonClient;

    private final UserPrizePlusMapper userPrizePlusMapper;

    private static final Integer MAX_SIZE = 30;

    public List<UserPrizePlus> getRecentByGiftId(int giftId) {
        RDeque<UserPrizePlus> deque = redissonClient.getDeque(buildRecentGiftIdKey(giftId));
        int size = deque.size();
        if (size < MAX_SIZE || size > 120) {
            List<UserPrizePlus> records = userPrizePlusMapper.recentByGiftId(giftId);
            deque.clear();
            deque.addAll(records);
        }
        return deque.readAll().stream().limit(MAX_SIZE).collect(Collectors.toList());
    }

    public void addRecentGiftBoxUserPrize(UserPrizePlus userPrize) {
        try {
            RDeque<UserPrizePlus> deque = redissonClient.getDeque(buildRecentGiftIdKey(userPrize.getGiftId()));
            int size = deque.size();
            if (size > 0) {
                deque.addFirst(userPrize);
                if (size >= 35) {
                    deque.removeLast();
                }
            }
        } catch (Exception e) {
            log.error("添加礼包开箱缓存错误," + e.getMessage(), e);
        }
    }

    private String buildRecentGiftIdKey(int giftId) {
        return String.format(CacheConstant.CACHE_PRIZE_GIFT_RECENT, giftId);
    }

}
