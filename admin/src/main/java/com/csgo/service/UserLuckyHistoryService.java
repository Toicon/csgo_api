package com.csgo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.user.UserLuckyHistory;
import com.csgo.mapper.UserLuckyHistoryMapper;
import com.csgo.support.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLuckyHistoryService {

    @Autowired
    private UserLuckyHistoryMapper userLuckyHistoryMapper;

    public PageInfo<UserLuckyHistory> pageList(Integer pageNum, Integer pageSize, String keywords, Long startTime, Long endTime) {
        Page<UserLuckyHistory> page = new Page<>(pageNum, pageSize);
        Page<UserLuckyHistory> blindBoxList = userLuckyHistoryMapper.pageList(page, keywords, startTime, endTime);
        return new PageInfo<>(blindBoxList);
    }

    public void deleteBath(List<Integer> ids) {
    }

}
