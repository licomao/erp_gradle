package com.daqula.carmore.util;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BeanUtil {

    public static <E> void copyFields(E source, E target, String... ignoreFields) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        List<String> ignoreList = (ignoreFields != null ? Arrays.asList(ignoreFields) : null);

        for (Field field : source.getClass().getFields()) {
            try {
                if (Modifier.isStatic(field.getModifiers())
                        || Modifier.isPrivate(field.getModifiers())
                        || Modifier.isProtected(field.getModifiers())
                        || (ignoreList != null && ignoreList.contains(field.getName()))) continue;

                field.set(target, field.get(source));
            } catch (Exception e) {
                throw new RuntimeException(
                        "Convert field "+ field.getName()+" to SFSObject failed.", e);
            }
        }
    }

    public static <T> Collection<T> getBeans(ListableBeanFactory beanFactory,
                                              Class<T> type) {
        return BeanFactoryUtils.beansOfTypeIncludingAncestors(beanFactory, type).values();
    }

    public static Object getPrivateField(Object target, String fieldName) {
        return getPrivateField(target, target.getClass(), fieldName);
    }

    private static Object getPrivateField(Object target, Class clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (NoSuchFieldException nsfe) {
            if (clazz.getSuperclass() != null) {
                return getPrivateField(target, clazz.getSuperclass(), fieldName);
            } else {
                throw new RuntimeException("Get Private Field "+fieldName+" failed.", nsfe);
            }
        } catch (Exception e) {
            throw new RuntimeException("Get Private Field "+fieldName+" failed.", e);
        }
    }

    public static void setPrivateField(Object target, String fieldName, Object value) {
        setPrivateField(target, target.getClass(), fieldName, value);
    }

    private static void setPrivateField(Object target, Class clazz, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException nsfe) {
            if (clazz.getSuperclass() != null) {
                setPrivateField(target, clazz.getSuperclass(), fieldName, value);
            } else {
                throw new RuntimeException("Set Private Field "+fieldName+" failed.", nsfe);
            }
        } catch (Exception e) {
            throw new RuntimeException("Set Private Field "+fieldName+" failed.", e);
        }
    }
}
