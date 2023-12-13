package com.csgo.service;

import java.util.Comparator;
import java.util.List;

import com.csgo.modular.product.model.dto.UserMessageKeyStatisticsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.SearchUserMessageCondition;
import com.csgo.domain.plus.message.MessageTurnDTO;
import com.csgo.domain.plus.user.UserMessageDTO;
import com.csgo.domain.plus.user.UserMessagePlus;
import com.csgo.domain.user.UserMessage;
import com.csgo.mapper.UserMessageMapper;
import com.csgo.mapper.plus.user.UserMessagePlusMapper;

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

    public int delete(int id) {
        return userMessageMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public Integer insert(UserMessagePlus message) {
        return userMessagePlusMapper.insert(message);
    }

    public List<MessageTurnDTO> findIdAndTurnId(String roomNum) {
        return userMessagePlusMapper.findIdAndTurnId(roomNum);
    }

    public Page<UserMessageDTO> pagination(SearchUserMessageCondition condition) {
        return userMessagePlusMapper.pagination(condition.getPage(), condition);
    }

    @Transactional
    public void updateByPrimaryKeySelective(UserMessage um) {
        userMessageMapper.updateByPrimaryKeySelective(um);
    }

    public List<UserMessagePlus> findByIds(List<Integer> userMessageIdList, int userId, String status) {
        return userMessagePlusMapper.findSellByIds(userMessageIdList, userId, status);
    }

    @Transactional
    public void updatePlus(UserMessagePlus userMessagePlus) {
        userMessagePlusMapper.updateById(userMessagePlus);
    }

    public List<UserMessageKeyStatisticsDTO> getKeyStatistics(Integer userId) {
        List<UserMessageKeyStatisticsDTO> list = userMessagePlusMapper.getKeyStatistics(userId);
        if (!list.isEmpty()) {
            list.sort(Comparator.comparing(UserMessageKeyStatisticsDTO::getProductNum).reversed());
        }
        return list;
    }

}
