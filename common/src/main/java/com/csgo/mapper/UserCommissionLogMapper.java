package com.csgo.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.user.UserCommissionLog;

@Repository
public interface UserCommissionLogMapper extends BaseMapper<UserCommissionLog> {


    Page<UserCommissionLog> pageList(Page<UserCommissionLog> page, @Param("keywords") String keywords, @Param("start_time") Long start_time, @Param("end_time") Long end_time);

    Page<UserCommissionLog> pageListByUser(Page<UserCommissionLog> page, @Param("userId") Integer userId);

    UserCommissionLog getRecommendCommission(@Param("userId") Integer userId);

    List<UserCommissionLog> getUserCommissionAmount(@Param("userId") Integer userId, @Param("date") String date);

    void updateStatusByDateAndUserId(@Param("userId") Integer userId, @Param("date") String date);

    void updateCommissionUserId(@Param("commissionUserId") Integer commissionUserId,@Param("userId") Integer userId);
}
