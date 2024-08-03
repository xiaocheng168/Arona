package cc.mcyx.arona.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ProxyCommand extends Command {
    private final Object commandSubscribe;

    protected ProxyCommand(String name, Class<?> clazz) {
        super(name);
        try {
            this.commandSubscribe = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {

        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return super.tabComplete(sender, alias, args);
    }
}
