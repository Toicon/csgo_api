package com.csgo.mapper;

import com.csgo.domain.BlindBoxType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlindBoxTypeMapper {
    int deleteByPrimaryKey(Integer id);

    BlindBoxType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BlindBoxType record);

    int updateByPrimaryKey(BlindBoxType record);

    BlindBoxType selectOne(BlindBoxType record);

    List<BlindBoxType> selectList(BlindBoxType record);


    /*================================================*/
    List<BlindBoxType> selectAllType();


    BlindBoxType selectByName(@Param("name") String name);

}