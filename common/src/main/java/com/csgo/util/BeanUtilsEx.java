package com.csgo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;


public final class BeanUtilsEx extends BeanUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtilsEx.class);

    private BeanUtilsEx() {
    }

    public static void copyProperties(Object source, Object target) {
        copyProperties(source, target, new HashSet<>());
    }

    //重写复制方法，对于null值不复制
    public static void copyProperties(Object source, Object target, Set<String> excludeFieldSet) {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        for (PropertyDescriptor targetPd : targetPds) {
            if (targetPd.getWriteMethod() != null) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd == null) {
                    continue;
                }
                if (excludeFieldSet.contains(sourcePd.getName())) {
                    continue;
                }
                if (sourcePd.getReadMethod() != null) {
                    assignValue(source, target, targetPd, sourcePd);
                }
            }
        }
    }

    private static void assignValue(Object source, Object target, PropertyDescriptor targetPd, PropertyDescriptor sourcePd) {
        try {
            Method readMethod = sourcePd.getReadMethod();
            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                readMethod.setAccessible(true);
            }
            Object value = readMethod.invoke(source);
            Method writeMethod = targetPd.getWriteMethod();
            if (null != value && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) { //null值不复制
                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                    writeMethod.setAccessible(true);
                }
                writeMethod.invoke(target, value);
            }
        } catch (Exception ex) {
            LOGGER.info("Could not copy properties from source to target", ex);
        }
    }
}
