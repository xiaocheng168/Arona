package cc.mcyx.arona.nms

import cc.mcyx.arona.nms.craftbukkit.CraftBukkitPacket
import org.bukkit.entity.Player


/**
 * 发送数据包给一个玩家
 * @param packet 数据包类
 */
fun Player.sendPacket(packet: Any) {
    CraftBukkitPacket.craftPlayer.cast(this).apply {
        val handle = javaClass.getDeclaredMethod("getHandle").invoke(this)
        val playerConnection = CraftBukkitPacket.getObject(handle, "PlayerConnection")
        playerConnection.javaClass.getDeclaredMethod(
            if (CraftBukkitPacket.serverId > 1170) "a" else "sendPacket",
            CraftBukkitPacket.packet
        )
            .also { it.isAccessible = true }.invoke(playerConnection, packet)
    }
}