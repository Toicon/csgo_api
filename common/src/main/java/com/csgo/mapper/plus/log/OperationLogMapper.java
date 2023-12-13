package com.csgo.mapper.plus.log;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.operation.SearchOperationLogCondition;
import com.csgo.domain.plus.log.OperationLog;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * @author admin
 */
@Repository
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    default Page<OperationLog> pagination(SearchOperationLogCondition condition) {
        LambdaQueryWrapper<OperationLog> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(condition.getUserName())) {
            wrapper.like(OperationLog::getUserName, condition.getUserName());
        }
        if (StringUtils.hasText(condition.getKeywords())) {
            wrapper.like(OperationLog::getParams, condition.getKeywords());
        }
        if (null != condition.getStartDate()) {
            wrapper.ge(OperationLog::getCreateTime, condition.getStartDate());
        }
        if (null != condition.getEndDate()) {
            wrapper.le(OperationLog::getCreateTime, condition.getEndDate());
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return selectPage(condition.getPage(), wrapper);
    }
}
