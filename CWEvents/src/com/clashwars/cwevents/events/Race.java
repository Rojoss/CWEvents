package com.clashwars.cwevents.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.clashwars.cwevents.events.internal.BaseEvent;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import com.clashwars.cwevents.utils.ItemUtils;
import com.clashwars.cwevents.utils.Util;
import com.clashwars.cwevents.utils.WGUtils;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class Race extends BaseEvent {
	
	public Race() {
		regionsNeeded.add("lobby");
	}
	
	public void Reset() {
		super.Reset();
		WGUtils.setFlag(world, em.getRegionName("lobby"), DefaultFlag.EXIT, "deny");
	}
	
	public void Open() {
		super.Open();
	}
	
	public void Start() {
		super.Start();
	}
	
	public void Begin() {
		WGUtils.setFlag(world, em.getRegionName("lobby"), DefaultFlag.EXIT, "allow");
	}
	
	public void Stop() {
		super.Stop();
		WGUtils.setFlag(world, em.getRegionName("lobby"), DefaultFlag.EXIT, "deny");
	}

	public void onPlayerLeft(Player player) {
		
	}

	public void onPlayerJoin(Player player) {
		player.getInventory().addItem(ItemUtils.getItem(Material.LEASH, 1, (short)0, "&6&lLasso", new String[] {"&7Use this on other players.", "&7It will pull them towards you!"}));
		player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 8));
		
		//Random colored boots.
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
		ItemMeta meta = boots.getItemMeta();
		LeatherArmorMeta armorMeta = (LeatherArmorMeta)meta;
		armorMeta.setColor(Util.getRandomColor());
		boots.setItemMeta(armorMeta);
		player.getInventory().setBoots(boots);
	}
	
	@EventHandler
	public void respawn(PlayerRespawnEvent event) {
		if (em.getEvent() != EventType.RACE) {
			return;
		}
		if (em.getStatus() != EventStatus.STARTED) {
			return;
		}
		if (em.getPlayers().contains(event.getPlayer().getName())) {
			em.broadcast(Util.formatMsg("&b&l" + event.getPlayer().getDisplayName() + " &3died and has to start over again!"));
			event.getPlayer().teleport(em.getSpawn());
		}
	}
}