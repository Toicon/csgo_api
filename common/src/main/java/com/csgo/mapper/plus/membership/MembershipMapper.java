package com.csgo.mapper.plus.membership;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.membership.SearchMembershipCondition;
import com.csgo.domain.plus.membership.Membership;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MembershipMapper extends BaseMapper<Membership> {

    /**
     * 分页查询表membership所有信息
     */
    default Page<Membership> pagination(SearchMembershipCondition condition) {
        LambdaQueryWrapper<Membership> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Membership::getUserId);
        return selectPage(condition.getPage(), wrapper);
    }

    default Membership get(Integer userId) {
        LambdaQueryWrapper<Membership> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Membership::getUserId, userId);
        return selectOne(wrapper);
    }

    default List<Membership> findByDate(Date date) {
        LambdaQueryWrapper<Membership> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(Membership::getGrade, 3);
        wrapper.lt(Membership::getLastGradeDate, date);
        return selectList(wrapper);
    }
}

