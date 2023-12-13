package com.csgo.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csgo.domain.user.UserMessageGift;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Repository
public interface UserMessageGiftMapper extends BaseMapper<UserMessageGift> {
    int deleteByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserMessageGift record);

    int updateByPrimaryKey(UserMessageGift record);

    List<UserMessageGift> selectByGiftProductIds(@Param("giftProductIds") Collection<Integer> giftProductIds);

    List<UserMessageGift> selectList(UserMessageGift record);

    List<UserMessageGift> selectListBytoday(UserMessageGift record);

    List<UserMessageGift> selectListBys(UserMessageGift record);

    List<UserMessageGift> selectListTq(UserMessageGift record);

    List<UserMessageGift> selectListTqWfy(UserMessageGift record);

    UserMessageGift selectByOrderId(UserMessageGift record);

    default void updateByGiftProductId(Integer giftProductId, BigDecimal price) {
        LambdaUpdateWrapper<UserMessageGift> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(UserMessageGift::getGiftProductId, giftProductId);
        wrapper.set(UserMessageGift::getMoney, price);
        update(null, wrapper);
    }
}