package cc.mcyx.arona.demo.command;

import cc.mcyx.arona.core.command.annotation.Command;
import cc.mcyx.arona.core.command.annotation.CommandEvent;
import cc.mcyx.arona.core.command.event.CommandExecutor;
import cc.mcyx.arona.core.command.event.CommandTabExecutor;

import java.util.Arrays;

@Command(value = "qwq", permission = "cc.mcyx")
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
