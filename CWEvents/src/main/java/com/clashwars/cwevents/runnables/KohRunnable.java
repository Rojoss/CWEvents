package com.clashwars.cwevents.runnables;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwevents.events.KOH;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KohRunnable extends BukkitRunnable {

    private final int CAPTURE_TIME = 10;

    private KOH koh;
    private Player capturer;
    private int seconds;
    private boolean running = false;



    public KohRunnable(KOH koh) {
        this.koh = koh;
        seconds = CAPTURE_TIME;
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
        koh.em.broadcast(CWUtil.formatMsg("&5" + capturer.getName() + " &6started to capture the hill!"));
    }

    @Override
    public void run() {
        if (!running || capturer == null) {
            return;
        }

        if (seconds == 8 || seconds == 5 || seconds <= 3) {
            koh.em.broadcast(CWUtil.formatMsg("&5" + capturer.getName() + " &6is the king of the hill in &a&l"+ seconds + "&7..."));
        }
        capturer.getWorld().playSound(capturer.getLocation(), Sound.NOTE_PLING, 10, 1.8f);
        //TODO: Particle at center of region

        if (seconds <= 0) {
            koh.capture(capturer);
            this.cancel();
        }
        seconds--;
    }
}
