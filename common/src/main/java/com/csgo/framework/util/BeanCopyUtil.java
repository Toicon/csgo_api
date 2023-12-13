package com.csgo.framework.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author admin
 */
public class BeanCopyUtil extends BeanUtils {

    public static <S, T> T map(S source, Class<T> targetClass) {
        return map(source, targetClass, (String[]) null);
    }

    public static <S, T> T map(S source, Class<T> targetClass, String... ignoreProperties) {
        T dist = instantiateClass(targetClass);
        return mapTo(source, dist, ignoreProperties);
    }

    public static <S, T> T mapTo(S source, T dist, String... ignoreProperties) {
        if (source == null) {
            return dist;
        }
        copyProperties(source, dist, ignoreProperties);
        return dist;
    }

    public static <S, T> T notNullMap(S source, Class<T> targetClass) {
        return notNullMap(source, targetClass, (String[]) null);
    }

    public static <S, T> T notNullMap(S source, Class<T> targetClass, String... ignoreProperties) {
        T dist = instantiateClass(targetClass);
        return notNullMapTo(source, dist, ignoreProperties);
    }

    public static <S, T> T notNullMapTo(S source, T dist, String... ignoreProperties) {
        if (source == null) {
            return dist;
        }
        notNullCopyProperties(source, dist);
        return dist;
    }

    public static <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return mapList(source, targetClass, null, (String[]) null);
    }

    public static <S, T> List<T> mapList(List<S> source, Class<T> targetClass, String... ignoreProperties) {
        return mapList(source, targetClass, null, ignoreProperties);
    }

    public static <S, T> List<T> mapList(List<S> source, Class<T> targetClass, BiConsumer<S, T> consumer, String... ignoreProperties) {
        return source == null ? null : source.stream().map(item -> {
            T dist = map(item, targetClass, ignoreProperties);
            if (consumer != null) {
                consumer.accept(item, dist);
            }
            return dist;
        }).collect(Collectors.toList());
    }

    public static <S, T> List<T> notNullMapList(List<S> source, Class<T> targetClass) {
        return notNullMapList(source, targetClass, null, (String[]) null);
    }

    public static <S, T> List<T> notNullMapList(List<S> source, Class<T> targetClass, String... ignoreProperties) {
        return notNullMapList(source, targetClass, null, ignoreProperties);
    }

    public static <S, T> List<T> notNullMapList(List<S> source, Class<T> targetClass, BiConsumer<S, T> consumer, String... ignoreProperties) {
        return source == null ? null : source.stream().map(item -> {
            T dist = notNullMap(item, targetClass, ignoreProperties);
            if (consumer != null) {
                consumer.accept(item, dist);
            }
            return dist;
        }).collect(Collectors.toList());
    }

    public static void notNullCopyProperties(Object source, Object target) throws BeansException {
        notNullCopyProperties(source, target, null, (String[]) null);
    }

    public static void notNullCopyProperties(Object source, Object target, Class<?> editable) throws BeansException {
        notNullCopyProperties(source, target, editable, (String[]) null);
    }

    public static void notNullCopyProperties(Object source, Object target, String... ignoreProperties) throws BeansException {
        notNullCopyProperties(source, target, null, ignoreProperties);
    }

    private static void notNullCopyProperties(Object source, Object target, @Nullable Class<?> editable,
                                              @Nullable String... ignoreProperties) throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
                        "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (value != null && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }
                                writeMethod.invoke(target, value);
                            }
                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

}
