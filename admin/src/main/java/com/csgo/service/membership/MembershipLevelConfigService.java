package com.csgo.service.membership;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.membership.SearchMembershipLevelConfigCondition;
import com.csgo.domain.plus.membership.MembershipLevelConfig;
import com.csgo.mapper.plus.membership.MembershipLevelConfigMapper;
import com.csgo.service.envelop.RedEnvelopService;
import com.echo.framework.platform.exception.ApiException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class MembershipLevelConfigService {

    @Autowired
    private MembershipLevelConfigMapper mapper;
    @Autowired
    private RedEnvelopService redEnvelopService;

    /**
     * membership_level分页查询
     */
    public Page<MembershipLevelConfig> pagination(SearchMembershipLevelConfigCondition condition) {
        return mapper.pagination(condition);
    }

    /**
     * 根据id查询membership_level
     */
    public MembershipLevelConfig get(int id) {
        return mapper.selectById(id);
    }

    /**
     * 新增membership_level
     */
    @Transactional
    public void save(MembershipLevelConfig entity, BigDecimal redAmount, String username) {

        List<MembershipLevelConfig> levels = mapper.findLevelLimit(entity.getLevel(), entity.getLevelLimit());
        if (!CollectionUtils.isEmpty(levels)) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "等级上限配置错误");
        }
        entity.setCb(username);
        entity.setCt(new Date());
        mapper.insert(entity);
        redEnvelopService.levelRedEnvelop(entity.getLevel(), redAmount);
    }

    /**
     * 更新membership_level
     */
    @Transactional
    public void update(MembershipLevelConfig entity, BigDecimal redAmount, String username) {
        List<MembershipLevelConfig> levels = mapper.findLevelLimit(entity.getLevel(), entity.getLevelLimit());
        if (!CollectionUtils.isEmpty(levels)) {
            throw new ApiException(HttpStatus.SC_BAD_REQUEST, "等级上限配置错误");
        }
        entity.setUb(username);
        entity.setUt(new Date());
        mapper.updateById(entity);
        redEnvelopService.levelRedEnvelop(entity.getLevel(), redAmount);
    }

    public List<MembershipLevelConfig> findAll() {
        return mapper.selectList(null);
    }
}
