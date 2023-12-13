package com.csgo.service.fish;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.fish.SearchFishPriceCondition;
import com.csgo.domain.plus.fish.FishUserPrize;
import com.csgo.mapper.plus.fish.FishUserPrizeMapper;
import com.csgo.support.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 钓鱼玩法--用户奖励管理
 */
@Service
public class FishPrizeService {

    @Autowired
    private FishUserPrizeMapper fishUserPrizeMapper;


    public PageInfo<FishUserPrize> pageList(SearchFishPriceCondition condition) {
        Page<FishUserPrize> pagination = fishUserPrizeMapper.pagination(condition);
        return new PageInfo<>(pagination);
    }
}
