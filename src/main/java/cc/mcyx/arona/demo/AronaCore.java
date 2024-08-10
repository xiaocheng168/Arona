package cc.mcyx.arona.demo;

import cc.mcyx.arona.core.listener.annotation.Listener;
import cc.mcyx.arona.core.listener.annotation.SubscribeEvent;
import cc.mcyx.arona.core.plugin.AronaPlugin;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Listener
public class AronaCore extends AronaPlugin {
    @Override
    public void onEnabled() {
        System.out.println("加载!");
    }

    @SubscribeEvent
    public void qwq(PlayerJoinEvent event) {
        System.out.println(event.getPlayer());
    }

    @SubscribeEvent
    public void aaa(PlayerQuitEvent event) {
        System.out.println(event.getPlayer());
    }

    @SubscribeEvent
    public void qwq(PlayerDropItemEvent event) {
        System.out.println(event.getItemDrop().getItemStack());
    }
}
