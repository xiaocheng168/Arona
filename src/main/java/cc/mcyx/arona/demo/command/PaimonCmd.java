package cc.mcyx.arona.demo.command;

import cc.mcyx.arona.core.command.annotation.Command;
import cc.mcyx.arona.core.command.annotation.CommandEvent;
import cc.mcyx.arona.core.command.event.CommandExecutor;
import cc.mcyx.arona.core.command.event.CommandTabExecutor;

import java.util.Arrays;

@Command(value = "wahaha", permission = "cc.mcyx", aliases = {"awa", "aaa"})
public class PaimonCmd {

    @CommandEvent
    public void qwq(CommandExecutor event) {
        System.out.println(event.getCommandSender());
    }

    @CommandEvent
    public void awa(CommandTabExecutor commandTabExecutor) {
        System.out.println(Arrays.toString(commandTabExecutor.getArgs()));
    }

}
