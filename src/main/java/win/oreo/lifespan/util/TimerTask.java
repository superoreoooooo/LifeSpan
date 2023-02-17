package win.oreo.lifespan.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class TimerTask implements Runnable{

    private BukkitTask task;

    public TimerTask(JavaPlugin plugin, int a, int b) {
        task = Bukkit.getScheduler().runTaskTimer(plugin, this, a, b);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(task.getTaskId());
    }

    public BukkitTask getTask() {
        return task;
    }
}
