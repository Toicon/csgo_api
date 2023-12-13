package com.csgo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.user.UserBuyBoxLog;
import com.csgo.mapper.UserBuyBoxLogMapper;
import com.csgo.support.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserBuyBoxLogService {

    @Autowired
    private UserBuyBoxLogMapper userBuyBoxLogMapper;


    public PageInfo<UserBuyBoxLog> pageList(Integer pageNum, Integer pageSize, String keywords,
                                            Long startTime, Long endTime) {
        Page<UserBuyBoxLog> page = new Page<>(pageNum, pageSize);
        Page<UserBuyBoxLog> blindBoxList = userBuyBoxLogMapper.pageList(page, keywords, startTime, endTime);
        return new PageInfo<>(blindBoxList);
    }

    public void deleteBath(List<Integer> ids) {
        for (Integer id : ids) {
            userBuyBoxLogMapper.deleteById(id);
        }
    }
}
