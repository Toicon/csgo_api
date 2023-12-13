package com.csgo.web.request.blindbox;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author admin
 */
@Getter
@Setter
public class UpdateBlindBoxTypeRequest {

    private Integer id;
    private String name;
    private Integer sortId;
}
