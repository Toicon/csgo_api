package com.csgo.service.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.csgo.domain.plus.message.MessageTemplate;
import com.csgo.mapper.plus.message.MessageTemplateMapper;

/**
 * Created by Admin on 2021/4/27
 */
@Service
public class MessageTemplateService {

    @Autowired
    private MessageTemplateMapper mapper;
    
    public MessageTemplate selectOne(String type) {
        return mapper.findByType(type);
    }

}
