package com.clashwars.cwevents.events;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import com.clashwars.cwevents.events.extra.BombermanData;
import com.clashwars.cwevents.events.internal.BaseEvent;
import com.clashwars.cwevents.events.internal.EventType;
import com.clashwars.cwevents.utils.ItemUtils;
import com.clashwars.cwevents.utils.Util;
import com.clashwars.cwevents.utils.WGUtils;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class Bomberman extends BaseEvent {
	
	private Material floorBlock = Material.OBSIDIAN;
	private Material destroyBlock = Material.COBBLESTONE;
	
	private HashMap<String, BombermanData> bombData = new HashMap<String, BombermanData>();
	
	public void Reset() {
		WGUtils.setFlag(world, em.getRegionName("arena"), DefaultFlag.PVP, "deny");
	}
	
	public void Open() {
		
	}
	
	public void Start() {
		//TODO: Teleport players to different locations somehow.
	}
	
	public void Begin() {
		WGUtils.setFlag(world, em.getRegionName("arena"), DefaultFlag.PVP, "allow");
	}
	
	public void Stop() {
		WGUtils.setFlag(world, em.getRegionName("arena"), DefaultFlag.PVP, "deny");
	}

	public void onPlayerLeft(Player player) {
		if (bombData.containsKey(player.getName())) {
			bombData.remove(player.getName());
		}
	}

	public void onPlayerJoin(Player player) {
		player.getInventory().addItem(ItemUtils.getItem(Material.TNT, 1, (short)0, "&4&lBomb", new String[]
				{"&7Place this on the ground to create an explosion.", "&7Bombs will will slowly regenerate based on ur stats."}));
		bombData.put(player.getName(), new BombermanData(player.getName()));
	}
	
	public void bombExplode(Player player, Location loc) {
		
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
			return;
		}
		if (event.getBlock().getType() != Material.TNT) {
			return;
		}
		Player player = event.getPlayer();
		if (!em.getPlayers().contains(player.getName())) {
			return;
		}
		if (event.getBlock().getRelative(BlockFace.DOWN).getType() != floorBlock) {
			player.sendMessage(Util.formatMsg("&cBombs must be placed on the ground."));
			return;
		}
		event.setCancelled(true);
		ItemStack tnt = new ItemStack(Material.TNT, 1);
		player.getInventory().remove(tnt);
		
		
		
		
	}
}
