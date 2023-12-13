package com.csgo.web.request.user;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class InsertLuckyRecordRequest {

    private int userId;
    private BigDecimal lucky;
    private String type;
}
