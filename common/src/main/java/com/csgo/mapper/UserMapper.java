package com.csgo.mapper;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.report.SearchAnchorStatisticsCondition;
import com.csgo.domain.report.AnchorStatisticsDTO;
import com.csgo.domain.report.AnchorUserDTO;
import com.csgo.domain.user.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {

    int deleteByPrimaryKey(int id);

    User selectByPrimaryKey(int id);

    int updateByPrimaryKeySelective(User record);

    User selectOne(User record);

    List<User> selectList(User record);

    List<User> selectListLt(User record);

    int update(User record);

    User getUserByExtensionUrl(@Param("extensionUrl") String extensionUrl);

    User getUserByExtensionCode(@Param("extensionCode") String extensionCode);

    User getUserByUserName(@Param("userName") String userName);

    int getUserRecommendCount(@Param("userId") Integer userId);

    Page<AnchorStatisticsDTO> statisticsAnchor(IPage<AnchorStatisticsDTO> page, SearchAnchorStatisticsCondition condition);

    int findActiveUser(@Param("userId") Integer userId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    int findNewUser(@Param("userId") Integer userId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    int findOldUser(@Param("userId") Integer userId, @Param("startDate") String startDate);

    /**
     * 有效用户：注册+首次充值
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    int findValidUser(@Param("userId") Integer userId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    List<AnchorUserDTO> selectAllAnchorList();
}