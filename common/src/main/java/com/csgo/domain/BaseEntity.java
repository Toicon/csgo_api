package com.csgo.domain;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Created by Administrator on 2018/7/7
 */
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_date")
    private Date createDate;

    @TableField("create_by")
    private String createBy;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_date")
    private Date updateDate;

    @TableField("update_by")
    private String updateBy;


    public static <T extends BaseEntity> T created(T entity, String operator) {
        BaseEntity.updated(entity, operator);
        entity.setCreateBy(operator);
        entity.setCreateDate(new Date());
        return entity;
    }

    public static <T extends BaseEntity> T updated(T entity, String operator) {
        entity.setUpdateBy(operator);
        entity.setUpdateDate(new Date());
        return entity;
    }

}
