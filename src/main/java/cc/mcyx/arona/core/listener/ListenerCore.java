package cc.mcyx.arona.core.listener;

import cc.mcyx.arona.core.bean.ObjBean;
import cc.mcyx.arona.core.loader.ClassUtils;
import cc.mcyx.arona.core.plugin.AronaPlugin;
import cc.mcyx.arona.core.listener.annotation.SubscribeEvent;
import cn.hutool.core.util.ClassUtil;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ListenerCore extends RegisteredListener implements Listener {
    public ListenerCore(Listener listener, EventExecutor executor, EventPriority priority, Plugin plugin, boolean ignoreCancelled) {
        super(listener, executor, priority, plugin, ignoreCancelled);
    }

    // 事件监听表
    // MAX KEY 注册插件实例
    // K 时间
    // V -> <K 方法 , V 注册在哪个对象里>
    private static final HashMap<AronaPlugin, HashMap<Class<? extends Event>, HashMap<Method, Object>>> METHOD_LIST = new LinkedHashMap<>();

    public static EventExecutor getEventExe(AronaPlugin aronaPlugin) {
        return (listener, event) -> callEventPaimon(event, aronaPlugin);
    }

    /**
     * 传递事件
     *
     * @param event 事件
     */
    public static void callEventPaimon(Event event, AronaPlugin aronaPlugin) {
        HashMap<Method, Object> methodObjectHashMap = METHOD_LIST.get(aronaPlugin).get(event.getClass());
        if (methodObjectHashMap != null && !methodObjectHashMap.isEmpty()) {
            methodObjectHashMap.forEach((k, v) -> {
                try {
                    k.invoke(v, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("事件执行错误", e);
                }
            });
        }
    }

    /**
     * 注册监听器处理器
     *
     * @param plugin 插件
     */
    public static void autoSubscribe(AronaPlugin plugin) {
        METHOD_LIST.putIfAbsent(plugin, new LinkedHashMap<>());
        // 扫描插件有订阅哪些事件
        scanListenerClass(plugin);
        // 再将扫描出来的注册到监听器里
        bukkitAllSubscribe(plugin);
    }

    /**
     * 扫描订阅的监听器
     *
     * @param plugin 插件实例
     */
    private static void scanListenerClass(AronaPlugin plugin) {
        try {
            // 扫描插件里所有注册的监听器
            Set<Class<?>> listenerClass = ClassUtils.getJarClassAnnotation(plugin, cc.mcyx.arona.core.listener.annotation.Listener.class);
            for (Class<?> aClass : listenerClass) {
                Object listenerObj;
                // KOTLIN OBJECT 获取方法
                Field instance = ClassUtil.getDeclaredField(aClass, "INSTANCE");
                if (instance != null) {
                    instance.setAccessible(true);
                    listenerObj = instance.get(null);
                } else {
                    listenerObj = aClass.getSuperclass() == AronaPlugin.class ? plugin : ObjBean.getBean(aClass);
                }
                // 如果是监听类，那么就获取里面的订阅事件
                List<Method> methods = new LinkedList<>(Arrays.asList(aClass.getDeclaredMethods()));
                // 判断此类方法是否存在订阅事件
                for (Method method : methods) {
                    if (method.isAnnotationPresent(SubscribeEvent.class)) {
                        method.setAccessible(true);
                        // 如果事件订阅了,但是没有声明事件
                        if (0 == method.getParameterCount()) {
                            System.err.printf("无效的订阅事件 位置 %s\n", method);
                        } else {
                            Class<Event> eventClass = (Class<Event>) method.getParameters()[0].getType();
                            METHOD_LIST.get(plugin).putIfAbsent(eventClass, new HashMap<>());
                            METHOD_LIST.get(plugin).get(eventClass).put(method, listenerObj);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            System.err.printf("扫描异常 %s", e.getLocalizedMessage());
        }

    }

    /**
     * 对应注册插件监听的所有事件
     */
    private static void bukkitAllSubscribe(AronaPlugin plugin) {
        for (Map.Entry<Class<? extends Event>, HashMap<Method, Object>> classHashMapEntry : METHOD_LIST.get(plugin).entrySet()) {
            for (Map.Entry<Method, Object> methodObjectEntry : classHashMapEntry.getValue().entrySet()) {
                Method method = methodObjectEntry.getKey();
                SubscribeEvent subscribeEvent = (SubscribeEvent) method.getAnnotations()[0];
                if (method.getParameterCount() > 0) {
                    // 获取事件类
                    Class<?> eventClass = method.getParameters()[0].getType();
                    // 注入 Bukkit 监听器
                    try {
                        Field handlers = eventClass.getDeclaredField("handlers");
                        handlers.setAccessible(true);
                        HandlerList handlerList = (HandlerList) handlers.get(null);
                        handlerList.register(new ListenerCore(new Listener() {
                        }, getEventExe(plugin), subscribeEvent.priority(), plugin, subscribeEvent.ignoreCancelled()));
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * 判断这个类以及继承类是否为 Event
     *
     * @param clazz 类
     * @return 是否为 Event 事件类
     */
    public static boolean isSupperEvent(Class<?> clazz) {
        if (clazz == Object.class) return false;
        if (clazz.getSuperclass() == org.bukkit.event.Event.class) return true;
        return isSupperEvent(clazz.getSuperclass());
    }
}
