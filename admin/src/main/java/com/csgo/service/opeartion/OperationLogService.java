package com.csgo.service.opeartion;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.operation.SearchOperationLogCondition;
import com.csgo.domain.plus.log.OperationLog;
import com.csgo.mapper.plus.log.OperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author admin
 */
@Service
public class OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    public Page<OperationLog> pagination(SearchOperationLogCondition condition) {
        return operationLogMapper.pagination(condition);
    }

    @Transactional
    public void insert(OperationLog log) {
        log.setCreateTime(new Date());
        operationLogMapper.insert(log);
    }
}
