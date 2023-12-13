package com.csgo.framework.sensitive;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * @author admin
 */
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveSerialize extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveCondition condition;

    private SensitiveTypeEnum[] typeEnums;

    private Integer prefixNoMaskLen;

    private Integer suffixNoMaskLen;

    private String makStr;

    @Override
    public void serialize(String origin, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (condition.encrypt()) {
            String change = null;

            for (SensitiveTypeEnum typeEnum : typeEnums) {
                switch (typeEnum) {
                    //中文名
                    case CHINESE_NAME:
                        change = SensitiveDesensitizedUtils.chineseName(origin);
                        break;
                    //固定电话
                    case FIXED_PHONE:
                        change = SensitiveDesensitizedUtils.fixedPhone(origin);
                        break;
                    //手机号
                    case MOBILE_PHONE:
                        change = SensitiveDesensitizedUtils.mobilePhone(origin);
                        break;
                    //身份证号
                    case ID_CARD:
                        change = SensitiveDesensitizedUtils.idCardNum(origin);
                        break;
                    //地址
                    case ADDRESS:
                        change = SensitiveDesensitizedUtils.address(origin);
                        break;
                    //邮件
                    case EMAIL:
                        change = SensitiveDesensitizedUtils.email(origin);
                        break;
                    //银行卡号
                    case BANK_CARD:
                        change = SensitiveDesensitizedUtils.bankCard(origin);
                        break;
                    //密码
                    case PASSWORD:
                        change = SensitiveDesensitizedUtils.password(origin);
                        break;
                    //自定义
                    case CUSTOMER:
                        change = SensitiveDesensitizedUtils.desValue(origin, prefixNoMaskLen, suffixNoMaskLen, makStr);
                        break;
                    //关键字
                    case KEY:
                        change = SensitiveDesensitizedUtils.key(origin);
                        break;
                    default:
                        throw new IllegalArgumentException("unknown sensitive type enum " + typeEnum);
                }
            }
            if (StringUtils.isNotBlank(change)) {
                gen.writeString(change);
            }
        } else {
            gen.writeString(origin);
        }
    }

    @Override
    @SneakyThrows
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
                Sensitive sensitive = beanProperty.getAnnotation(Sensitive.class);
                if (sensitive == null) {
                    sensitive = beanProperty.getContextAnnotation(Sensitive.class);
                }

                if (sensitive != null) {
                    Class<? extends SensitiveCondition> conditionClass = sensitive.condition();
                    SensitiveCondition condition = conditionClass.newInstance();
                    return new SensitiveSerialize(condition, sensitive.type(),
                            sensitive.prefixNoMaskLen(),
                            sensitive.suffixNMaskLen(), sensitive.maskStr());
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(null);
    }

}
