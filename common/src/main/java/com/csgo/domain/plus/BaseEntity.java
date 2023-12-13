package com.csgo.domain.plus;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * Created by Administrator on 2018/7/7
 */
@MappedSuperclass
@Getter
@Setter
public class BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private String cb;
    private Date ct;
    private String ub;
    private Date ut;
}
