package com.csgo.mapper.plus.lottery;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.log.SearchLuckyProductDrawRecordCondition;
import com.csgo.domain.plus.config.LuckyProductDrawRecord;
import com.csgo.domain.plus.lucky.LuckyProductDrawRecordDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface LuckyProductDrawRecordMapper extends BaseMapper<LuckyProductDrawRecord> {

    default Page<LuckyProductDrawRecord> pagination(SearchLuckyProductDrawRecordCondition condition) {
        LambdaQueryWrapper<LuckyProductDrawRecord> wrapper = new LambdaQueryWrapper<>();
        if (condition.getUserId() != null) {
            wrapper.eq(LuckyProductDrawRecord::getUserId, condition.getUserId());
        }
        if (!StringUtils.isEmpty(condition.getProductName())) {
            wrapper.like(LuckyProductDrawRecord::getProductName, condition.getProductName());
        }
        if (null != condition.getStartDate()) {
            wrapper.ge(LuckyProductDrawRecord::getCt, condition.getStartDate());
        }
        if (null != condition.getEndDate()) {
            wrapper.le(LuckyProductDrawRecord::getCt, condition.getEndDate());
        }
        if (null != condition.getProfitSort() && condition.getProfitSort() > 0) {
            if (condition.getProfitSort() == 1) {
                wrapper.ge(LuckyProductDrawRecord::getProfit, BigDecimal.ZERO);
            } else if (condition.getProfitSort() == 2) {
                wrapper.lt(LuckyProductDrawRecord::getProfit, BigDecimal.ZERO);
            }
        }
        wrapper.orderByDesc(LuckyProductDrawRecord::getCt);
        return selectPage(condition.getPage(), wrapper);
    }

    List<LuckyProductDrawRecordDTO> top15(@Param("luckyId") int luckyId);
}
