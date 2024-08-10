package cc.mcyx.arona.demo.command

import cc.mcyx.arona.core.command.annotation.Command
import cc.mcyx.arona.core.command.annotation.CommandEvent
import cc.mcyx.arona.core.command.event.CommandExecutor

@Command("qwq")
class Qwq {
    @CommandEvent
    fun qwq(commandExecutor: CommandExecutor) {
        commandExecutor.setaBoolean(false)
        commandExecutor.commandSender.sendMessage("halo~~~~ ${System.currentTimeMillis()}")
    }
}