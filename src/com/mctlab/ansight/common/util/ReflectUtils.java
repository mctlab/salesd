package com.mctlab.ansight.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtils {

    public static Object invokeMethod(Object instance, String name, Class<?>[] paramTypes, Object[] paramValues)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = instance.getClass();
        Method method = getMethod(clazz, name, paramTypes);
        method.setAccessible(true);
        return method.invoke(instance, paramValues);
    }

    public static Object invokeStaticMethod(Class<?> clazz, String name, Class<?>[] paramTypes, Object[] paramValues)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getMethod(clazz, name, paramTypes);
        method.setAccessible(true);
        return method.invoke(clazz, paramValues);
    }

    public static void setFieldValue(Object instance, String name, Object value) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = instance.getClass();
        Field field = getField(clazz, name);
        field.setAccessible(true);
        field.set(instance, value);
    }

    public static void setStaticFieldValue(Class<?> clazz, String name, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = getField(clazz, name);
        field.setAccessible(true);
        field.set(clazz, value);
    }

    public static Object getFieldValue(Object instance, String name) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = instance.getClass();
        Field field = getField(clazz, name);
        field.setAccessible(true);
        return field.get(instance);
    }

    public static Object getStaticFieldValue(Class<?> clazz, String name) throws NoSuchFieldException, IllegalAccessException {
        Field field = getField(clazz, name);
        field.setAccessible(true);
        return field.get(clazz);
    }

    public static List<Field> getFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();
        for (Field field : clazz.getDeclaredFields()) {
            fields.add(field);
        }
        if ((clazz != Object.class) && (clazz.getSuperclass() != null)) {
            fields.addAll(getFields(clazz.getSuperclass()));
        }
        return fields;
    }

    private static Method getMethod(Class<?> clazz, String name, Class<?>[] paramTypes) throws NoSuchMethodException {
        try {
            return clazz.getDeclaredMethod(name, paramTypes);
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null) {
                return getMethod(clazz.getSuperclass(), name, paramTypes);
            } else {
                throw e;
            }
        }
    }

    private static Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return getField(clazz.getSuperclass(), name);
            } else {
                throw e;
            }
        }
    }
}
