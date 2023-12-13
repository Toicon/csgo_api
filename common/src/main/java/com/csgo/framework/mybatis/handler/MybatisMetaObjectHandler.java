package com.csgo.framework.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.csgo.framework.mybatis.entity.BaseMybatisEntity;
import com.csgo.framework.security.SecurityContext;
import com.csgo.framework.security.model.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * @author admin
 */
@Component
@RequiredArgsConstructor
public class MybatisMetaObjectHandler implements MetaObjectHandler {

    public static final String CREATE_BY = "createBy";
    public static final String CREATE_DATE = "createDate";

    public static final String UPDATE_BY = "updateBy";
    public static final String UPDATE_DATE = "updateDate";

    private static final String USER_IDENTITY_TEMPLATE = "%s-%s";

    @Autowired
    private SecurityContext securityContext;

    private String getUserIdentity() {
        SecurityUser user = securityContext.getUser();
        if (user == null || user.getType() == null || user.getUserId() == null) {
            return null;
        }
        return String.format(USER_IDENTITY_TEMPLATE, user.getType(), user.getUserId());
    }

    /**
     * 判断字段是否为空
     *
     * @param metaObject 元对象
     * @param field      字段名称
     */
    private boolean isFieldBlank(MetaObject metaObject, String field) {
        Object value = metaObject.getValue(field);
        return value == null || "".equals(value);
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseMybatisEntity) {
            String identity = getUserIdentity();

            if (StringUtils.isNotBlank(identity)) {
                if (metaObject.hasSetter(CREATE_BY) && isFieldBlank(metaObject, CREATE_BY)) {
                    this.strictInsertFill(metaObject, CREATE_BY, String.class, identity);
                }
                if (metaObject.hasSetter(UPDATE_BY) && isFieldBlank(metaObject, UPDATE_BY)) {
                    this.strictInsertFill(metaObject, UPDATE_BY, String.class, identity);
                }
            }

            Date now = new Date();
            if (metaObject.hasSetter(CREATE_DATE)) {
                this.strictInsertFill(metaObject, CREATE_DATE, Date.class, now);
            }
            if (metaObject.hasSetter(UPDATE_DATE)) {
                this.strictInsertFill(metaObject, UPDATE_DATE, Date.class, now);
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseMybatisEntity) {
            String identity = getUserIdentity();
            if (StringUtils.isNotBlank(identity) && metaObject.hasSetter(UPDATE_BY)) {
                setFieldValByName(UPDATE_BY, identity, metaObject);
            }
            if (metaObject.hasSetter(UPDATE_DATE)) {
                Date now = new Date();
                setFieldValByName(UPDATE_DATE, now, metaObject);
            }
        }
    }

}
