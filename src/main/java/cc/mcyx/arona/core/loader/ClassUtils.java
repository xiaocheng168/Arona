package cc.mcyx.arona.core.loader;

import cc.mcyx.arona.core.listener.ListenerCore;
import cc.mcyx.arona.core.plugin.AronaPlugin;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class ClassUtils {

    private static final Set<Class<?>> classs = new LinkedHashSet<>();

    /**
     * 获取插件的所有加载类
     *
     * @param plugin 插件
     * @return 返回所有加载类
     */
    public static Set<Class<?>> getPluginLoadClass(JavaPlugin plugin) {
        if (!classs.isEmpty()) return classs;
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
                    classs.add(aClass);
                }
            }
        } catch (Throwable ignored) {
        }

        return classs;
    }

    public static Set<Class<?>> getJarClass(JavaPlugin javaPlugin) {
        LinkedHashSet<Class<?>> clazzs = new LinkedHashSet<>();
        try {
            Field knownCommands = JavaPlugin.class.getDeclaredField("file");
            knownCommands.setAccessible(true);
            File pluginFile = (File) knownCommands.get(javaPlugin);
            JarFile jarFile = new JarFile(pluginFile);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String clazz = jarEntry.getName().replace("/", ".");
                if (clazz.endsWith(".class")) {
                    try {
                        String dClass = clazz.replace(".class", "");
                        if (dClass.startsWith("kotlin")) continue; // 过滤 kotlin
                        Class<?> aClass = Class.forName(dClass, false, javaPlugin.getClass().getClassLoader());
                        if (ListenerCore.isSupperEvent(aClass)) clazzs.add(Class.forName(dClass));
                    } catch (Throwable ignored) {
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        return clazzs;
    }


}
