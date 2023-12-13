package com.csgo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.user.UserLuckyHistory;
import com.csgo.mapper.UserLuckyHistoryMapper;
import com.csgo.support.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLuckyHistoryService {


    @Autowired
    private UserLuckyHistoryMapper userLuckyHistoryMapper;

    public int add(UserLuckyHistory userLuckyHistory) {
        return userLuckyHistoryMapper.add(userLuckyHistory);
    }

    public PageInfo<UserLuckyHistory> pageListByUser(Integer pageNum, Integer pageSize, Integer userId) {
        Page<UserLuckyHistory> page = new Page<>(pageNum, pageSize);
        Page<UserLuckyHistory> blindBoxList = userLuckyHistoryMapper.pageListByUser(page, userId);
        return new PageInfo<>(blindBoxList);
    }

    public UserLuckyHistory getById(Integer historyId) {
        return userLuckyHistoryMapper.getById(historyId);
    }
}
