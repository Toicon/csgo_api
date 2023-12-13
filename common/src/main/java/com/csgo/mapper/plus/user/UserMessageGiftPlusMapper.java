package com.csgo.mapper.plus.user;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beust.jcommander.internal.Lists;
import com.csgo.condition.SearchUserMessageGiftCondition;
import com.csgo.domain.enums.ExtractEnum;
import com.csgo.domain.plus.user.UserMessageGiftPlus;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface UserMessageGiftPlusMapper extends BaseMapper<UserMessageGiftPlus> {

    default UserMessageGiftPlus findByMessageId(Integer messageId) {
        LambdaQueryWrapper<UserMessageGiftPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserMessageGiftPlus::getUserMessageId, messageId);
        wrapper.last("limit 0,1");
        return selectOne(wrapper);
    }

    default List<UserMessageGiftPlus> findByMessageIds(List<Integer> messageIdList) {
        if (CollUtil.isEmpty(messageIdList)) {
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<UserMessageGiftPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.in(UserMessageGiftPlus::getUserMessageId, messageIdList);
        return selectList(wrapper);
    }

    List<UserMessageGiftPlus> findRecent(@Param("giftProductIds") Collection<Integer> giftProductIds, @Param("excludeGiftType") String excludeGiftType);

    default Page<UserMessageGiftPlus> extractPage(SearchUserMessageGiftCondition condition) {
        List<Integer> stateList = new ArrayList<>();
        stateList.add(ExtractEnum.YES.getCode());
        LambdaQueryWrapper<UserMessageGiftPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserMessageGiftPlus::getUserId, condition.getUserId());
        wrapper.in(UserMessageGiftPlus::getState, stateList);
        wrapper.orderByDesc(UserMessageGiftPlus::getUt);
        return selectPage(condition.getPage(), wrapper);
    }

    int deleteBeforeTime(@Param("flag") int flag, @Param("beforeTime") Date beforeTime);
}
