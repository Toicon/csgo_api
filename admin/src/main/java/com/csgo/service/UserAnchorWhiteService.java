package com.csgo.service;


import com.csgo.domain.BaseEntity;
import com.csgo.domain.plus.anchor.UserAnchorWhite;
import com.csgo.mapper.plus.hee.UserAnchorWhiteMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 测试开箱白名单
 */
@Service
@Slf4j
public class UserAnchorWhiteService {


    @Autowired
    private UserAnchorWhiteMapper userAnchorWhiteMapper;


    @Transactional
    public void delWhite(int userId, String source) {
        List<UserAnchorWhite> list = userAnchorWhiteMapper.findByUserId(userId);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        log.info("用户:{},删除白名单用户:{}", source, userId);
        userAnchorWhiteMapper.deleteByUserId(userId);
    }


    @Transactional
    public void addWhite(int userId, String source) {
        List<UserAnchorWhite> list = userAnchorWhiteMapper.findByUserId(userId);
        if (!CollectionUtils.isEmpty(list)) {
            return;
        }
        UserAnchorWhite userAnchorWhite = new UserAnchorWhite();
        userAnchorWhite.setUserId(userId);
        BaseEntity.created(userAnchorWhite, source);
        userAnchorWhiteMapper.insert(userAnchorWhite);
    }

    public boolean validateWhiteAnchor(int userId) {
        List<UserAnchorWhite> list = userAnchorWhiteMapper.findByUserId(userId);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取全部
     *
     * @return
     */
    public List<UserAnchorWhite> findAllList() {
        return userAnchorWhiteMapper.selectList(null);
    }


}
