package cc.mcyx.arona.core.bean;

import cn.hutool.core.util.ClassUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class ObjBean {
    private static final HashMap<Class<?>, Object> CO = new LinkedHashMap<>();

    public static <T> T getBean(Class<T> tClass, Object... args) {

        // KOTLIN OBJECT 获取方法
        Field instance = ClassUtil.getDeclaredField(tClass, "INSTANCE");
        T bean;
        if (instance != null) {
            instance.setAccessible(true);
            try {
                bean = (T) instance.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else bean = newInstance(tClass, args);

        CO.putIfAbsent(tClass, bean);
        return (T) CO.get(tClass);
    }

    public static <T> T newInstance(Class<T> tClass, Object[] args) {
        try {
            Class<?>[] argTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) argTypes[i] = args[i].getClass();
            Constructor<T> declaredConstructor = tClass.getDeclaredConstructor(argTypes);
            declaredConstructor.setAccessible(true);
            return declaredConstructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
