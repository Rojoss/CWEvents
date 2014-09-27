package com.clashwars.cwevents.commands;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwevents.CWEvents;
import com.clashwars.cwevents.Util;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.UUID;

public class Commands {
    private CWEvents cwe;

    public Commands(CWEvents cwe) {
        this.cwe = cwe;
    }


    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //LOC COMMAND
        if (label.equalsIgnoreCase("loc")) {
            //Console check
            if (!(sender instanceof Player)) {
                sender.sendMessage(Util.formatMsg("&cThis is a player command only."));
                return true;
            }
            Player player = (Player) sender;

            if (args.length > 0) {
                //Permission check.
                if (!player.isOp() && !player.hasPermission("cwevents.loc")) {
                    player.sendMessage(Util.formatMsg("&cInsuficient permissions."));
                    return true;
                }

                //##########################################################################################################################
                //#################################################### /loc set {name} #####################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("set")) {
                    if (args.length < 2) {
                        player.sendMessage(Util.formatMsg("&cInvalid usage. &7/loc set {name}"));
                        return true;
                    }
                    if (args[1].length() < 2) {
                        player.sendMessage(Util.formatMsg("&cLocation name is too short."));
                        return true;
                    }
                    cwe.getLocConfig().setLocation(args[1], player.getLocation());
                    player.sendMessage(Util.formatMsg("&6Location &8'&5" + args[1] + "&8' &6set to your location!"));
                    return true;
                }

                //##########################################################################################################################
                //################################################### /loc remove {name} ###################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length < 2) {
                        player.sendMessage(Util.formatMsg("&cInvalid usage. &7/loc remove {name}"));
                        return true;
                    }
                    String name = cwe.getLocConfig().getName(args[1]);
                    if (name == "") {
                        player.sendMessage(Util.formatMsg("&6Location &8'&5" + args[1] + "&8' &6doesn't exist!"));
                        return true;
                    }
                    cwe.getLocConfig().removeLoction(name);
                    player.sendMessage(Util.formatMsg("&6Location &8'&5" + name + "&8' &6has been removed!"));
                    return true;
                }


                //##########################################################################################################################
                //##################################################### /loc tp {name} #####################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("tp")) {
                    if (args.length < 2) {
                        player.sendMessage(Util.formatMsg("&cInvalid usage. &7/loc tp {name}"));
                        return true;
                    }
                    String name = cwe.getLocConfig().getName(args[1]);
                    if (name == "") {
                        player.sendMessage(Util.formatMsg("&6Location &8'&5" + args[1] + "&8' &6doesn't exist!"));
                        return true;
                    }
                    player.teleport(cwe.getLocConfig().getLoc(name));
                    player.sendMessage(Util.formatMsg("&6Teleported to location &8'&5" + name + "&8'&6."));
                    return true;
                }


                //##########################################################################################################################
                //####################################################### /loc list ########################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("list")) {
                    String locs = "";
                    for (String name : cwe.getLocConfig().getLocations().keySet()) {
                        locs += name + "&8, &5";
                    }
                    player.sendMessage(CWUtil.integrateColor("&6&lLocations&8: &5" + locs));
                    return true;
                }
            }

            //##########################################################################################################################
            //########################################################## /loc ##########################################################
            //##########################################################################################################################
            sender.sendMessage(CWUtil.integrateColor("&8===== &4&lCommand Help &6/" + label + " &8====="));
            sender.sendMessage(CWUtil.integrateColor("&7Manage locations which can be used in certain events."));
            sender.sendMessage(CWUtil.integrateColor("&7Pretty much same as warps but more simple and easier to use in code."));
            sender.sendMessage(CWUtil.integrateColor("&6/loc set [name] &8- &5Set location or add location."));
            sender.sendMessage(CWUtil.integrateColor("&6/loc remove [name] &8- &5Remove location."));
            sender.sendMessage(CWUtil.integrateColor("&6/loc tp [name] &8- &5Teleport to location."));
            sender.sendMessage(CWUtil.integrateColor("&6/loc list &8- &5List all locations."));
            return true;
        }


        //LEAVE COMMAND
        if (label.equalsIgnoreCase("pvp") || label.equalsIgnoreCase("leave") || label.equalsIgnoreCase("quit")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Util.formatMsg("&cThis is a player command only."));
                return true;
            }
            cwe.joinPvP((Player)sender);
            return true;
        }


        //EVENT COMMAND
        if (label.equalsIgnoreCase("event") || label.equalsIgnoreCase("e") || label.equalsIgnoreCase("events")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Util.formatMsg("&cThis is a player command only."));
                return true;
            }
            Player player = (Player) sender;

            if (args.length > 0) {
                //Player commands...

                //##########################################################################################################################
                //###################################################### /event help #######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage(CWUtil.integrateColor("&8===== &4&lCommand Help &6/" + label + " &8====="));
                    sender.sendMessage(CWUtil.integrateColor("&6/event help &8- &5Show this page."));
                    sender.sendMessage(CWUtil.integrateColor("&6/event &8- &5Info about event."));
                    sender.sendMessage(CWUtil.integrateColor("&6/event join &8- &5Go to the events server or join event."));
                    sender.sendMessage(CWUtil.integrateColor("&6/event leave &8- &5Leave current event or server if on server."));
                    sender.sendMessage(CWUtil.integrateColor("&6/event set {event} {arena} [slots] &8- &5Set active event."));
                    sender.sendMessage(CWUtil.integrateColor("&6/event setspawn &8- &5Set arena spawn."));
                    sender.sendMessage(CWUtil.integrateColor("&6/event spawn &8- &5Teleport to the active event."));
                    sender.sendMessage(CWUtil.integrateColor("&6/event reset &8- &5Reset active event."));
                    sender.sendMessage(CWUtil.integrateColor("&6/event open &8- &5Open active event."));
                    sender.sendMessage(CWUtil.integrateColor("&6/event close &8- &5Close opened event."));
                    sender.sendMessage(CWUtil.integrateColor("&6/event start &8- &5Start active event."));
                    sender.sendMessage(CWUtil.integrateColor("&6/event stop [winner] &8- &5Stop active event."));
                    sender.sendMessage(CWUtil.integrateColor("&6/event &8- &5Info about current event."));
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
                        //Not in a event so teleport player to pvp server.
                        cwe.joinPvP((Player)sender);
                    }
                    return true;
                }

                //##########################################################################################################################
                //##################################################### /event spawn #######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("spawn")) {
                    if ((cwe.getEM().getPlayers() != null && cwe.getEM().getPlayers().contains(player.getName())) || player.isOp() || player.hasPermission("cwevents.cmd.admin")) {
                        if (cwe.getEM().getSpawn() != null) {
                            player.teleport(cwe.getEM().getSpawn());
                            sender.sendMessage(Util.formatMsg("&6Teleported to &5" + cwe.getEM().getEvent().getName() + " &6arena &5" + cwe.getEM().getArena() + "&6."));
                        } else {
                            sender.sendMessage(Util.formatMsg("&cNo cached event/arena data found. &7Set it with &8/event set"));
                        }
                    } else {
                        sender.sendMessage(Util.formatMsg("&cYou're not playing an event."));
                    }
                    return true;
                }


                //Admin commands...
                if (!player.isOp() && !player.hasPermission("cwevents.cmd.admin")) {
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
                    if (!event.getEventClass().checkSetup(event, arena, sender)) {
                        return true;
                    }

                    int slots = 0;
                    if (args.length >= 4) {
                        slots = CWUtil.getInt(args[3]);
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
                    if (winner != null) {
                        sender.sendMessage(Util.formatMsg("&6Winner set to &a&l" + w.getName() + " &7(&8" + winner.toString() + "&7)"));
                    }

                    cwe.getEM().setStatus(EventStatus.STOPPED);
                    activeEvent.getEventClass().Stop();

                    //Send winner data to pvp server.
                    if (winner != null) {
                        try {
                            ByteArrayOutputStream b = new ByteArrayOutputStream();
                            DataOutputStream out = new DataOutputStream(b);

                            out.writeUTF("queue");
                            out.writeUTF("pvp");
                            out.writeUTF(winner.toString());
                            out.writeUTF("cmd");
                            out.writeUTF("eventreward {PLAYER}");

                            Bukkit.getOnlinePlayers()[0].sendPluginMessage(cwe, "CWBungee", b.toByteArray());
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }

                    cwe.getEM().updateEventItem();
                    return true;
                }
            }

            //##########################################################################################################################
            //######################################################## /event ##########################################################
            //##########################################################################################################################
            sender.sendMessage(CWUtil.integrateColor("&8======== &4&lEvent Information &8========"));
            if (cwe.getEM().getEvent() == null || cwe.getEM().getArena() == null) {
                sender.sendMessage(CWUtil.integrateColor("&cThere is currently no event active."));
            } else {
                sender.sendMessage(CWUtil.integrateColor("&6Event&8: &5" + cwe.getEM().getEvent().getName()));
                sender.sendMessage(CWUtil.integrateColor("&6Arena&8: &5" + cwe.getEM().getArena().toString()));
                sender.sendMessage(CWUtil.integrateColor("&6Slots&8: &a" + cwe.getEM().getPlayers().size() + "&7/&2" + (cwe.getEM().getSlots() < 1 ? "Infinite" : cwe.getEM().getSlots())));
                sender.sendMessage(CWUtil.integrateColor("&6Status&8: &5" + cwe.getEM().getStatus().getName()));
                String playerStr = "";
                for (String p : cwe.getEM().getPlayers()) {
                    playerStr = playerStr + "&5" + p + "&8, ";
                }
                sender.sendMessage(CWUtil.integrateColor("&6Players&8: &5" + playerStr));
            }
            return true;
        }
        return false;
    }
}
