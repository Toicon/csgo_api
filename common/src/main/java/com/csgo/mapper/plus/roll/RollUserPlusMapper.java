package com.csgo.mapper.plus.roll;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.roll.RollUserSelectCondition;
import com.csgo.condition.roll.SearchRollUserCondition;
import com.csgo.domain.RollUser;
import com.csgo.domain.plus.roll.RollUserDTO;
import com.csgo.domain.plus.roll.RollUserPlus;
import com.csgo.support.GlobalConstants;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author admin
 */
@Repository
public interface RollUserPlusMapper extends BaseMapper<RollUserPlus> {

    Set<Integer> findInnerAndUnAppoint(@Param("rollId") int rollId);

    default RollUserPlus getUnHit(int userId, int rollId) {
        LambdaQueryWrapper<RollUserPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RollUserPlus::getUserId, userId);
        wrapper.eq(RollUserPlus::getRollId, rollId);
        wrapper.eq(RollUserPlus::getFlag, GlobalConstants.ROLL_USER_UN_HIT);
        return selectOne(wrapper);
    }

    default List<RollUserPlus> find(Integer userId, Integer rollId, String flag, String userName) {
        LambdaQueryWrapper<RollUserPlus> wrapper = Wrappers.lambdaQuery();
        if (null != userId) {
            wrapper.eq(RollUserPlus::getUserId, userId);
        }
        if (null != rollId) {
            wrapper.eq(RollUserPlus::getRollId, rollId);
        }
        if (StringUtils.hasText(flag)) {
            wrapper.eq(RollUserPlus::getFlag, flag);
        }
        if (StringUtils.hasText(userName)) {
            wrapper.like(RollUserPlus::getUsername, userName);
        }
        return selectList(wrapper);
    }

    default List<RollUserPlus> findByRollIds(Collection<Integer> rollIds) {
        LambdaQueryWrapper<RollUserPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.in(RollUserPlus::getRollId, rollIds);
        return selectList(wrapper);
    }

    default List<RollUserPlus> findByRollId(int rollId) {
        LambdaQueryWrapper<RollUserPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RollUserPlus::getRollId, rollId);
        return selectList(wrapper);
    }

    default RollUserPlus getAppoint(int rollId, int rollGiftId) {
        LambdaQueryWrapper<RollUserPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RollUserPlus::getRollId, rollId);
        wrapper.eq(RollUserPlus::getRollgiftId, rollGiftId);
        wrapper.eq(RollUserPlus::getIsappoint, "1");
        return selectOne(wrapper);
    }

    /**
     * 同步修改未结束roll房用户头像、昵称
     *
     * @param userId
     * @param userImg
     * @param nickName
     */
    default void updateNotFinishByUserId(Integer userId, String userImg, String nickName) {
        if (userId == null) {
            return;
        }
        if (StringUtils.isEmpty(userImg) && StringUtils.isEmpty(nickName)) {
            return;
        }
        LambdaUpdateWrapper<RollUserPlus> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(RollUserPlus::getUserId, userId);
        wrapper.exists("select 1 from roll where id = roll_user.rollId and status = 0");
        if (!StringUtils.isEmpty(userImg)) {
            wrapper.set(RollUserPlus::getImg, userImg);
        }
        if (!StringUtils.isEmpty(nickName)) {
            wrapper.set(RollUserPlus::getUsername, nickName);
        }
        this.update(null, wrapper);
    }

    Page<RollUserDTO> findSelectRoomUserPage(IPage<RollUser> page, @Param("condition") RollUserSelectCondition condition);

    Page<RollUserDTO> findRoomUserPage(Page<RollUserPlus> page, @Param("condition") SearchRollUserCondition condition);

}
