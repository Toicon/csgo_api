package com.csgo.framework.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author admin
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class BaseMybatisLogicEntity<T extends Model<?>> extends BaseMybatisEntity<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableLogic(value = "0", delval = "UNIX_TIMESTAMP()")
    private Integer deleted;

}
