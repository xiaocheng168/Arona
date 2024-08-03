package cc.mcyx.arona.core.command;

import cc.mcyx.arona.core.AronaPlugin;
import cc.mcyx.arona.core.command.annotation.Command;
import cn.hutool.core.util.ClassUtil;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

public abstract class CommandCore {
    public static void autoRegistrationCommand(AronaPlugin aronaPlugin) {
        Set<Class<?>> pluginLoadClass = cc.mcyx.core.loader.ClassUtils.getPluginLoadClass(aronaPlugin);
        for (Class<?> loadClass : pluginLoadClass) {
            boolean isCommand = loadClass.isAnnotationPresent(Command.class);
            if (isCommand) {
                registerCommand(loadClass);
            }
        }
    }

    /**
     * 注册一个命令
     *
     * @param c 类
     */
    public static void registerCommand(Class<?> c) {
        Class<?> craftServerClass = scanNmsClass("CraftServer");
        if (craftServerClass == null) return;
        Object craftServer = craftServerClass.cast(Bukkit.getServer());
        try {
            Field declaredField = craftServerClass.getDeclaredField("commandMap");
            declaredField.setAccessible(true);
            Object commandMap = declaredField.get(craftServer);
            Method getKnownCommands = commandMap.getClass().getDeclaredMethod("getKnownCommands");
            Object invoke = getKnownCommands.invoke(commandMap);
            HashMap<String, org.bukkit.command.Command> knownCommands = (HashMap<String, org.bukkit.command.Command>) invoke;
            Command commandInfo = c.getAnnotation(Command.class);
            knownCommands.put("qwq", new ProxyCommand(commandInfo.value(), c));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    public static Class<?> scanNmsClass(String className) {
        for (Class<?> aClass : ClassUtil.scanPackage("org.bukkit.craftbukkit")) {
            if (aClass.getSimpleName().equals(className)) return aClass;
        }
        return Object.class;
    }
}
