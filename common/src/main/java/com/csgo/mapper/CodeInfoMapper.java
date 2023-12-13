package com.csgo.mapper;

import com.csgo.domain.CodeInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CodeInfo record);

    int insertSelective(CodeInfo record);

    CodeInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CodeInfo record);

    int updateByPrimaryKey(CodeInfo record);

    List<CodeInfo> getList(CodeInfo record);

    List<CodeInfo> getListlike(CodeInfo record);
}