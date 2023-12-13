package com.csgo.domain.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class UserRoomImgDTO {

    private String img;
    private Integer status = 0;  // 1赢家  0 输家
    @TableField("user_id")
    private Integer userId;
    @TableField("room_num")
    private String roomNum;
}
