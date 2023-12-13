package com.csgo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.notification.SearchMessageTemplateCondition;
import com.csgo.domain.plus.message.MessageTemplate;
import com.csgo.domain.enums.NotificationTemplateTypeEnum;
import com.csgo.mapper.plus.message.MessageTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Admin on 2021/4/27
 */
@Service
public class MessageTemplateService {

    @Autowired
    private MessageTemplateMapper mapper;


    @Transactional
    public int add(String type, String title, String content) {
        MessageTemplate messageTemplate = new MessageTemplate();
        messageTemplate.setType(NotificationTemplateTypeEnum.valueOf(type));
        messageTemplate.setTitle(title);
        messageTemplate.setContent(content);
        messageTemplate.setCt(new Date());
        mapper.insert(messageTemplate);
        return messageTemplate.getId();
    }

    @Transactional
    public int update(int id, String type, String title, String content) {
        MessageTemplate messageTemplate = get(id);
        messageTemplate.setType(NotificationTemplateTypeEnum.valueOf(type));
        messageTemplate.setTitle(title);
        messageTemplate.setContent(content);
        messageTemplate.setUt(new Date());
        return mapper.updateById(messageTemplate);
    }

    public MessageTemplate get(int id) {
        return mapper.selectById(id);
    }

    public MessageTemplate selectOne(String type) {
        return mapper.findByType(type);
    }

    public Page<MessageTemplate> pagination(SearchMessageTemplateCondition condition) {
        return mapper.search(condition);
    }

    @Transactional
    public int delete(int id) {
        return mapper.deleteById(id);
    }
}
