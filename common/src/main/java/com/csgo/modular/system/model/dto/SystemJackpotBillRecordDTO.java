package com.csgo.modular.system.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Data
public class SystemJackpotBillRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer type;

    private BigDecimal beforeBalance;

    private BigDecimal newBalance;

    private BigDecimal changeBalance;

    private BigDecimal jacketBalance;

    private BigDecimal spareBalance;

    private String phone;

    private Integer userId;

    private String userName;

    private String operator;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operateDate;

}
