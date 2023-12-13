package com.csgo.mapper.plus.envelop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.envelop.SearchRedEnvelopRecordCondition;
import com.csgo.domain.plus.envelop.RedEnvelopRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface RedEnvelopRecordMapper extends BaseMapper<RedEnvelopRecord> {

    BigDecimal getSumAmount(@Param("userId") Integer userId);

    default Page<RedEnvelopRecord> pagination(SearchRedEnvelopRecordCondition condition) {
        LambdaQueryWrapper<RedEnvelopRecord> wrapper = Wrappers.lambdaQuery();
        if (null != condition.getUserId()) {
            wrapper.eq(RedEnvelopRecord::getUserId, condition.getUserId());
        }
        if (null != condition.getEnvelopItemId()) {
            wrapper.eq(RedEnvelopRecord::getEnvelopItemId, condition.getEnvelopItemId());
        }
        if (!CollectionUtils.isEmpty(condition.getEnvelopItemIds())) {
            wrapper.in(RedEnvelopRecord::getEnvelopItemId, condition.getEnvelopItemIds());
        }
        return selectPage(condition.getPage(), wrapper);
    }

    default List<RedEnvelopRecord> find(Integer userId, Integer envelopItemId, Date startTime) {
        LambdaQueryWrapper<RedEnvelopRecord> wrapper = Wrappers.lambdaQuery();
        if (null != userId) {
            wrapper.eq(RedEnvelopRecord::getUserId, userId);
        }
        if (null != envelopItemId) {
            wrapper.eq(RedEnvelopRecord::getEnvelopItemId, envelopItemId);
        }
        if (startTime != null) {
            wrapper.ge(RedEnvelopRecord::getCreateDate, startTime);
        }
        return selectList(wrapper);
    }

    default List<RedEnvelopRecord> findRecord(Date startTime) {
        LambdaQueryWrapper<RedEnvelopRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.ge(RedEnvelopRecord::getCreateDate, startTime);
        wrapper.orderByDesc(RedEnvelopRecord::getCreateDate);
        wrapper.last(" limit 20");
        return selectList(wrapper);
    }

    /**
     * 获取红包领取数量
     *
     * @param envelopItemId
     * @return
     */
    default int getReceiveCount(Integer envelopItemId) {
        LambdaQueryWrapper<RedEnvelopRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RedEnvelopRecord::getEnvelopItemId, envelopItemId);
        return selectCount(wrapper);
    }

    List<RedEnvelopRecord> findReceive(@Param("userId") int userId, @Param("envelopId") int envelopId, @Param("startTime") Date startTime);
}
