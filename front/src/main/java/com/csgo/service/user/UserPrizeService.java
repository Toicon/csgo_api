package com.csgo.service.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.csgo.condition.user.SearchUserRewardCondition;
import com.csgo.domain.plus.user.UserRewardDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.plus.gift.GiftPlus;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.plus.user.UserPrizeDTO;
import com.csgo.domain.plus.user.UserPrizePlus;
import com.csgo.domain.user.UserPrize;
import com.csgo.mapper.UserPrizeMapper;
import com.csgo.mapper.plus.gift.GiftPlusMapper;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.mapper.plus.user.UserPrizePlusMapper;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.util.DateUtils;
import com.csgo.web.support.UserInfo;
import com.echo.framework.support.jackson.json.JSON;
import lombok.extern.slf4j.Slf4j;

/**
 * @author admin
 */
@Service
@Slf4j
public class UserPrizeService {

    private static final String RECENT_PRIZE_LIST = "recentPrizeList";

    @Autowired
    private UserPrizeMapper userPrizeMapper;
    @Autowired
    private UserPrizePlusMapper userPrizePlusMapper;
    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private RedisTemplateFacde redisTemplate;
    @Autowired
    private GiftPlusMapper giftPlusMapper;
    @Autowired
    private UserPrizeRecentService userPrizeRecentService;

    @Transactional
    public void insert(UserPrizePlus userPrize) {
        userPrize.setCt(DateUtils.now());
        userPrizePlusMapper.insert(userPrize);
        long push = redisTemplate.lPush(RECENT_PRIZE_LIST, JSON.toJSON(userPrize));
        if (push == 0) {
            log.info("recentPrizeList-redis-push-fail");
            return;
        }
        redisTemplate.rPop(RECENT_PRIZE_LIST);
    }

    public UserPrizePlus findByUserIdAndGiftId(Integer userId, Integer giftId) {
        return userPrizePlusMapper.findByUserIdAndGiftId(userId, giftId);
    }

    public List<UserPrizeDTO> findRecent(UserInfo userInfo, Integer limit) {
        List<UserPrizePlus> userPrizeList = new ArrayList<>();
        if (userInfo != null) {
            userPrizeList = userPrizePlusMapper.findRecentByUserId(userInfo.getId());
        }
        if (CollectionUtils.isEmpty(userPrizeList) || (null != limit && limit > userPrizeList.size())) {
            int realLimit = 5;
            if (null != limit) {
                realLimit = 20;
            }
            userPrizeList = cacheFindRecent(realLimit);
        }
        if (CollectionUtils.isEmpty(userPrizeList)) {
            return new ArrayList<>();
        }
        // TODO: 2021/12/30 缓存用户信息
        Map<Integer, UserPlus> userMap = userPlusMapper.findByIds(userPrizeList.stream().map(UserPrizePlus::getUserId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(UserPlus::getId, user -> user));

        Map<Integer, GiftPlus> giftPlusMap = giftPlusMapper.findLeGrade(20).stream().collect(Collectors.toMap(GiftPlus::getId, giftPlus -> giftPlus));

        return userPrizeList.stream().filter(userPrizePlus -> userMap.containsKey(userPrizePlus.getUserId()) && !giftPlusMap.containsKey(userPrizePlus.getGiftId())).map(userPrizePlus -> {
            UserPrizeDTO userPrizeDTO = new UserPrizeDTO();
            BeanUtils.copyProperties(userPrizePlus, userPrizeDTO);
            UserPlus user = userMap.get(userPrizePlus.getUserId());
            userPrizeDTO.setUserImg(user.getImg());
            String phoneNumber = user.getUserName().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            userPrizeDTO.setUserName(user.getName().equals(user.getPhone()) ? phoneNumber : user.getName());
            return userPrizeDTO;
        }).collect(Collectors.toList());
    }

    public List<UserPrizePlus> recentByGiftId(int giftId) {
        List<UserPrizePlus> records = userPrizeRecentService.getRecentByGiftId(giftId);
        // 特殊需求，需要和弹窗显示一致
        return records.stream().sorted((o1, o2) -> {
            int ct = o1.getCt().compareTo(o2.getCt());
            if (ct != 0) {
                return -ct;
            }
            return o1.getId().compareTo(o2.getId());
        }).collect(Collectors.toList());
    }

    private List<UserPrizePlus> cacheFindRecent(int limit) {
        List<String> cacheDataList = redisTemplate.lRange(RECENT_PRIZE_LIST, 0, limit - 1);
        if (cacheDataList.size() < limit) {
            log.info("limit lt size limit:{} size:{}", limit, cacheDataList.size());
            //清空重新入redis
            if (!CollectionUtils.isEmpty(cacheDataList)) {
                log.info("delete-recentPrizeList");
                redisTemplate.delete(RECENT_PRIZE_LIST);
            }
            // TODO: 2021/12/30 考虑不走page直接select limit0,5
            List<UserPrizePlus> records = userPrizePlusMapper.userPrizePagination(new Page<>(1, limit)).getRecords();
            for (UserPrizePlus userPrize : records) {
                redisTemplate.lPush(RECENT_PRIZE_LIST, JSON.toJSON(userPrize));
            }
            return records;
        }
        return cacheDataList.stream().map(cacheData -> JSON.fromJSON(cacheData, UserPrizePlus.class)).collect(Collectors.toList());
    }

    public List<UserPrizeDTO> findByIds(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return userPrizePlusMapper.findByIds(ids);
    }

    public List<UserPrize> getListLt(UserPrize prize) {
        return userPrizeMapper.getListLt(prize);
    }


    public List<UserPrize> getList(UserPrize prize) {
        return userPrizeMapper.getList(prize);
    }

    public Page<UserRewardDTO> rewardPage(SearchUserRewardCondition condition) {
        return userPrizeMapper.rewardPage(condition.getPage(), condition);
    }
}
