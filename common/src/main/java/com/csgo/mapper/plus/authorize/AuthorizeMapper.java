package com.csgo.mapper.plus.authorize;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.authorize.Authorize;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorizeMapper extends BaseMapper<Authorize> {

    default List<Authorize> query() {
        LambdaQueryWrapper<Authorize> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Authorize::getParentCode, "");
        wrapper.orderByDesc(Authorize::getCt);
        return selectList(wrapper);
    }

    default List<Authorize> findByParent(String parentCode) {
        LambdaQueryWrapper<Authorize> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Authorize::getParentCode, parentCode);
        return selectList(wrapper);
    }
}
