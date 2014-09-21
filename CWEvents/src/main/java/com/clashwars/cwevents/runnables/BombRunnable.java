package com.clashwars.cwevents.runnables;

import com.clashwars.cwevents.events.Bomberman;
import com.clashwars.cwevents.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


public class BombRunnable extends BukkitRunnable {

    private Bomberman bom;
    private Player player;
    private Location loc;
    private long fuseTime;
    private long ticks;

    public BombRunnable(Bomberman bom, Player player, Location loc, long fuseTime) {
        this.bom = bom;
        this.player = player;
        this.loc = loc;
        this.fuseTime = fuseTime;
        ticks = fuseTime;
    }


    @Override
    public void run() {
        int increaseTicks = 2;
        float pitch = 2.0f;
        int particles = 20;
        for (int i = 1; i < fuseTime; i += increaseTicks) {
            if (ticks == i) {
                loc.getWorld().playSound(loc, Sound.CLICK, 0.3f, pitch);
                ParticleEffect.SMOKE.display(loc, 0.2f, 0.4f, 0.2f, 0.001f, particles);
            }
            particles--;
            pitch -= 0.1f;
            increaseTicks += 4;
        }

        if (ticks <= 0) {
            bom.bombExplode(player, loc);
            this.cancel();
        }
        ticks--;
    }
}
