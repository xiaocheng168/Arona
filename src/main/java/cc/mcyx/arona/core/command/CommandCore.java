package cc.mcyx.arona.core.command;

import cc.mcyx.arona.core.loader.ClassUtils;
import cc.mcyx.arona.core.plugin.AronaPlugin;
import cc.mcyx.arona.core.command.annotation.Command;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.*;

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
     * @param aronaPlugin 插件实例
     */
    public static void registerCommand(Class<?> c, AronaPlugin aronaPlugin) {
        try {
            Command command = c.getAnnotation(Command.class);
            ProxyCommand proxyCommand = new ProxyCommand(command.value(), c, command);
            getCommandMap().register(aronaPlugin.getName(), proxyCommand);
            aronaPlugin.proxyCommands.put(proxyCommand.getName(), proxyCommand);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取 Bukkit 命令表
     * @return 返回 Bukkit 命令处理类
     */
    private static SimpleCommandMap getCommandMap() {
        Class<?> craftServerClass = scanNmsClass("CraftServer");
        if (ObjectUtil.isNull(craftServerClass)) throw new IllegalStateException("没有找到CraftServer!");
        Object craftServer = craftServerClass.cast(Bukkit.getServer());
        try {
            Field declaredField = craftServerClass.getDeclaredField("commandMap");
            declaredField.setAccessible(true);
            return (SimpleCommandMap) declaredField.get(craftServer);
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
        proxyCommand.unregister(getCommandMap());
    }


    /**
     * 取消注册插件的所有命令
     * @param aronaPlugin 插件实例
     */
    public static void unRegisterPluginCommand(AronaPlugin aronaPlugin) {
        for (Map.Entry<String, ProxyCommand> stringProxyCommandEntry : aronaPlugin.proxyCommands.entrySet()) {
            unregisterCommand(stringProxyCommandEntry.getValue());
        }
    }


    public static Class<?> scanNmsClass(String className) {
        for (Class<?> aClass : ClassUtil.scanPackage("org.bukkit.craftbukkit")) {
            if (aClass.getSimpleName().equals(className)) return aClass;
        }
        return Object.class;
    }
}
