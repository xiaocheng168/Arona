package cc.mcyx.arona.gui

import org.bukkit.entity.Player

fun Player.openAGui(aGui: AGui) {
    this.openInventory(aGui)
}