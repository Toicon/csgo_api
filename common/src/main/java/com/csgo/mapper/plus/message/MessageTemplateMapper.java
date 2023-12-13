package com.csgo.mapper.plus.message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.notification.SearchMessageTemplateCondition;
import com.csgo.domain.plus.message.MessageTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public interface MessageTemplateMapper extends BaseMapper<MessageTemplate> {

    default Page<MessageTemplate> search(SearchMessageTemplateCondition condition) {
        LambdaQueryWrapper<MessageTemplate> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(condition.getType())) {
            wrapper.eq(MessageTemplate::getType, condition.getType());
        }
        return selectPage(condition.getPage(), wrapper);
    }

    default MessageTemplate findByType(String type) {
        LambdaQueryWrapper<MessageTemplate> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MessageTemplate::getType, type);
        return selectOne(wrapper);
    }
}