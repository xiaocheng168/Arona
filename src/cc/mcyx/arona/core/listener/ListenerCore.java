package cc.mcyx.arona.core.listener;

import cc.mcyx.arona.core.AronaPlugin;
import cc.mcyx.arona.core.listener.annotation.SubscribeEvent;
import cn.hutool.core.lang.ClassScanner;
import cn.hutool.core.util.ClassUtil;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;

public class ListenerCore extends RegisteredListener implements Listener {
    public ListenerCore(Listener listener, EventExecutor executor, EventPriority priority, Plugin plugin, boolean ignoreCancelled) {
        super(listener, executor, priority, plugin, ignoreCancelled);
    }

    private static final HashMap<Class<? extends Event>, HashMap<Method, Object>> METHOD_LIST = new LinkedHashMap<>();

    private static ListenerCore LISTENER_CORE;

    public static EventExecutor getEventExe() {
        return (listener, event) -> callEventPaimon(event);
    }

    /**
     * 传递事件
     *
     * @param event 事件
     */
    public static void callEventPaimon(Event event) {
        HashMap<Method, Object> methodObjectHashMap = METHOD_LIST.get(event.getClass());
        if (methodObjectHashMap != null) {
            methodObjectHashMap.forEach((k, v) -> {
                try {
                    k.invoke(v, event);
                } catch (Throwable e) {
                    System.err.println(e.getLocalizedMessage());
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
        LISTENER_CORE = new ListenerCore(new Listener() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        }, getEventExe(), EventPriority.HIGH, plugin, true);
        bukkitAllSubscribe();
        scanPaimonEvent(plugin);
        System.out.println(METHOD_LIST);
    }

    /**
     * 扫描订阅的监听器
     *
     * @param plugin 插件实例
     */
    private static void scanPaimonEvent(AronaPlugin plugin) {
        URL pluginJar = ClassUtil.getLocation(plugin.getClass());
        try {
            JarFile jarFile = new JarFile(pluginJar.getFile());
            jarFile.stream().forEach((jarEntry) -> {
                try {
                    String jarSubFile = jarEntry.getName().replace("/", ".");
                    boolean b = jarSubFile.endsWith(".class");
                    if (b) {
                        Class<?> aClass = Class.forName(jarSubFile.replace(".class", ""));
                        // 是否为监听器
                        boolean isListener = aClass.isAnnotationPresent(cc.mcyx.arona.core.listener.annotation.Listener.class);
                        if (isListener) {
                            Object listenerObj = aClass.newInstance();
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
                                        METHOD_LIST.putIfAbsent(eventClass, new HashMap<>());
                                        METHOD_LIST.get(eventClass).put(method, listenerObj);
                                    }
                                }
                            }
                        }
                    }
                } catch (Throwable ignored) {
                }
            });
        } catch (Throwable e) {
            System.err.println(e.getLocalizedMessage());
        }

    }

    /**
     * 注册Bukkit所有监听器
     */
    private static void bukkitAllSubscribe() {
        // 扫描 bukkit event 事件
        Set<Class<?>> classes = ClassScanner.scanAllPackageBySuper("org.bukkit.event", Event.class);
        for (Class<?> aClass : classes) {
            try {
                Field handlers = aClass.getDeclaredField("handlers");
                handlers.setAccessible(true);
                HandlerList handlerList = (HandlerList) handlers.get(null);
                handlerList.register(ListenerCore.LISTENER_CORE);
            } catch (Throwable ignored) {
            }
        }
    }
}
