package com.clashwars.cwevents.runnables;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.clashwars.cwevents.events.Bomberman;
import com.clashwars.cwevents.utils.ParticleEffect;


public class BombRunnable extends BukkitRunnable {
	
	private Bomberman bom;
	private Player player;
	private Location loc;
	private long ticks;
	
	public BombRunnable(Bomberman bom, Player player, Location loc, long ticks) {
		this.bom = bom;
		this.player = player;
		this.loc = loc;
		this.ticks = ticks;
	}
	
	
	@Override
	public void run() {
		int increaseTicks = 2;
		float pitch = 2.0f;
		int particles = 20;
		for (int i = 1; i < 100; i += increaseTicks) {
			if (ticks == i) {
				loc.getWorld().playSound(loc, Sound.CLICK, 0.5f, pitch);
				ParticleEffect.SMOKE.display(loc, 0.5f, 0.5f, 0.5f, 0.001f, particles);
			}
			particles--;
			pitch -= 0.1f;
			increaseTicks++;
		}
		
		if (ticks <= 0) {
			bom.bombExplode(player, loc);
		}
		ticks--;
	}
}