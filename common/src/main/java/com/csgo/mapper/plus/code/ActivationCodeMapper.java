package com.csgo.mapper.plus.code;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.plus.code.ActivationCode;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface ActivationCodeMapper extends BaseMapper<ActivationCode> {

    BigDecimal getSumPrice(@Param("userId") Integer userId);

    default Page<ActivationCode> find(String cdKey, String userName, Page<ActivationCode> page) {
        LambdaQueryWrapper<ActivationCode> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(cdKey)) {
            wrapper.like(ActivationCode::getCdKey, cdKey);
        }
        if (StringUtils.hasText(userName)) {
            wrapper.like(ActivationCode::getUserName, userName);
        }
        wrapper.orderByDesc(ActivationCode::getCreateDate, ActivationCode::getId);
        return selectPage(page, wrapper);
    }

    default List<ActivationCode> findByList(List<Integer> ids) {
        LambdaQueryWrapper<ActivationCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ActivationCode::getId, ids);
        return selectList(wrapper);
    }

    default List<ActivationCode> findByCdKey(String cdKey) {
        LambdaQueryWrapper<ActivationCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivationCode::getCdKey, cdKey);
        return selectList(wrapper);
    }
}

