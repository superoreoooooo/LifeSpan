package win.oreo.lifespan.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import win.oreo.lifespan.Main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LifeSpanUtil implements Listener {
    private Main plugin;

    public static HashMap<UUID, Integer> playerLifeMap = new HashMap<>();
    public static HashMap<UUID, BukkitTask> taskMap = new HashMap<>();
    public static Set<Player> playerSet = new HashSet<>();


    private static final int spawnDuration = 5 * 60 * 20;
    private static final int killDuration = 1440000;

    public void initialize() {
        plugin = JavaPlugin.getPlugin(Main.class);
        for (String id : plugin.ymlManager.getConfig().getConfigurationSection("player.").getKeys(false)) {
            UUID playerID = UUID.fromString(id);
            int tickLived = plugin.ymlManager.getConfig().getInt("player." + id + ".tickLived");
            playerLifeMap.put(playerID, tickLived);
            if (run(playerID) != null) {
                taskMap.put(playerID, run(playerID));
            }
        }
    }

    public void save() {
        plugin = JavaPlugin.getPlugin(Main.class);
        for (UUID id : playerLifeMap.keySet()) {
            plugin.ymlManager.getConfig().set("player." + id.toString() + ".tickLived", playerLifeMap.get(id));
        }
        plugin.ymlManager.saveConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        taskMap.put(e.getPlayer().getUniqueId(), run(e.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onExit(PlayerQuitEvent e) {
        if (taskMap.containsKey(e.getPlayer().getUniqueId())) {
            //Bukkit.getConsoleSender().sendMessage("task stopped! ID : " + taskMap.get(e.getPlayer().getUniqueId()).getTaskId());
            Bukkit.getScheduler().cancelTask(taskMap.get(e.getPlayer().getUniqueId()).getTaskId());
            taskMap.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (playerSet.contains(e.getPlayer())) {
            Main.print("player " + e.getPlayer().getName() + " died!");
            taskMap.remove(e.getPlayer().getUniqueId());

            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), () -> {
                //Bukkit.broadcastMessage("start!");
                taskMap.put(e.getPlayer().getUniqueId(), run(e.getPlayer().getUniqueId()));
            }, spawnDuration);
        }
    }

    public void addEffect(Player player) {
        player.sendMessage("infected!");
        playerSet.add(player);
        player.addPotionEffect(PotionEffectType.WITHER.createEffect(10000000, 9));
    }

    public BukkitTask run(UUID uuid) {
        if (Bukkit.getPlayer(uuid) != null) {
            TimerTask timer = new TimerTask(JavaPlugin.getPlugin(Main.class), 0, 20) {
                @Override
                public void run() {
                    if (playerLifeMap.containsKey(uuid)) {
                        int tickLived = playerLifeMap.get(uuid);
                        playerLifeMap.put(uuid, tickLived += 20);
                        //Bukkit.broadcastMessage(Bukkit.getPlayer(uuid).getName() + " / " + playerLifeMap.get(uuid) + " / " + taskMap.get(uuid).getTaskId());
                        if (tickLived >= killDuration) {
                            playerLifeMap.put(uuid, 0);
                            addEffect(Bukkit.getPlayer(uuid));
                            cancel();
                        }
                    } else {
                        playerLifeMap.put(uuid, 0);
                    }
                }
            };
            //Bukkit.getConsoleSender().sendMessage("run! : " + Bukkit.getPlayer(uuid).getName());
            return timer.getTask();
        } else {
            return null;
        }
    }
}
