package win.oreo.lifespan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import win.oreo.lifespan.util.LifeSpanUtil;
import win.oreo.lifespan.util.YmlManager;

public final class Main extends JavaPlugin implements CommandExecutor {
    public static final String prefix = "[LifeSpan] ";
    public YmlManager ymlManager;

    private LifeSpanUtil util;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            if (Bukkit.getPlayer(args[0]) != null) {
                if (LifeSpanUtil.playerLifeMap.containsKey(Bukkit.getPlayer(args[0]).getUniqueId())) {
                    sender.sendMessage(Bukkit.getPlayer(args[0]).getName() + "'s Life now (tick) : " + LifeSpanUtil.playerLifeMap.get(Bukkit.getPlayer(args[0]).getUniqueId()));
                }
            }
        }
        return false;
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new LifeSpanUtil(), this);
        this.ymlManager = new YmlManager(this);
        this.util = new LifeSpanUtil();
        util.initialize();
        getCommand("life").setExecutor(this);
        print(ChatColor.GREEN + "Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        util.save();
        print(ChatColor.RED + "Plugin Disabled!");
    }

    public static void print(String msg) {
        Bukkit.getConsoleSender().sendMessage(prefix + msg);
    }
}
