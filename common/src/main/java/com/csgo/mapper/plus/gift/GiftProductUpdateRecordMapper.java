package com.csgo.mapper.plus.gift;

import java.util.Date;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.shop.SearchProductRecordCondition;
import com.csgo.domain.plus.gift.GiftProductUpdateRecord;

/**
 * @author admin
 */
@Repository
public interface GiftProductUpdateRecordMapper extends BaseMapper<GiftProductUpdateRecord> {

    default Page<GiftProductUpdateRecord> pagination(Page<GiftProductUpdateRecord> page, SearchProductRecordCondition condition) {
        LambdaQueryWrapper<GiftProductUpdateRecord> wrapper = new LambdaQueryWrapper<>();
        if (null != condition.getStartDate()) {
            wrapper.ge(GiftProductUpdateRecord::getCreateDate, condition.getStartDate());
        }
        if (null != condition.getEndDate()) {
            wrapper.le(GiftProductUpdateRecord::getCreateDate, condition.getEndDate());
        }
        if (StrUtil.isNotBlank(condition.getName())) {
            wrapper.like(GiftProductUpdateRecord::getName, "%" + condition.getName() + "%");
        }
        return selectPage(page, wrapper);
    }

    default void delete(Date date) {
        LambdaQueryWrapper<GiftProductUpdateRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(GiftProductUpdateRecord::getCreateDate, date);
        delete(wrapper);
    }

}
