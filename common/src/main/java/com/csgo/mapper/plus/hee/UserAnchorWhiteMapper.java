package com.csgo.mapper.plus.hee;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.anchor.UserAnchorWhite;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 测试奖池开箱关闭白名单
 */
@Repository
public interface UserAnchorWhiteMapper extends BaseMapper<UserAnchorWhite> {


    /**
     * 根据用户id获取白名单
     *
     * @param userId
     * @return
     */
    default List<UserAnchorWhite> findByUserId(Integer userId) {
        LambdaQueryWrapper<UserAnchorWhite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAnchorWhite::getUserId, userId);
        return selectList(wrapper);
    }

    /**
     * 根据用户id删除数据
     *
     * @param userId
     * @return
     */
    default int deleteByUserId(Integer userId) {
        LambdaQueryWrapper<UserAnchorWhite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAnchorWhite::getUserId, userId);
        return delete(wrapper);
    }
}
