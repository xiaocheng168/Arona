package cc.mcyx.arona.demo.command;

import cc.mcyx.arona.core.command.annotation.Command;
import cc.mcyx.arona.core.command.annotation.CommandEvent;
import cc.mcyx.arona.core.command.event.CommandExecutor;
import cc.mcyx.arona.core.command.event.CommandTabExecutor;

@Command(value = "qwq", permission = "cc.mcyx")
public class PaimonCmd {

    @Command(value = "qwq", permission = "cc.mcyx")
    public PaimonCmd qwq = new PaimonCmd();


    @CommandEvent
    public void qwq(CommandExecutor event) {

    }

    @CommandEvent
    public void awa(CommandTabExecutor commandTabExecutor) {

    }
}
