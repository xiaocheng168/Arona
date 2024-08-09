package cc.mcyx.arona.core.plugin;

import cc.mcyx.arona.core.listener.ListenerCore;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AronaPlugin extends JavaPlugin {
    @Override
    public final void onLoad() {
        ListenerCore.autoSubscribe(this);
//        CommandCore.autoRegistrationCommand(this);
        this.onLoaded();
    }

    @Override
    public final void onEnable() {
        this.onEnabled();
    }

    @Override
    public final void onDisable() {
        this.onDisabled();
    }

    public void onLoaded() {
    }


    public void onEnabled() {
    }


    public void onDisabled() {

    }
}

