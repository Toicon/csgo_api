package com.csgo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.user.UserBuyBoxLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBuyBoxLogMapper extends BaseMapper<UserBuyBoxLog> {

    int add(@Param("userBuyBoxLog") UserBuyBoxLog userBuyBoxLog);

    Page<UserBuyBoxLog> pageList(Page<UserBuyBoxLog> page, @Param("keywords") String keywords, @Param("start_time") Long start_time, @Param("end_time") Long end_time);

    void deleteById(@Param("id") Integer id);
}