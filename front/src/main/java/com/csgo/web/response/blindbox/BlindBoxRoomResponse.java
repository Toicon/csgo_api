package com.csgo.web.response.blindbox;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import com.csgo.domain.user.UserRoomImgDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class BlindBoxRoomResponse {
    private Integer id;
    private Integer userId;
    private String roomUserIds;
    private String roomNum;
    private String randomNum;
    private Integer seat;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    private Integer personPattern;
    private Integer blindBoxNum;
    private BigDecimal price;
    private Integer status;
    private Integer integral;
    private Integer currentNum;
    private String victoryUserIds;
    private Integer victoryStatus;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addTime;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private Integer currentTurn;
    private Integer roomCount;
    private List<String> boxImgList;
    private List<UserRoomImgDTO> userImgList;
    private boolean needPwd;
}
