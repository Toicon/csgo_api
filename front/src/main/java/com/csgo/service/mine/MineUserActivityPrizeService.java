package com.csgo.service.mine;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csgo.domain.plus.mine.MineUserActivityPrize;
import com.csgo.mapper.plus.mine.MineUserActivityPrizeMapper;
import com.csgo.web.support.SiteContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 扫雷活动用户每关奖品
 *
 * @author admin
 */
@Service
public class MineUserActivityPrizeService extends ServiceImpl<MineUserActivityPrizeMapper, MineUserActivityPrize> {

    @Autowired
    protected SiteContext siteContext;

}
