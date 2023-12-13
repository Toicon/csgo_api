package com.csgo.mapper.plus.roll;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.plus.roll.RollPlus;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface RollPlusMapper extends BaseMapper<RollPlus> {

    default Page<RollPlus> search(Page<RollPlus> page) {
        LambdaQueryWrapper<RollPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(RollPlus::getCt);
        return selectPage(page, wrapper);
    }

    default List<RollPlus> find(String status) {
        LambdaQueryWrapper<RollPlus> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq(RollPlus::getStatus, status);
        }
        wrapper.eq(RollPlus::getRoomSwitch, true);
        wrapper.orderByAsc(RollPlus::getStatus);
        wrapper.orderByDesc(RollPlus::getSortId);
        wrapper.orderByAsc(RollPlus::getDrawDate);
        wrapper.orderByAsc(RollPlus::getCt);
        return selectList(wrapper);
    }

    default List<RollPlus> findTop3() {
        LambdaQueryWrapper<RollPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RollPlus::getStatus, "0");
        wrapper.eq(RollPlus::getRoomSwitch, true);
        wrapper.orderByDesc(RollPlus::getSortId);
        wrapper.last("limit 3");
        return selectList(wrapper);
    }

    default List<RollPlus> findTopN(Integer n) {
        LambdaQueryWrapper<RollPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RollPlus::getStatus, "0");
        wrapper.eq(RollPlus::getRoomSwitch, true);
        wrapper.orderByDesc(RollPlus::getSortId);
        wrapper.last(String.format("limit %s", n));
        return selectList(wrapper);
    }

    default List<RollPlus> find(Collection<Integer> ids) {
        LambdaQueryWrapper<RollPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RollPlus::getId, ids);
        wrapper.eq(RollPlus::getRoomSwitch, true);
        wrapper.orderByAsc(RollPlus::getStatus);
        wrapper.orderByDesc(RollPlus::getSortId);
        return selectList(wrapper);
    }

    default RollPlus findByGrade(Integer minGrade, String status) {
        LambdaQueryWrapper<RollPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(RollPlus::getMinGrade, minGrade);
        wrapper.le(RollPlus::getMaxGrade, minGrade);
        wrapper.eq(RollPlus::getStatus, status);
        return selectOne(wrapper);
    }
}
