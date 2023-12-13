package com.csgo.service.membership;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.membership.SearchMembershipCondition;
import com.csgo.domain.plus.membership.Membership;
import com.csgo.mapper.plus.membership.MembershipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MembershipService {

    @Autowired
    private MembershipMapper mapper;

    /**
     * membership分页查询
     */
    public Page<Membership> pagination(SearchMembershipCondition condition) {
        return mapper.pagination(condition);
    }

    /**
     * 根据用户id查询membership
     */
    public Membership get(int userId) {
        return mapper.selectById(userId);
    }

    /**
     * 新增membership
     */
    @Transactional
    public void save(Integer userId) {
        Membership entity = new Membership();
        entity.setUserId(userId);
        mapper.insert(entity);
    }

    /**
     * 更新membership
     */
    @Transactional
    public void update(Membership entity) {
        mapper.updateById(entity);
    }
}
