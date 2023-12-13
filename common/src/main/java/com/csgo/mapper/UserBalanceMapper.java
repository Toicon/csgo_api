package com.csgo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.user.UserBalance;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface UserBalanceMapper extends BaseMapper<UserBalance> {

    int add(@Param("userBalance") UserBalance userBalance);

    Page<UserBalance> pageList(Page<UserBalance> page, @Param("keywords") String keywords,
                               @Param("start_time") Long start_time,
                               @Param("end_time") Long end_time,
                               @Param("userId") Integer userId);

    Page<UserBalance> getFrontPage(Page<UserBalance> page,
                                   @Param("userId") Integer userId,
                                   @Param("startDate") Date startDate);

}
