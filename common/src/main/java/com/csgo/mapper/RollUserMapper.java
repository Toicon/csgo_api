package com.csgo.mapper;

import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.csgo.domain.RollUser;
import com.csgo.domain.plus.roll.RollUserCountDTO;
import com.csgo.domain.plus.roll.RollUserDTO;

@Repository
public interface RollUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RollUser record);

    int insertSelective(RollUser record);

    RollUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RollUser record);

    int updateByPrimaryKey(RollUser record);

    List<RollUser> selectByList(RollUser roll);

    List<RollUser> selectByRollGiftIdList(Integer rollId);

    List<RollUser> selectByRollUserList(RollUser rollUser);

    List<RollUserDTO> find(Integer rollId);

    List<RollUserCountDTO> countRollUser(@Param("rollIds") Collection<Integer> rollIds);
}
