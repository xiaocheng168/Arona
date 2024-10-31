package cc.mcyx.arona.core.bean;

import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class ObjBean {
    private static final HashMap<Class<?>, Object> CO = new LinkedHashMap<>();

    public static <T> T getBean(Class<T> tClass) {
        CO.putIfAbsent(tClass, newInstance(tClass));
        return (T) CO.get(tClass);
    }

    public static <T> T newInstance(Class<T> tClass) {
        try {
            return tClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
