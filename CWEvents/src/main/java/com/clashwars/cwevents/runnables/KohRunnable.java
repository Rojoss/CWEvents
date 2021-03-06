package com.clashwars.cwevents.runnables;

import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwevents.Util;
import com.clashwars.cwevents.events.KOH;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class KohRunnable extends BukkitRunnable {

    private final int CAPTURE_TIME = 10;

    private KOH koh;
    private Player capturer;
    private int seconds;
    private boolean running = false;
    private Location hillCenter;


    public KohRunnable(KOH koh) {
        ProtectedRegion region = CWWorldGuard.getRegion(koh.world, koh.em.getRegionName("hill"));
        Vector centerVector = region.getMinimumPoint().add(region.getMaximumPoint()).divide(2).floor();
        hillCenter = new Location(koh.world, centerVector.getX(), centerVector.getY(), centerVector.getZ());
        this.koh = koh;
    }

    public void stopCapture() {
        this.capturer = null;
        running = false;
        seconds = CAPTURE_TIME;
    }

    public void startCapture(Player capturer) {
        this.capturer = capturer;
        running = true;
        seconds = CAPTURE_TIME;
        koh.em.broadcast(Util.formatMsg("&5" + capturer.getName() + " &6started to capture the hill!"));
    }


    @Override
    public void run() {
        if (!running || capturer == null) {
            return;
        }

        if (seconds == 8 || seconds == 5 || seconds == 3 || seconds == 2 || seconds == 1) {
            koh.em.broadcast(Util.formatMsg("&5" + capturer.getName() + " &6is the king of the hill in &a&l" + seconds + "&7..."));
        }
        koh.em.playSound(Sound.NOTE_PLING, 0.2f, 1.8f);
        ParticleEffect.FIREWORKS_SPARK.display(hillCenter, 1.5f, 2.0f, 1.5f, 0.1f, 50);

        if (seconds <= 0) {
            koh.capture(capturer);
            this.cancel();
        }
        seconds--;
    }
}
