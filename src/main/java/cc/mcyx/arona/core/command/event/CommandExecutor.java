package cc.mcyx.arona.core.command.event;

import org.bukkit.command.CommandSender;

public class CommandExecutor {
    private String command;
    private String[] args;
    private CommandSender commandSender;
    private Boolean aBoolean = false;

    public CommandExecutor(String command, String[] args, CommandSender commandSender, Boolean aBoolean) {
        this.command = command;
        this.args = args;
        this.commandSender = commandSender;
        this.aBoolean = aBoolean;
    }

    public Boolean getaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(Boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }

    public void setCommandSender(CommandSender commandSender) {
        this.commandSender = commandSender;
    }
}
