package com.csgo.mapper.plus.message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.message.UserMessageItemRecord;
import com.csgo.domain.plus.message.UserMessageItemRecordDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UserMessageItemRecordMapper extends BaseMapper<UserMessageItemRecord> {

    default List<UserMessageItemRecord> findByRecordIds(List<Integer> userMessageRecordIds) {
        LambdaQueryWrapper<UserMessageItemRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserMessageItemRecord::getRecordId, userMessageRecordIds);
        return selectList(wrapper);
    }

    List<UserMessageItemRecordDTO> findWithProductByRecordIds(@Param("userMessageRecordIds") List<Integer> userMessageRecordIds);

    int deleteBeforeTime(@Param("flag") int flag, @Param("beforeTime") Date beforeTime);
}