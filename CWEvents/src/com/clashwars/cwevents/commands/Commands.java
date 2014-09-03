package com.clashwars.cwevents.commands;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clashwars.cwevents.CWEvents;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import com.clashwars.cwevents.utils.Util;

public class Commands {
	private CWEvents			cwe;

	public Commands(CWEvents cwe) {
		this.cwe = cwe;
	}

	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("event")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Util.formatMsg("&cThis is a player command only."));
				return true;
			}
			Player player = (Player)sender;
			
			if (args.length > 0 ) {
				//Player commands...
				
				//##########################################################################################################################
				//###################################################### /event help #######################################################
				//##########################################################################################################################
				if (args[0].equalsIgnoreCase("help")) {
					sender.sendMessage(Util.integrateColor("&8===== &4&lCommand Help &6/" + label + " &8====="));
					sender.sendMessage(Util.integrateColor("&6/event help &8- &5Show this page."));
					sender.sendMessage(Util.integrateColor("&6/event &8- &5Info about event."));
					sender.sendMessage(Util.integrateColor("&6/event join &8- &5Go to the events server or join event."));
					sender.sendMessage(Util.integrateColor("&6/event leave &8- &5Leave current event or server if on server."));
					sender.sendMessage(Util.integrateColor("&6/event set {event} {arena} [slots] &8- &5Set active event."));
					sender.sendMessage(Util.integrateColor("&6/event setspawn &8- &5Set arena spawn."));
					sender.sendMessage(Util.integrateColor("&6/event spawn &8- &5Teleport to the active event."));
					sender.sendMessage(Util.integrateColor("&6/event reset &8- &5Reset active event."));
					sender.sendMessage(Util.integrateColor("&6/event open &8- &5Open active event."));
					sender.sendMessage(Util.integrateColor("&6/event close &8- &5Close opened event."));
					sender.sendMessage(Util.integrateColor("&6/event start &8- &5Start active event."));
					sender.sendMessage(Util.integrateColor("&6/event stop [winner] &8- &5Stop active event."));
					sender.sendMessage(Util.integrateColor("&6/event &8- &5Info about current event."));
					return true;
				}
				
				//##########################################################################################################################
				//###################################################### /event join #######################################################
				//##########################################################################################################################
				if (args[0].equalsIgnoreCase("join")) {
					cwe.getEM().joinEvent(player);
					return true;
				}
				
				//##########################################################################################################################
				//##################################################### /event leave #######################################################
				//##########################################################################################################################
				if (args[0].equalsIgnoreCase("leave")) {
					if (!cwe.getEM().leaveEvent(player, false)) {
						sender.sendMessage(Util.formatMsg("&cYou are not playing a event."));
					}
					return true;
				}
				
				//##########################################################################################################################
				//##################################################### /event spawn #######################################################
				//##########################################################################################################################
				if (args[0].equalsIgnoreCase("spawn")) {
					if (cwe.getEM().getPlayers().contains(player.getName()) || player.isOp() || player.hasPermission("cwevents.cmd.admin")) {
						player.teleport(cwe.getEM().getSpawn());
						sender.sendMessage(Util.formatMsg("&6Teleported to &5" + cwe.getEM().getEvent().getName() + " &6arena &5" + cwe.getEM().getArena() + "&6."));
					} else {
						sender.sendMessage(Util.formatMsg("&cYou're not playing an event."));
					}
					return true;
				}
				
				
				
				
				//Admin commands...
				if (!sender.hasPermission("cwevents.cmd.admin") && sender.isOp()) {
					sender.sendMessage(Util.formatMsg("&cInsufficient permissions."));
					return true;
				}
				
				//##########################################################################################################################
				//########################################### /event set {event} {arena} [slots] ###########################################
				//##########################################################################################################################
				if (args[0].equalsIgnoreCase("set")) {
					if (cwe.getEM().getStatus() == EventStatus.OPEN || cwe.getEM().getStatus() == EventStatus.STARTED || cwe.getEM().getStatus() == EventStatus.STARTING) {
						sender.sendMessage(Util.formatMsg("&cthe game is running or opened already!"));
						sender.sendMessage(Util.formatMsg("&cUse &4/arena stop &cor &4/arena close &cbefore setting it again."));
						return true;
					}
					if (args.length > 1 && args[1].equalsIgnoreCase("none")) {
						cwe.getEM().setStatus(EventStatus.CLOSED);
						cwe.getEM().setEvent(null);
						cwe.getEM().setArena(null);
						cwe.getEM().setSlots(-1);
						cwe.getEM().setSpawn(null);
						cwe.getEM().updateEventItem();
						sender.sendMessage(Util.formatMsg("&6Cached arena data has been cleared."));
						return true;
					}
					if (args.length < 3) {
						sender.sendMessage(Util.formatMsg("&cInvalid usage. &7/event set {event|'none'} {arena} [slots]"));
						return true;
					}
					
					EventType event = EventType.fromString(args[1]);
					if (event == null) {
						sender.sendMessage(Util.formatMsg("&cInvalid event name."));
						return true;
					}
					
					String arena = args[2];
					//TODO: Check if region exists.
					
					int slots = 0;
					if (args.length >= 4) {
						slots = Util.getInt(args[3]);
						if (slots <= 1) {
							sender.sendMessage(Util.formatMsg("&cInvalid slot amount. Must be number and at least 2."));
							return true;
						}
					} else {
						slots = -1;
					}
					
					cwe.getEM().setStatus(EventStatus.CLOSED);
					cwe.getEM().setEvent(event);
					cwe.getEM().setArena(arena);
					cwe.getEM().setSlots(slots);
					cwe.getEM().setSpawn(player.getLocation());
					
					cwe.getEM().updateEventItem();
					
					sender.sendMessage(Util.formatMsg("&6Event set to&8: &5" + event.getName()));
					sender.sendMessage(Util.formatMsg("&6Arena set to&8: &5" + arena));
					if (slots > 0) {
						sender.sendMessage(Util.formatMsg("&6Slots set to&8: &5" + event.getName()));
					} else {
						sender.sendMessage(Util.formatMsg("&7No slots are set. Infinite players can join."));
					}
					sender.sendMessage(Util.formatMsg("&6Spawn set to&8: &aX&8:&7" + player.getLocation().getBlockX() + " &9Y&8:&7" + player.getLocation().getBlockY() + " &cZ&8:&7" + player.getLocation().getBlockZ()));
					return true;
				}
				
				
				EventType activeEvent = cwe.getEM().getEvent();
				String activeArena = cwe.getEM().getArena();
				if (activeEvent == null || activeArena == null) {
					sender.sendMessage(Util.formatMsg("&cNo cached event/arena data found. &7Set it with &8/event set"));
					return true;
				}
				
				//##########################################################################################################################
				//#################################################### /event setspawn #####################################################
				//##########################################################################################################################
				if (args[0].equalsIgnoreCase("setspawn")) {
					cwe.getEM().setSpawn(player.getLocation());
					sender.sendMessage(Util.formatMsg("&6Spawn set to&8: &aX&8:&7" + player.getLocation().getBlockX() + " &9Y&8:&7" + player.getLocation().getBlockY() + " &cZ&8:&7" + player.getLocation().getBlockZ()));
					return true;
				}
				
				//##########################################################################################################################
				//##################################################### /event reset #######################################################
				//##########################################################################################################################
				if (args[0].equalsIgnoreCase("reset")) {
					if (cwe.getEM().getStatus() == EventStatus.OPEN || cwe.getEM().getStatus() == EventStatus.STARTED || cwe.getEM().getStatus() == EventStatus.STARTING) {
						sender.sendMessage(Util.formatMsg("&cthe game is running or opened!"));
						sender.sendMessage(Util.formatMsg("&cUse &4/arena stop &cor &4/arena close &cbefore resetting."));
						return true;
					}
					cwe.getEM().setStatus(EventStatus.RESETTING);
					cwe.getEM().updateEventItem();
					activeEvent.getEventClass().Reset();
					sender.sendMessage(Util.formatMsg("&6Reset &5" + activeEvent.getName() + " &6arena &5" + activeArena + "&6."));
					return true;
				}
				
				//##########################################################################################################################
				//##################################################### /event open ########################################################
				//##########################################################################################################################
				if (args[0].equalsIgnoreCase("open")) {
					if (cwe.getEM().getStatus() == EventStatus.OPEN || cwe.getEM().getStatus() == EventStatus.STARTED || cwe.getEM().getStatus() == EventStatus.STARTING) {
						sender.sendMessage(Util.formatMsg("&cThe game is already running or opened."));
						return true;
					}
					activeEvent.getEventClass().Open();
					cwe.getEM().setStatus(EventStatus.OPEN);
					cwe.getEM().updateEventItem();
					sender.sendMessage(Util.formatMsg("&6Opened &5" + activeEvent.getName() + " &6arena &5" + activeArena + "&6 for joining."));
					return true;
				}
				
				//##########################################################################################################################
				//#################################################### /event start ########################################################
				//##########################################################################################################################
				if (args[0].equalsIgnoreCase("start")) {
					if (cwe.getEM().getStatus() != EventStatus.OPEN) {
						sender.sendMessage(Util.formatMsg("&cthe game hasn't been opened yet."));
						return true;
					}
					if (cwe.getEM().getPlayers().size() < 2) {
						sender.sendMessage(Util.formatMsg("&cThere need to be at least 2 players to start the game!"));
						//TODO: Uncomment this for public version.
						//return true;
					}
					activeEvent.getEventClass().Start();
					cwe.getEM().setStatus(EventStatus.STARTING);
					cwe.getEM().updateEventItem();
					sender.sendMessage(Util.formatMsg("&6Started &5" + activeEvent.getName() + " &6arena &5" + activeArena + "&6."));
					return true;
				}
				
				//##########################################################################################################################
				//#################################################### /event close ########################################################
				//##########################################################################################################################
				if (args[0].equalsIgnoreCase("close")) {
					if (cwe.getEM().getStatus() != EventStatus.OPEN) {
						sender.sendMessage(Util.formatMsg("&cthe game hasn't been opened yet."));
						return true;
					}
					activeEvent.getEventClass().Close();
					cwe.getEM().setStatus(EventStatus.CLOSED);
					cwe.getEM().updateEventItem();
					sender.sendMessage(Util.formatMsg("&6Closed &5" + activeEvent.getName() + " &6arena &5" + activeArena + "&6."));
					return true;
				}
				
				//##########################################################################################################################
				//################################################# /event stop [winner] ###################################################
				//##########################################################################################################################
				if (args[0].equalsIgnoreCase("stop")) {
					if (cwe.getEM().getStatus() != EventStatus.STARTED) {
						sender.sendMessage(Util.formatMsg("&cthe game hasn't been started yet."));
						return true;
					}
					UUID winner = null;
					OfflinePlayer w = null;
					if (args.length > 1) {
						w = cwe.getServer().getOfflinePlayer(args[1]);
						if (w == null) {
							sender.sendMessage(Util.formatMsg("&cInvalid player specified."));
							return true;
						}
						winner = w.getUniqueId();
					}
					
					sender.sendMessage(Util.formatMsg("&6Stopped &5" + activeEvent.getName() + " &6arena &5" + activeArena + "&6."));
					if (winner !=null) {
						sender.sendMessage(Util.formatMsg("&6Winner set to &a&l" + w.getName() + " &7(&8" + winner.toString() + "&7)"));
					}

					cwe.getEM().setStatus(EventStatus.STOPPED);
					activeEvent.getEventClass().Stop();
					//TODO: send winner data to pvp server.
					cwe.getEM().updateEventItem();
					return true;
				}
			}
			
			sender.sendMessage(Util.integrateColor("&8======== &4&lEvent Information &8========"));
			if (cwe.getEM().getEvent() == null || cwe.getEM().getArena() == null) {
				sender.sendMessage(Util.integrateColor("&cThere is currently no event active."));
			} else {
				sender.sendMessage(Util.integrateColor("&6Event&8: &5" + cwe.getEM().getEvent().getName()));
				sender.sendMessage(Util.integrateColor("&6Arena&8: &5" + cwe.getEM().getArena().toString()));
				sender.sendMessage(Util.integrateColor("&6Slots&8: &a" + cwe.getEM().getPlayers().size() + "&7/&2" + (cwe.getEM().getSlots() < 1 ? "Infinite" : cwe.getEM().getSlots())));
				sender.sendMessage(Util.integrateColor("&6Status&8: &5" + cwe.getEM().getStatus().getName()));
				String playerStr = "";
				for (String p : cwe.getEM().getPlayers()) {
					playerStr = playerStr + "&5" + p + "&8, ";
				}
				sender.sendMessage(Util.integrateColor("&6Players&8: &5" + playerStr));
			}
			return true;
		}
		return false;
	}
}