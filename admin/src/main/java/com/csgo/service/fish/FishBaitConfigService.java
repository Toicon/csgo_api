package com.csgo.service.fish;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.fish.SearchFishBaitConfigCondition;
import com.csgo.domain.plus.fish.FishBaitConfig;
import com.csgo.mapper.plus.fish.FishBaitConfigMapper;
import com.csgo.support.PageInfo;
import com.csgo.web.support.SiteContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 钓鱼玩法--鱼饵配置信息
 */
@Service
public class FishBaitConfigService {

    @Autowired
    private FishBaitConfigMapper fishBaitConfigMapper;

    @Autowired
    protected SiteContext siteContext;

    public PageInfo<FishBaitConfig> pageList(SearchFishBaitConfigCondition condition) {
        Page<FishBaitConfig> pagination = fishBaitConfigMapper.pagination(condition);
        return new PageInfo<>(pagination);
    }

    /**
     * 新增
     *
     * @param fishBaitConfig
     * @return 结果
     */
    public int insert(FishBaitConfig fishBaitConfig) {
        fishBaitConfig.setCreateBy(siteContext.getCurrentUser().getName());
        fishBaitConfig.setCreateDate(new Date());
        return fishBaitConfigMapper.insert(fishBaitConfig);
    }

    /**
     * 修改
     *
     * @param fishBaitConfig
     * @return 结果
     */
    public int update(FishBaitConfig fishBaitConfig) {
        fishBaitConfig.setUpdateBy(siteContext.getCurrentUser().getName());
        fishBaitConfig.setUpdateDate(new Date());
        return fishBaitConfigMapper.updateById(fishBaitConfig);
    }
}
