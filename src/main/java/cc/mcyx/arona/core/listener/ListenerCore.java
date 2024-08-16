package cc.mcyx.arona.core.listener;

import cc.mcyx.arona.core.loader.ClassUtils;
import cc.mcyx.arona.core.plugin.AronaPlugin;
import cc.mcyx.arona.core.listener.annotation.SubscribeEvent;
import cn.hutool.core.lang.ClassScanner;
import cn.hutool.core.util.ClassUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ListenerCore extends RegisteredListener implements Listener {
    public ListenerCore(Listener listener, EventExecutor executor, EventPriority priority, Plugin plugin, boolean ignoreCancelled) {
        super(listener, executor, priority, plugin, ignoreCancelled);
    }

    private static final HashMap<Class<? extends Event>, HashMap<Method, Object>> METHOD_LIST = new LinkedHashMap<>();

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
        // 扫描服务器全部可注册事件
        scanServerRegisterEvent();
        // 扫描插件有订阅哪些事件
        scanPaimonEvent(plugin);
        // 再将扫描出来的注册到监听器里
        bukkitAllSubscribe(plugin);
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
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String jarSubFile = jarEntry.getName().replace("/", ".");
                boolean b = jarSubFile.endsWith(".class");
                if (b) {
                    Class<?> aClass = Class.forName(jarSubFile.replace(".class", ""));
                    // 是否为监听器
                    boolean isListener = aClass.isAnnotationPresent(cc.mcyx.arona.core.listener.annotation.Listener.class);
                    if (isListener) {
                        Object listenerObj = aClass.getSuperclass() == AronaPlugin.class ? plugin : aClass.newInstance();
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
            }
        } catch (Throwable e) {
            System.err.printf("扫描异常 %s", e.getLocalizedMessage());
        }

    }

    /**
     * 对应注册插件监听的所有事件
     */
    private static void bukkitAllSubscribe(AronaPlugin plugin) {
        for (Map.Entry<Class<? extends Event>, HashMap<Method, Object>> classHashMapEntry : METHOD_LIST.entrySet()) {
            for (Map.Entry<Method, Object> methodObjectEntry : classHashMapEntry.getValue().entrySet()) {
                Method method = methodObjectEntry.getKey();
                SubscribeEvent subscribeEvent = (SubscribeEvent) method.getAnnotations()[0];
                if (method.getParameterCount() > 0) {
                    Parameter parameter = method.getParameters()[0];
                    Class<?> event = parameter.getType();
                    Class<?> eventClass = getEventClass(event);
                    // 注入 Bukkit 监听器
                    try {
                        Field handlers = eventClass.getDeclaredField("handlers");
                        handlers.setAccessible(true);
                        HandlerList handlerList = (HandlerList) handlers.get(null);
                        handlerList.register(new ListenerCore(new Listener() {
                        }, getEventExe(), subscribeEvent.priority(), plugin, subscribeEvent.ignoreCancelled()));
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private static final HashMap<Class<?>, Class<?>> EVENT_CLASS_MAP = new LinkedHashMap<>();


    /**
     * 扫描服务器全部插件的事件
     */
    private static void scanServerRegisterEvent() {
        // 如果不是空的，则不再扫描
        if (!EVENT_CLASS_MAP.isEmpty()) return;
        // 扫描 Bukkit 内置事件
        ClassScanner.scanAllPackage("org.bukkit.event", aClass -> true).forEach(clazz -> EVENT_CLASS_MAP.put(clazz, clazz));
        // 扫描服务器全部插件
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            JavaPlugin javaPlugin = (JavaPlugin) plugin;
            Set<Class<?>> pluginLoadClass = ClassUtils.getJarClass(javaPlugin);
            pluginLoadClass.forEach((aClass -> EVENT_CLASS_MAP.put(aClass, aClass)));
        }
    }

    /**
     * 判断这个类以及继承类是否为 Event
     * @param clazz 类
     * @return 是否为 Event 事件类
     */
    public static boolean isSupperEvent(Class<?> clazz) {
        if (clazz == Object.class) return false;
        if (clazz.getSuperclass() == org.bukkit.event.Event.class) return true;
        return isSupperEvent(clazz.getSuperclass());
    }

    /**
     * 获取事件类
     * @param scanClass 事件
     * @return 返回该事件，如果没有异常
     */
    private static Class<?> getEventClass(Class<?> scanClass) {
        Class<?> aClass = EVENT_CLASS_MAP.get(scanClass);
        if (aClass != null) return aClass;
        throw new RuntimeException("未知订阅事件类 " + scanClass);
    }
}
