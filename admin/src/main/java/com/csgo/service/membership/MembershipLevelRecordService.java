package com.csgo.service.membership;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.membership.SearchMembershipLevelRecordCondition;
import com.csgo.domain.plus.membership.MembershipLevelRecordDTO;
import com.csgo.mapper.plus.membership.MembershipLevelRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MembershipLevelRecordService {

    @Autowired
    private MembershipLevelRecordMapper mapper;

    /**
     * membership_level_record分页查询
     */
    public Page<MembershipLevelRecordDTO> pagination(SearchMembershipLevelRecordCondition condition) {
        return mapper.pagination(condition.getPage(), condition);
    }
}
