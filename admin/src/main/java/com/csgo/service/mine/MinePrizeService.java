package com.csgo.service.mine;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.mine.SearchMinePriceCondition;
import com.csgo.domain.plus.mine.MineUserPrize;
import com.csgo.mapper.plus.mine.MineUserPrizeMapper;
import com.csgo.web.support.SiteContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 扫雷活动--用户奖励查询
 */
@Service
public class MinePrizeService {

    @Autowired
    private MineUserPrizeMapper minePrizeMapper;

    @Autowired
    protected SiteContext siteContext;

    public Page<MineUserPrize> pageList(SearchMinePriceCondition condition) {
        return minePrizeMapper.pagination(condition);
    }
}
