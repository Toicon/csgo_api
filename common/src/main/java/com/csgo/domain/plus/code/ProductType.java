package com.csgo.domain.plus.code;

import org.apache.ibatis.type.EnumTypeHandler;

/**
 * @author admin
 */
public enum ProductType {
    VB("V币"),
    PRODUCT("道具"),
    ;
    private final String description;

    ProductType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static class TypeHandler extends EnumTypeHandler<ProductType> {
        public TypeHandler() {
            super(ProductType.class);
        }
    }
}
