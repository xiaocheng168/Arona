package cc.mcyx.arona.core.command.event;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CommandTabExecutor {
    private CommandSender sender;
    private String[] args;
    private List<String> callbacks = Collections.emptyList();


    public CommandTabExecutor(CommandSender sender, String[] args, List<String> callbacks) {
        this.sender = sender;
        this.args = args;
        this.callbacks = callbacks;
    }

    public CommandSender getSender() {
        return sender;
    }

    public void setSender(CommandSender sender) {
        this.sender = sender;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public List<String> getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(List<String> callbacks) {
        this.callbacks = callbacks;
    }
}
