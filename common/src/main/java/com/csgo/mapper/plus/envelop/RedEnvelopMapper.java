package com.csgo.mapper.plus.envelop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.envelop.SearchRedEnvelopCondition;
import com.csgo.domain.plus.envelop.RedEnvelop;
import com.csgo.domain.enums.RedEnvelopStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RedEnvelopMapper extends BaseMapper<RedEnvelop> {

    default Page<RedEnvelop> pagination(SearchRedEnvelopCondition condition) {
        LambdaQueryWrapper<RedEnvelop> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(condition.getName())) {
            wrapper.eq(RedEnvelop::getName, condition.getName());
        }
        wrapper.ne(RedEnvelop::getStatus, RedEnvelopStatus.DELETE);
        wrapper.orderByDesc(RedEnvelop::getSortId);
        wrapper.orderByDesc(RedEnvelop::getCreateDate);
        return selectPage(condition.getPage(), wrapper);
    }

    default RedEnvelop get(String token) {
        LambdaQueryWrapper<RedEnvelop> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RedEnvelop::getToken, token);
        wrapper.eq(RedEnvelop::getStatus, RedEnvelopStatus.NORMAL);
        return selectOne(wrapper);
    }

    default List<RedEnvelop> find() {
        LambdaQueryWrapper<RedEnvelop> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RedEnvelop::isAuto, true);
        wrapper.eq(RedEnvelop::getStatus, RedEnvelopStatus.NORMAL);
        return selectList(wrapper);
    }

    default List<RedEnvelop> findAll() {
        return selectList(null);
    }

    BigDecimal getSendAmount(Integer redEnvelopId);

    default RedEnvelop findByGrade(int grade) {
        LambdaQueryWrapper<RedEnvelop> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RedEnvelop::getGrade, grade);
        return selectOne(wrapper);
    }
}