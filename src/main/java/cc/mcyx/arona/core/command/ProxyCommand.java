package cc.mcyx.arona.core.command;

import cc.mcyx.arona.core.bean.ObjBean;
import cc.mcyx.arona.core.command.annotation.Command;
import cn.hutool.core.util.ObjectUtil;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.*;

public class ProxyCommand extends BaseCommand {
    // 命令订阅类
    private final BaseCommand bc;
    // 子命令
    private final HashMap<String, ProxyCommand> subCommandList = new LinkedHashMap<>();

    protected ProxyCommand(String name, Class<?> clazz, cc.mcyx.arona.core.command.annotation.Command command) {
        super(name);
        try {
            this.bc = (BaseCommand) ObjBean.getBean(clazz);
            this.bc.setName(this.getName());
            this.setDescription(command.description());
            this.setPermission(command.permission());
            this.setPermissionMessage(command.noPermission());
            this.setAliases(Arrays.asList(command.aliases()));
            this.scanSubCommand();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 递归扫描子命令
     */
    public void scanSubCommand() {
        for (Field declaredField : bc.getClass().getDeclaredFields()) {
            System.out.println(declaredField);
            Command command = declaredField.getDeclaredAnnotation(Command.class);
            if (ObjectUtil.isNotNull(command)) {
                String name = command.value();
                Class<?> type = declaredField.getType();
                subCommandList.put(name, new ProxyCommand(name, type, command));
            }
        }
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!commandSender.hasPermission(this.getPermission())) {
            commandSender.sendMessage(this.getPermissionMessage());
            return false;
        }
        return this.bc.execute(commandSender, s, strings);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return this.bc.tabComplete(sender, alias, args);
    }

    public List<String> subCommandList() {
        return new ArrayList<>(this.subCommandList.keySet());
    }
}
