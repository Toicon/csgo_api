package com.csgo.web.request.prize;

import com.echo.framework.platform.web.request.PageRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Setter
@Getter
public class SearchUserPrizePlusRequest extends PageRequest {
    private String userName;
    private String giftType;
    private String giftName;
    private String giftProductName;
    private BigDecimal priceMin;
    private BigDecimal priceMax;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;


}
