package cc.mcyx.arona.core.plugin;

import cc.mcyx.arona.core.command.CommandCore;
import cc.mcyx.arona.core.command.ProxyCommand;
import cc.mcyx.arona.core.listener.ListenerCore;
import cc.mcyx.arona.core.loader.AronaLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class AronaPlugin extends JavaPlugin {

    static {
        System.out.println("qwq");
        // 加载默认依赖
        AronaLoader.loadDefaultLib();
    }

    // 插件注册的命令表
    public final HashMap<String, ProxyCommand> proxyCommands = new LinkedHashMap<>();

    @Override
    public final void onLoad() {
        ListenerCore.autoSubscribe(this);
        CommandCore.autoRegistrationCommand(this);
        this.onLoaded();
    }

    @Override
    public final void onEnable() {
        this.onEnabled();
    }

    @Override
    public final void onDisable() {
        CommandCore.unRegisterPluginCommand(this);
        this.onDisabled();
    }

    public void onLoaded() {
    }


    public void onEnabled() {
    }


    public void onDisabled() {

    }

}

