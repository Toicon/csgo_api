package com.csgo.service;

import com.csgo.domain.user.UserMessage;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.mapper.UserMessageMapper;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserMessageService {

    @Autowired
    private UserMessageMapper userMessageMapper;
    @Autowired
    private UserMessagePlusMapper userMessagePlusMapper;

    public int add(UserMessage message) {
        return userMessageMapper.add(message);
    }

    public int update(UserMessage message, int id) {
        message.setId(id);
        return userMessageMapper.updateByPrimaryKeySelective(message);
    }

    public UserMessage queryById(int id) {
        return userMessageMapper.selectByPrimaryKey(id);
    }

    public UserMessage selectOne(UserMessage message) {
        return userMessageMapper.selectOne(message);
    }

    public List<UserMessage> selectList(UserMessage message) {
        return userMessageMapper.selectList(message);
    }

    @Transactional
    public void insert(UserMessagePlus message) {
        userMessagePlusMapper.insert(message);
    }

    public int delete(int id) {
        return userMessageMapper.deleteByPrimaryKey(id);
    }

    public List<UserMessage> selectListLm(UserMessage userMessage) {
        return userMessageMapper.selectListLm(userMessage);
    }

}
