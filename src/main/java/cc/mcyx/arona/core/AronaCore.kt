package cc.mcyx.arona.core

import cc.mcyx.arona.core.listener.annotation.Listener
import cc.mcyx.arona.core.plugin.AronaPlugin

@Listener
class AronaCore : AronaPlugin() {
    override fun onEnabled() {
        println("qwq")
    }
}