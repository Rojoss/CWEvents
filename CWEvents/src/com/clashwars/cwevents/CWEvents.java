package com.clashwars.cwevents;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import com.clashwars.cwevents.bukkit.CWEventsPlugin;
import com.clashwars.cwevents.bukkit.events.MainEvents;
import com.clashwars.cwevents.commands.Commands;
import com.clashwars.cwevents.events.internal.EventManager;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import com.clashwars.cwevents.utils.ItemUtils;

public class CWEvents {
	private CWEventsPlugin		cwe;
	
	private Commands			cmds;
	private EventManager	em;

	private final Logger		log	= Logger.getLogger("Minecraft");
	public static CWEvents		instance;


	public CWEvents(CWEventsPlugin cwe) {
		this.cwe = cwe;
	}

	@SuppressWarnings("deprecation")
	public void onDisable() {
		for (String p : em.getPlayers()) {
			if (getServer().getPlayer(p) != null) {
				em.leaveEvent(getServer().getPlayer(p), true);
			}
		}
		em.setStatus(EventStatus.CLOSED);
		em.setArena(null);
		em.setEvent(null);
		em.updateEventItem();
		
		instance = null;
		
		log("Disabled.");
	}

	public void onEnable() {
		instance = this;
		
		cmds = new Commands(this);
		
		em = new EventManager(this);
		
		for (EventType event : EventType.values()) {
			event.getEventClass().Init(this, em);
		}
		
		registerEvents();
		
		for (Player p : getServer().getOnlinePlayers()) {
			em.resetPlayer(p);
		}
		em.updateEventItem();
		
		log("Successfully enabled.");
	}
	
	private void registerEvents() {
		PluginManager pm = getPlugin().getServer().getPluginManager();
		pm.registerEvents(new MainEvents(this), getPlugin());
		for (EventType event : EventType.values()) {
			if (event.getEventClass() != null) {
				pm.registerEvents(event.getEventClass(), getPlugin());
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return cmds.onCommand(sender, cmd, label, args);
	}

	public void log(Object msg) {
		log.info("[CWClasses " + getPlugin().getDescription().getVersion() + "]: " + msg.toString());
	}
	
	

	public CWEventsPlugin getPlugin() {
		return cwe;
	}

	public Server getServer() {
		return getPlugin().getServer();
	}
	
	public EventManager getEM() {
		return em;
	}
	
	public ItemStack GetEventItem() {
		if (em.getEvent() == null || em.getArena() == null) {
			return ItemUtils.getItem(Material.INK_SACK, 1, (short)1, "&4&lNo Event", new String[] {"&7There is currently no open event!"});
		} else {
			if (em.getStatus() == EventStatus.OPEN) {
				return ItemUtils.getItem(Material.INK_SACK, 1, (short)10, "&6&lJoin &5&l" + em.getEvent().getName(), new String[] {
					"&7Use this item to join &8" + em.getEvent().getName() + "&7.", "&6&lEvent&8&l: &5" +  em.getEvent().getName(), 
					"&6&lArena&8&l: &5" +  em.getArena(), "&6&lPlayers&8&l: &a" +  em.getPlayers().size() + "&7/&2" + em.getSlots()});
			} else {
				return ItemUtils.getItem(Material.INK_SACK, 1, (short)8, "&c&lNot Joinable", new String[] {
					"&7The event is not joinable.", "&6&lEvent&8&l: &5" +  em.getEvent().getName(), "&6&lArena&8&l: &5" +  em.getArena(), 
					"&6&lStatus&8&l: &5" +  em.getStatus().getName()});
			}
		}
	}
}
