package com.csgo.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.user.SearchUserRewardCondition;
import com.csgo.domain.plus.user.UserRewardDTO;
import com.csgo.domain.user.UserPrize;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPrizeMapper {
    int deleteByPrimaryKey(int id);

    int insert(UserPrize record);

    int insertSelective(UserPrize record);

    UserPrize selectByPrimaryKey(int id);

    int updateByPrimaryKeySelective(UserPrize record);

    int updateByPrimaryKey(UserPrize record);

    List<UserPrize> getList(UserPrize record);

    List<UserPrize> getListLt(UserPrize prize);

    Page<UserRewardDTO> rewardPage(IPage<UserRewardDTO> page, @Param("condition") SearchUserRewardCondition condition);
}