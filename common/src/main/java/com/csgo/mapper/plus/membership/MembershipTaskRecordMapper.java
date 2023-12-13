package com.csgo.mapper.plus.membership;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.christmas.RecordStatus;
import com.csgo.domain.plus.membership.MembershipTaskRecord;
import com.csgo.domain.plus.membership.MembershipTaskRule;
import com.csgo.util.DateUtilsEx;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface MembershipTaskRecordMapper extends BaseMapper<MembershipTaskRecord> {

    default List<MembershipTaskRecord> findByUser(int userId, List<MembershipTaskRule> ruleTypes, boolean isToday) {
        LambdaQueryWrapper<MembershipTaskRecord> wrapper = new LambdaQueryWrapper<>();
        if (isToday) {
            Date date = new Date();
            wrapper.ge(MembershipTaskRecord::getCreateDate, DateUtilsEx.toDayStart(date));
            wrapper.le(MembershipTaskRecord::getCreateDate, DateUtilsEx.toDayEnd(date));
        }
        wrapper.eq(MembershipTaskRecord::getUserId, userId);
        wrapper.in(MembershipTaskRecord::getRuleType, ruleTypes);
        return selectList(wrapper);
    }

    default MembershipTaskRecord find(int userId, MembershipTaskRule ruleType, RecordStatus recordStatus) {
        LambdaQueryWrapper<MembershipTaskRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MembershipTaskRecord::getUserId, userId);
        wrapper.eq(MembershipTaskRecord::getRuleType, ruleType);
        wrapper.eq(MembershipTaskRecord::getRecordStatus, recordStatus);
        wrapper.orderByAsc(MembershipTaskRecord::getCreateDate);
        wrapper.last(" limit 1");
        return selectOne(wrapper);
    }

    default MembershipTaskRecord findByUser(int userId, MembershipTaskRule ruleType, boolean isToday) {
        LambdaQueryWrapper<MembershipTaskRecord> wrapper = new LambdaQueryWrapper<>();
        if (isToday) {
            Date date = new Date();
            wrapper.ge(MembershipTaskRecord::getCreateDate, DateUtilsEx.toDayStart(date));
            wrapper.le(MembershipTaskRecord::getCreateDate, DateUtilsEx.toDayEnd(date));
        }
        wrapper.eq(MembershipTaskRecord::getUserId, userId);
        wrapper.eq(MembershipTaskRecord::getRuleType, ruleType);
        return selectOne(wrapper);
    }

    default List<MembershipTaskRecord> findUserInvite(int userId, MembershipTaskRule ruleType, Integer inviteUserId) {
        LambdaQueryWrapper<MembershipTaskRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MembershipTaskRecord::getUserId, userId);
        wrapper.eq(MembershipTaskRecord::getInviteUserId, inviteUserId);
        wrapper.eq(MembershipTaskRecord::getRuleType, ruleType);
        wrapper.orderByAsc(MembershipTaskRecord::getCreateDate);
        return selectList(wrapper);
    }
}
