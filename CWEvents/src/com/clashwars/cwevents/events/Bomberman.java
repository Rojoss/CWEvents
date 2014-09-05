package com.clashwars.cwevents.events;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.clashwars.cwevents.events.extra.BombermanData;
import com.clashwars.cwevents.events.internal.BaseEvent;
import com.clashwars.cwevents.events.internal.EventType;
import com.clashwars.cwevents.runnables.BombRunnable;
import com.clashwars.cwevents.utils.ItemUtils;
import com.clashwars.cwevents.utils.ParticleEffect;
import com.clashwars.cwevents.utils.Util;
import com.clashwars.cwevents.utils.WGUtils;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class Bomberman extends BaseEvent {
	private List<Material> floorBlocks = Arrays.asList(Material.SANDSTONE);
	private List<Material> destroyBlocks = Arrays.asList(Material.SAND, Material.COBBLESTONE);
	
	private BlockFace[] directions = new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
	
	private HashMap<String, BombermanData> bombData = new HashMap<String, BombermanData>();
	
	public void Reset() {
		super.Reset();
		WGUtils.setFlag(world, em.getRegionName("arena"), DefaultFlag.PVP, "deny");
	}
	
	public void Open() {
		super.Open();
	}
	
	public void Start() {
		super.Start();
		int playerID = 0;
		for (String p : em.getPlayers()) {
			//Give each player a unique ID.
			if (!bombData.containsKey(p)) {
				bombData.put(p, new BombermanData(p));
			}
			bombData.get(p).setID(playerID);
			
			//TODO: Teleport players to different locations somehow.
			//Player player = cwe.getServer().getPlayer(p);
			playerID++;
		}
	}
	
	public void Begin() {
		WGUtils.setFlag(world, em.getRegionName("arena"), DefaultFlag.PVP, "allow");
		for (String p : em.getPlayers()) {
			cwe.getServer().getPlayer(p).getInventory().addItem(ItemUtils.getItem(Material.TNT, 1, (short)0, "&4&lBomb", new String[]
					{"&7Place this on the ground to create an explosion.", "&7Bombs will will slowly regenerate based on ur stats."}));
		}	
	}
	
	public void Stop() {
		super.Stop();
		WGUtils.setFlag(world, em.getRegionName("arena"), DefaultFlag.PVP, "deny");
	}

	public void onPlayerLeft(Player player) {
		if (bombData.containsKey(player.getName())) {
			bombData.remove(player.getName());
		}
	}

	public void onPlayerJoin(Player player) {
		bombData.put(player.getName(), new BombermanData(player.getName()));
	}
	
	public void bombExplode(Player player, Location loc) {
		BombermanData bd = bombData.get(player.getName());
		Block block = loc.getBlock();
		block.setType(Material.AIR);
		if (bd == null) {
			//return;
		}
		loc.getWorld().playSound(loc, Sound.EXPLODE, 0.6f, 1.5f);
		ParticleEffect.HUGE_EXPLOSION.display(loc, 0.5f, 0.5f, 0.5f, 0.001f, 3);
		
		Block b = block;
		Player otherPlayer;
		int distance = 1;
		for (BlockFace dir : directions) {
			for (distance = 1; distance <= 2; distance++) {
				b = block.getRelative(dir, distance);
				//Air blocks so just continue explosion.
				if (b == null || b.getType() == Material.AIR) {
					explodeParticle(b.getLocation().add(0.5f,0.5f,0.5f), dir);
					//Check if player is at this block and kill him if he is.
					for (String p : em.getPlayers()) {
						otherPlayer = cwe.getServer().getPlayer(p);
						if (otherPlayer.getLocation().getBlockX() == b.getLocation().getBlockX() && otherPlayer.getLocation().getBlockZ() == b.getLocation().getBlockZ()) {
							Bukkit.broadcastMessage(otherPlayer.getName() + " was in the explosion!");
						}
					}
					continue;
				}
				//Check for a destroyable block.
				if (destroyBlocks.contains(b.getType())) {
					explodeParticle(b.getLocation().add(0.5f,0.5f,0.5f), dir);
					b.breakNaturally();
					break;
				}
				//Hit a wall
				break;
			}
		}
	}
	
	private void explodeParticle(Location loc, BlockFace dir) {
		if (dir == BlockFace.NORTH || dir == BlockFace.SOUTH) {
			ParticleEffect.FLAME.display(loc, 0.25f, 0.2f, 0.35f, 0.005f, 15);
			ParticleEffect.SMOKE.display(loc, 0.25f, 0.5f, 0.35f, 0.001f, 5);
		} else {
			ParticleEffect.FLAME.display(loc, 0.35f, 0.2f, 0.25f, 0.005f, 15);
			ParticleEffect.SMOKE.display(loc, 0.35f, 0.5f, 0.25f, 0.001f, 5);
		}
	}
	
	@EventHandler
	public void damage(EntityDamageEvent event) {
		if (em.getEvent() != EventType.BOMBERMAN) {
			return;
		}
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player)event.getEntity();
		if (em.getPlayers().contains(player.getName())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void bombPlace(BlockPlaceEvent event) {
		if (em.getEvent() != EventType.BOMBERMAN) {
			//return;
		}
		if (event.getBlock().getType() != Material.TNT) {
			return;
		}
		Player player = event.getPlayer();
		if (!em.getPlayers().contains(player.getName())) {
			//return;
		}
		if (!floorBlocks.contains(event.getBlock().getRelative(BlockFace.DOWN).getType())) {
			player.sendMessage(Util.formatMsg("&cBombs must be placed on the ground."));
			event.setCancelled(true);
			return;
		}
		new BombRunnable(this, player, event.getBlock().getLocation().add(0.5f,0.5f,0.5f), /*bombData.get(player.getName()).getFuseTime()*/ 80).runTaskTimer(cwe.getPlugin(), 0, 1);
	}
}