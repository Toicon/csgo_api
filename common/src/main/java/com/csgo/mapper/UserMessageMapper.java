package com.csgo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.user.UserMessage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMessageMapper extends BaseMapper<UserMessage> {

    int deleteByPrimaryKey(int id);

    int add(UserMessage record);

    UserMessage selectByPrimaryKey(int id);

    int updateByPrimaryKeySelective(UserMessage record);

    int updateByPrimaryKey(UserMessage record);

    UserMessage selectOne(UserMessage record);

    List<UserMessage> selectList(UserMessage record);

    List<UserMessage> selectListLm(UserMessage record);
}