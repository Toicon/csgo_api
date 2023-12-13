package com.csgo.mapper.plus.box;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.box.TreasureBoxRelate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface TreasureBoxRelateMapper extends BaseMapper<TreasureBoxRelate> {

    default void deleteByTreasureBoxId(int treasureBoxId) {
        LambdaQueryWrapper<TreasureBoxRelate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TreasureBoxRelate::getTreasureBoxId, treasureBoxId);
        delete(wrapper);
    }

    default List<TreasureBoxRelate> findRelateByTreasureBoxIds(Collection<Integer> treasureBoxIds) {
        LambdaQueryWrapper<TreasureBoxRelate> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(TreasureBoxRelate::getTreasureBoxId, treasureBoxIds);
        return selectList(wrapper);
    }

    default List<TreasureBoxRelate> findAllRelateExclude(int treasureBoxId) {
        LambdaQueryWrapper<TreasureBoxRelate> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(TreasureBoxRelate::getTreasureBoxId, treasureBoxId);
        return selectList(wrapper);
    }

    default TreasureBoxRelate getByGiftId(int giftId) {
        LambdaQueryWrapper<TreasureBoxRelate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TreasureBoxRelate::getGiftId, giftId);
        return selectOne(wrapper);
    }
}
