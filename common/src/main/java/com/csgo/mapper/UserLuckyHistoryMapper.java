package com.csgo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.user.UserLuckyHistoryLowProbabilityDTO;
import com.csgo.domain.user.UserLuckyHistory;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface UserLuckyHistoryMapper extends BaseMapper<UserLuckyHistory> {

    int add(@Param("userLuckyHistory") UserLuckyHistory userLuckyHistory);

    int getLuckyUserCount(@Param("luckyId") Integer luckyId);

    Page<UserLuckyHistory> pageList(Page<UserLuckyHistory> page, @Param("keywords") String keywords, @Param("start_time") Long start_time,
                                    @Param("end_time") Long end_time);

    Page<UserLuckyHistory> pageListByUser(Page<UserLuckyHistory> page, @Param("userId") Integer userId);

    UserLuckyHistory getById(@Param("historyId") Integer historyId);

    UserLuckyHistoryLowProbabilityDTO getLowProbabilityLuckyProduct(@Param("userId") Integer userId, @Param("startDate") Date startDate);
    UserLuckyHistoryLowProbabilityDTO getLowProbabilityUnLuckyProduct(@Param("userId") Integer userId, @Param("startDate") Date startDate);

}
