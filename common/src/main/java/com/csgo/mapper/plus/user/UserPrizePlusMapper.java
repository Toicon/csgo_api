package com.csgo.mapper.plus.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.prize.SearchUserPrizeDTOCondition;
import com.csgo.domain.plus.user.UserPrizeDTO;
import com.csgo.domain.plus.user.UserPrizePlus;
import com.csgo.util.DateUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface UserPrizePlusMapper extends BaseMapper<UserPrizePlus> {

    default Page<UserPrizePlus> userPrizePagination(Page<UserPrizePlus> page) {
        LambdaQueryWrapper<UserPrizePlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(UserPrizePlus::getCt);
        wrapper.orderByDesc(UserPrizePlus::getId);
        return selectPage(page, wrapper);
    }

    Page<UserPrizeDTO> pagination(Page<UserPrizeDTO> page, SearchUserPrizeDTOCondition condition);

    List<UserPrizeDTO> findByIds(@Param("prizeIds") List<Integer> prizeIds);

    default List<UserPrizePlus> recentByGiftId(int giftId) {
        LambdaQueryWrapper<UserPrizePlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPrizePlus::getGiftId, giftId);
        // 特殊需求，需要和弹窗显示一致
        wrapper.orderByDesc(UserPrizePlus::getId);
        wrapper.last("limit 30");
        return selectList(wrapper);
    }

    default int countByUserId(Integer userId) {
        LambdaQueryWrapper<UserPrizePlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPrizePlus::getUserId, userId);
        return selectCount(wrapper);
    }

    default List<UserPrizePlus> findRecentByUserId(Integer userId) {
        LambdaQueryWrapper<UserPrizePlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPrizePlus::getUserId, userId);
        Date date = new Date();
        wrapper.ge(UserPrizePlus::getCt, DateUtils.addDate(date, -5, Calendar.SECOND));
        wrapper.le(UserPrizePlus::getCt, date);
        wrapper.orderByDesc(UserPrizePlus::getCt);
        wrapper.orderByDesc(UserPrizePlus::getId);
        return selectList(wrapper);
    }

    default UserPrizePlus findByUserIdAndGiftId(Integer userId, Integer giftId) {
        LambdaQueryWrapper<UserPrizePlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPrizePlus::getUserId, userId);
        wrapper.eq(UserPrizePlus::getGiftId, giftId);
        wrapper.last(" limit 1");
        return selectOne(wrapper);
    }
}
