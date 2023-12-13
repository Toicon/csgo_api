package com.csgo.mapper.plus.membership;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.membership.SearchMembershipLevelRecordCondition;
import com.csgo.domain.plus.membership.MembershipLevelRecord;
import com.csgo.domain.plus.membership.MembershipLevelRecordDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipLevelRecordMapper extends BaseMapper<MembershipLevelRecord> {

    /**
     * 分页查询表membership_level_record所有信息
     */
    Page<MembershipLevelRecordDTO> pagination(IPage<MembershipLevelRecord> page, SearchMembershipLevelRecordCondition condition);
}

