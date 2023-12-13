package com.csgo.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.user.SearchUserRechargeRecordCondition;
import com.csgo.domain.plus.user.RechargeRecord;
import com.csgo.domain.plus.user.RechargeRecordDTO;
import com.csgo.mapper.plus.user.RechargeRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author admin
 */
@Service
public class RechargeRecordService {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    public Page<RechargeRecordDTO> pagination(SearchUserRechargeRecordCondition condition) {
        return rechargeRecordMapper.pagination(condition.getPage(), condition);
    }
}
