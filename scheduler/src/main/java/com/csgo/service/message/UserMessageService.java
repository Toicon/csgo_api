package com.csgo.service.message;

import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserMessageService {

   
    @Autowired
    private UserMessagePlusMapper userMessagePlusMapper;

    @Transactional
    public Integer insert(UserMessagePlus message) {
        return userMessagePlusMapper.insert(message);
    }


}
