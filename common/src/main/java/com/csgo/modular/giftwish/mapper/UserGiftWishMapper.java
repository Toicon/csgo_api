package com.csgo.modular.giftwish.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.modular.giftwish.domain.UserGiftWishDO;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface UserGiftWishMapper extends BaseMapper<UserGiftWishDO> {

    default UserGiftWishDO getExist(Integer userId, Integer giftId) {
        LambdaQueryWrapper<UserGiftWishDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserGiftWishDO::getGiftId, giftId);
        wrapper.eq(UserGiftWishDO::getUserId, userId);
        wrapper.eq(UserGiftWishDO::getState, UserGiftWishDO.STATE_ING);
        wrapper.orderByAsc(UserGiftWishDO::getId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

    default Integer countByProductId(Integer giftProductId) {
        LambdaQueryWrapper<UserGiftWishDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserGiftWishDO::getGiftProductId, giftProductId);
        return selectCount(wrapper);
    }

}
