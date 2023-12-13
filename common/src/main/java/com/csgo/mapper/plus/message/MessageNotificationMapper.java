package com.csgo.mapper.plus.message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.notification.SearchMessageNotificationCondition;
import com.csgo.domain.plus.message.MessageNotification;
import com.csgo.domain.enums.NotificationStatusEnum;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageNotificationMapper extends BaseMapper<MessageNotification> {

    default Page<MessageNotification> pagination(SearchMessageNotificationCondition condition) {
        LambdaQueryWrapper<MessageNotification> wrapper = Wrappers.lambdaQuery();
        if (null != condition.getUserId()) {
            wrapper.eq(MessageNotification::getUserId, condition.getUserId());
        }
        wrapper.orderByDesc(MessageNotification::getCt);
        return selectPage(condition.getPage(), wrapper);
    }

    default void updateStatus(int userId, Integer id, NotificationStatusEnum status) {
        LambdaUpdateWrapper<MessageNotification> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(MessageNotification::getUserId, userId);
        if (id != null) {
            wrapper.eq(MessageNotification::getId, id);
        }
        wrapper.set(MessageNotification::getStatus, status);
        update(null, wrapper);
    }

    default int count(int userId, String status) {
        LambdaQueryWrapper<MessageNotification> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MessageNotification::getUserId, userId);
        wrapper.eq(MessageNotification::getStatus, status);
        return selectCount(wrapper);
    }
}