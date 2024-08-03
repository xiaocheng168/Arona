package cc.mcyx.core.loader;

import cc.mcyx.arona.core.AronaPlugin;
import cn.hutool.core.util.ClassUtil;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarFile;

public abstract class ClassUtils {

    private static final Set<Class<?>> classs = new LinkedHashSet<>();

    /**
     * 获取插件的所有加载类
     *
     * @param plugin 插件
     * @return 返回所有加载类
     */
    public static Set<Class<?>> getPluginLoadClass(AronaPlugin plugin) {
        if (!classs.isEmpty()) return classs;
        URL pluginJar = ClassUtil.getLocation(plugin.getClass());
        try {
            JarFile jarFile = new JarFile(pluginJar.getFile());
            jarFile.stream().forEach((jarEntry) -> {
                try {
                    String jarSubFile = jarEntry.getName().replace("/", ".");
                    boolean b = jarSubFile.endsWith(".class");
                    if (b) {
                        Class<?> aClass = Class.forName(jarSubFile.replace(".class", ""));
                        classs.add(aClass);
                    }
                } catch (Throwable ignored) {
                }
            });
        } catch (Throwable e) {
            System.err.println(e.getLocalizedMessage());
        }
        return classs;
    }
}
