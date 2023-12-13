package com.csgo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.notification.SearchMessageNotificationCondition;
import com.csgo.domain.plus.message.MessageNotification;
import com.csgo.domain.plus.message.MessageTemplate;
import com.csgo.domain.enums.NotificationStatusEnum;
import com.csgo.domain.enums.NotificationTemplateTypeEnum;
import com.csgo.mapper.plus.message.MessageNotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Admin on 2021/4/27
 */
@Service
public class MessageNotificationService {

    @Autowired
    private MessageNotificationMapper mapper;
    @Autowired
    private MessageTemplateService messageTemplateService;

    @Transactional
    public int add(Integer userId, String title, String content, NotificationTemplateTypeEnum type) {
        MessageTemplate template = messageTemplateService.selectOne(type.name());
        //未配置模板不发消息
        if (template == null) {
            return 0;
        }
        MessageNotification notification = new MessageNotification();
        notification.setUserId(userId);
        notification.setStatus(NotificationStatusEnum.UNREAD);
        notification.setTitle(template.getTitle().replace("${title}", title));
        notification.setContent(template.getContent().replace("${content}", content));
        notification.setCt(new Date());
        mapper.insert(notification);
        return notification.getId();
    }


    @Transactional
    public int addFailure(Integer userId, String title, String content, String reason) {
        MessageTemplate template = messageTemplateService.selectOne(NotificationTemplateTypeEnum.RETRIEVE_FAILURE.name());
        //未配置模板不发消息
        if (template == null) {
            return 0;
        }
        MessageNotification notification = new MessageNotification();
        notification.setUserId(userId);
        notification.setStatus(NotificationStatusEnum.UNREAD);
        String titleTemp = template.getTitle().replace("${title}", title);
        notification.setTitle(titleTemp.replace("${reason}", reason));
        String messageContent = template.getContent().replace("${content}", content);
        notification.setContent(messageContent.replace("${reason}", reason));
        notification.setCt(new Date());
        mapper.insert(notification);
        return notification.getId();
    }

    @Transactional
    public void updateStatus(int userId, Integer id, NotificationStatusEnum status) {
        mapper.updateStatus(userId, id, status);
    }

    @Transactional
    public int update(MessageNotification messageNotification) {
        return mapper.updateById(messageNotification);
    }

    public MessageNotification get(int id) {
        return mapper.selectById(id);
    }

    public Page<MessageNotification> pagination(SearchMessageNotificationCondition condition) {
        return mapper.pagination(condition);
    }

    @Transactional
    public int delete(int id) {
        return mapper.deleteById(id);
    }

    public int count(int userId, String status) {
        return mapper.count(userId, status);
    }
}
