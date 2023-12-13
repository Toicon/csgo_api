package com.csgo.mapper.plus.gift;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.gift.GiftGradePlus;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface GiftGradePlusMapper extends BaseMapper<GiftGradePlus> {

    default List<GiftGradePlus> findAll() {
        return selectList(null);
    }
}
