package cc.mcyx.arona.core.command;

import cc.mcyx.arona.core.command.annotation.CommandEvent;
import cc.mcyx.arona.core.command.event.CommandExecutor;
import cc.mcyx.arona.core.command.event.CommandTabExecutor;
import cn.hutool.core.util.ObjectUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;

public class ProxyCommand extends Command {
    private final Object commandSubscribe;

    private Method commandExecutorMethod;

    private Method commandTabExecutorMethod;

    protected ProxyCommand(String name, Class<?> clazz) {
        super(name);
        try {
            this.commandSubscribe = clazz.newInstance();
            for (Method declaredMethod : this.commandSubscribe.getClass().getDeclaredMethods()) {
                CommandEvent annotation = declaredMethod.getAnnotation(CommandEvent.class);
                if (ObjectUtil.isNotNull(annotation) && declaredMethod.getParameterCount() == 1) {
                    Parameter parameter = declaredMethod.getParameters()[0];
                    if (parameter.getType() == CommandExecutor.class) {
                        this.commandExecutorMethod = declaredMethod;
                        this.commandExecutorMethod.setAccessible(true);
                    }
                    if (parameter.getType() == CommandTabExecutor.class) {
                        this.commandTabExecutorMethod = declaredMethod;
                        this.commandTabExecutorMethod.setAccessible(true);
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        try {
            CommandExecutor commandExecutor = new CommandExecutor(s, strings, commandSender, false);
            commandExecutorMethod.invoke(commandSubscribe, commandExecutor);
            return commandExecutor.getaBoolean();
        } catch (IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        try {
            CommandTabExecutor commandTabExecutor = new CommandTabExecutor(sender, args, new LinkedList<>());
            commandTabExecutorMethod.invoke(commandSubscribe, commandTabExecutor);
            return commandTabExecutor.getCallbacks();
        } catch (IllegalAccessException | InvocationTargetException e) {
            return super.tabComplete(sender, alias, args);
        }
    }
}
