package com.csgo.mapper;

import com.csgo.domain.user.User;
import com.csgo.domain.user.UserRoomImgDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BlindBoxTurnMapper {

    List<User> getUserBalanceByRoomNum(@Param("roomNum") String roomNum);

    List<UserRoomImgDTO> getUserImgByRoomNum(@Param("roomNum") String roomNum);

    void updateByGiftProductId(@Param("giftProductId") Integer giftProductId, @Param("price") BigDecimal price);
}