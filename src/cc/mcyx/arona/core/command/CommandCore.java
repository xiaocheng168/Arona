package cc.mcyx.arona.core.command;

import cc.mcyx.arona.core.loader.ClassUtils;
import cc.mcyx.arona.core.plugin.AronaPlugin;
import cc.mcyx.arona.core.command.annotation.Command;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.VersionCommand;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

public abstract class CommandCore {
    public static void autoRegistrationCommand(AronaPlugin aronaPlugin) {
        Set<Class<?>> pluginLoadClass = ClassUtils.getPluginLoadClass(aronaPlugin);
        for (Class<?> loadClass : pluginLoadClass) {
            // 必须有注解
            if (loadClass.getAnnotations().length > 0) {
                Command annotation = loadClass.getAnnotation(Command.class);
                // 注册命令
                if (ObjectUtil.isNotNull(annotation)) registerCommand(loadClass, aronaPlugin);
            }
        }
    }

    /**
     * 注册一个命令
     *
     * @param c 类
     */
    public static void registerCommand(Class<?> c, AronaPlugin aronaPlugin) {
        Class<?> craftServerClass = scanNmsClass("CraftServer");
        if (ObjectUtil.isNull(craftServerClass)) return;
        Object craftServer = craftServerClass.cast(Bukkit.getServer());
        try {
            Field declaredField = craftServerClass.getDeclaredField("commandMap");
            declaredField.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) declaredField.get(craftServer);
            Command command = c.getAnnotation(Command.class);
            commandMap.register(aronaPlugin.getName(), new ProxyCommand(command.value(), c));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 取消注册命令
     *
     * @param proxyCommand 命令类
     */
    public static void unregisterCommand(ProxyCommand proxyCommand) {

    }


    public static Class<?> scanNmsClass(String className) {
        for (Class<?> aClass : ClassUtil.scanPackage("org.bukkit.craftbukkit")) {
            if (aClass.getSimpleName().equals(className)) return aClass;
        }
        return Object.class;
    }
}
