package com.clashwars.cwevents.commands;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwevents.CWEvents;
import com.clashwars.cwevents.Util;
import com.clashwars.cwevents.config.AutojoinCfg;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import com.clashwars.cwevents.stats.Stats;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                    cwe.getLocCfg().setLocation(args[1], player.getLocation());
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
                    String name = cwe.getLocCfg().getName(args[1]);
                    if (name == "") {
                        player.sendMessage(Util.formatMsg("&6Location &8'&5" + args[1] + "&8' &6doesn't exist!"));
                        return true;
                    }
                    cwe.getLocCfg().removeLoction(name);
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
                    String name = cwe.getLocCfg().getName(args[1]);
                    if (name == "") {
                        player.sendMessage(Util.formatMsg("&6Location &8'&5" + args[1] + "&8' &6doesn't exist!"));
                        return true;
                    }
                    player.teleport(cwe.getLocCfg().getLoc(name));
                    player.sendMessage(Util.formatMsg("&6Teleported to location &8'&5" + name + "&8'&6."));
                    return true;
                }


                //##########################################################################################################################
                //############################################### /loc list [event] [arena] ################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("list")) {
                    String event = "";
                    if (args.length > 1) {
                        event = args[1];
                    }
                    String arena = "";
                    if (args.length > 2) {
                        arena = args[2];
                    }

                    String locs = "";
                    for (String name : cwe.getLocCfg().getLocations().keySet()) {
                        String[] split = name.split("_");
                        if (!event.isEmpty()) {
                            if (split.length > 0) {
                                EventType eventType = EventType.fromPrefix(split[0]);
                                if (eventType != null) {
                                    name = eventType.getColor() + name;
                                }
                                if (event.equalsIgnoreCase(split[0])) {
                                    if (!arena.isEmpty() && split.length > 1) {
                                        if (arena.equalsIgnoreCase(split[1])) {
                                            locs += name + "&8, &5";
                                        }
                                    } else {
                                        locs += name + "&8, &5";
                                    }
                                }
                            }
                        } else {
                            if (split.length > 0) {
                                EventType eventType = EventType.fromPrefix(split[0]);
                                if (eventType != null) {
                                    name = eventType.getColor() + name;
                                }
                            }
                            locs += name + "&8, &5";
                        }
                    }
                    if (locs.isEmpty()) {
                        player.sendMessage(CWUtil.integrateColor("&6&lLocations&8: &4None found..."));
                    } else {
                        player.sendMessage(CWUtil.integrateColor("&6&lLocations&8: &5" + locs));
                    }
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
            sender.sendMessage(CWUtil.integrateColor("&6/loc list [event] [arena] &8- &5List all locations."));
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


        //STATS COMMAND
        if (label.equalsIgnoreCase("stats") || label.equalsIgnoreCase("stat")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Util.formatMsg("&cThis is a player command only."));
                return true;
            }
            Player player = (Player) sender;
            EventType eventType = null;
            OfflinePlayer otherPlayer = null;

            if (args.length > 0) {
                //Get event filter
                if (EventType.fromPrefix(args[0]) != null) {
                    eventType = EventType.fromPrefix(args[0]);
                } else if (EventType.fromString(args[0]) != null) {
                    eventType = EventType.fromString(args[0]);
                }
                if (!args[0].equalsIgnoreCase("list") &&eventType == null) {
                    player.sendMessage(Util.formatMsg("&cInvalid event name."));
                    player.sendMessage(Util.formatMsg("&cCommand usage: &7/stats [event|list] [player]"));
                    return true;
                }

                //Get player
                if (args.length > 1) {
                    otherPlayer = cwe.getServer().getOfflinePlayer(args[1]);
                    if (otherPlayer == null) {
                        player.sendMessage(Util.formatMsg("&cInvalid player."));
                        player.sendMessage(Util.formatMsg("&cCommand usage: &7/stats " + args[0] + " [player]"));
                        return true;
                    }
                }
            }

            //Get the stats
            if (cwe.getStats() == null) {
                player.sendMessage(Util.formatMsg("&cNo stats could be loaded."));
                player.sendMessage(Util.formatMsg("&cPlease try again later."));
                return true;
            }
            Stats stats = otherPlayer == null ? cwe.getStats().getStats(player) : cwe.getStats().getStats(otherPlayer);
            if (stats == null) {
                player.sendMessage(Util.formatMsg("&cNo stats could be found. &c" + otherPlayer == null ? "You" : otherPlayer.getName() + " probably haven't played any events or it hasn't synced yet."));
                return true;
            }

            //Display stats
            player.sendMessage(CWUtil.integrateColor("&8========== &4&l" + (otherPlayer == null ? "Your" : (otherPlayer.getName() + "'s")) + " &4event stats &8=========="));
            if (eventType == null || eventType == EventType.SPLEEF) {
                player.sendMessage(CWUtil.integrateColor("&6&lSpleef &8[&a" + stats.getSpleefGamesPlayed() + " &7games&8]"));
                player.sendMessage(CWUtil.integrateColor("&8- Wins&8: &5" + stats.getSpleefWins()));
                player.sendMessage(CWUtil.integrateColor("&8- Blocks spleefed&8: &5" + stats.getSpleefBlocks()));
                player.sendMessage(CWUtil.integrateColor("&8- Snowballs farmed&8: &5" + stats.getSpleefSnowballsFarmed()));
            }
            if (eventType == null || eventType == EventType.RACE) {
                player.sendMessage(CWUtil.integrateColor("&6&lRace &8[&a" + stats.getRaceGamesPlayed() + " &7games&8]"));
                player.sendMessage(CWUtil.integrateColor("&8- Wins&8: &5" + stats.getRaceWins()));
                player.sendMessage(CWUtil.integrateColor("&8- Deaths&8: &5" + stats.getRaceDeaths()));
                player.sendMessage(CWUtil.integrateColor("&8- Lasso uses&8: &5" + stats.getRaceLassoUses()));
            }
            if (eventType == null || eventType == EventType.KOH) {
                player.sendMessage(CWUtil.integrateColor("&6&lKOH &8[&a" + stats.getKohGamesPlayed() + " &7games&8]"));
                player.sendMessage(CWUtil.integrateColor("&8- Wins&8: &5" + stats.getKohWins()));
                player.sendMessage(CWUtil.integrateColor("&8- Kills&8: &5" + stats.getKohKills()));
                player.sendMessage(CWUtil.integrateColor("&8- Deaths&8: &5" + stats.getKohDeaths()));
            }
            if (eventType == null || eventType == EventType.BOMBERMAN) {
                player.sendMessage(CWUtil.integrateColor("&6&lBomberman &8[&a" + stats.getBombermanGamesPlayed() + " &7games&8]"));
                player.sendMessage(CWUtil.integrateColor("&8- Wins&8: &5" + stats.getBombermanWins()));
                player.sendMessage(CWUtil.integrateColor("&8- Kills&8: &5" + stats.getBombermanKills()));
                player.sendMessage(CWUtil.integrateColor("&8- Deaths&8: &5" + stats.getBombermanDeaths()));
                player.sendMessage(CWUtil.integrateColor("&8- Bombs placed&8: &5" + stats.getBombermanBombsPlaced()));
                player.sendMessage(CWUtil.integrateColor("&8- Powerups used&8: &5" + stats.getBombermanPowerups()));
            }
            return true;
        }


        //AUTOJOIN COMMAND
        if (label.equalsIgnoreCase("autojoin") || label.equalsIgnoreCase("autoplay") || label.equalsIgnoreCase("joinauto") || label.equalsIgnoreCase("automaticjoin") || label.equalsIgnoreCase("joinautomatic")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Util.formatMsg("&cThis is a player command only."));
                return true;
            }
            Player player = (Player) sender;

            AutojoinCfg ajCfg = cwe.getAutoJoinCfg();
            if (ajCfg.getAutoJoin(player)) {
                ajCfg.setAutoJoin(player, false);
                player.sendMessage(Util.formatMsg("&6Auto joining &cdisabled&6."));
                player.sendMessage(Util.formatMsg("&7You will have to use &8/event join &7to join events."));
            } else {
                ajCfg.setAutoJoin(player, true);
                player.sendMessage(Util.formatMsg("&6Auto joining &aenabled&6."));
                player.sendMessage(CWUtil.integrateColor("&7When events open you will join automatically. Also when you join and there is an open event you will join automatically."));
            }
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
                    sender.sendMessage(CWUtil.integrateColor("&6/event spawn &8- &5Teleport to the active event."));
                    sender.sendMessage(CWUtil.integrateColor("&6/event reset &8- &5Reset active event. &7(no need to use)"));
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
                if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("play")) {
                    cwe.getEM().joinEvent(player);
                    return true;
                }

                //##########################################################################################################################
                //##################################################### /event leave #######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("quit")) {
                    if (!cwe.getEM().leaveEvent(player, false)) {
                        //Not in a event so teleport player to pvp server.
                        cwe.joinPvP((Player)sender);
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
                    if (cwe.getEM().getStatus() == EventStatus.OPEN || cwe.getEM().getStatus() == EventStatus.STARTED || cwe.getEM().getStatus() == EventStatus.STARTING || cwe.getEM().getStatus() == EventStatus.ENDED) {
                        sender.sendMessage(Util.formatMsg("&cthe game is running or opened already!"));
                        sender.sendMessage(Util.formatMsg("&cUse &4/arena stop &cor &4/arena close &cbefore setting it again."));
                        return true;
                    }
                    if (args.length > 1 && args[1].equalsIgnoreCase("none")) {
                        cwe.getEM().setStatus(EventStatus.CLOSED);
                        cwe.getEM().setEvent(null);
                        cwe.getEM().setArena(null);
                        cwe.getEM().setSlots(-1);
                        cwe.getEM().updateEventItem();
                        sender.sendMessage(Util.formatMsg("&6Cached arena data has been cleared."));
                        return true;
                    }
                    EventType event = null;
                    String arena = "";
                    boolean arenaSet = false;
                    if (args.length > 1 && args[1].contains("_")) {
                        String[] split = args[1].split("_");
                        if (split.length > 1) {
                            event = EventType.fromPrefix(split[0]);
                            if (event == null) {
                                sender.sendMessage(Util.formatMsg("&cInvalid event prefix."));
                                return true;
                            }
                            arena = split[1];
                            arenaSet = true;
                        }
                    }
                    if (!arenaSet && args.length < 3) {
                        sender.sendMessage(Util.formatMsg("&cInvalid usage. &7/event set {event|'none'} {arena} [slots]"));
                        return true;
                    }

                    if (event == null) {
                        event = EventType.fromString(args[1]);
                        if (event == null) {
                            sender.sendMessage(Util.formatMsg("&cInvalid event name."));
                            return true;
                        }
                    }

                    int slots = 0;
                    int argsNeeded = arenaSet ? 3 : 4;
                    if (args.length >= argsNeeded) {
                        slots = CWUtil.getInt(arenaSet ? args[2] : args[3]);
                        if (slots <= 1) {
                            sender.sendMessage(Util.formatMsg("&cInvalid slot amount. Must be number and at least 2."));
                            return true;
                        }
                    } else {
                        slots = -1;
                    }
                    if (slots > 100) {
                        slots = 100;
                    }

                    if (arena.isEmpty() && !arenaSet) {
                        arena = args[2];
                    }
                    if (!event.getEventClass().checkSetup(event, arena, sender)) {
                        return true;
                    }
                    if (!cwe.getEM().checkSetup(event, arena, slots, sender)) {
                        return true;
                    }

                    cwe.getEM().setStatus(EventStatus.CLOSED);
                    cwe.getEM().setEvent(event);
                    cwe.getEM().setArena(arena);
                    if (!event.getEventClass().allowMultiplePeoplePerSpawn() && slots < 1) {
                        cwe.getEM().setSlots(12);
                    } else {
                        cwe.getEM().setSlots(slots);
                    }

                    cwe.getEM().updateEventItem();

                    sender.sendMessage(Util.formatMsg("&6Event set to&8: &5" + event.getName()));
                    sender.sendMessage(Util.formatMsg("&6Arena set to&8: &5" + arena));
                    if (slots > 0) {
                        sender.sendMessage(Util.formatMsg("&6Slots set to&8: &5" + slots));
                    } else {
                        sender.sendMessage(Util.formatMsg("&7No slots are set. Infinite players can join."));
                    }
                    return true;
                }


                EventType activeEvent = cwe.getEM().getEvent();
                String activeArena = cwe.getEM().getArena();
                if (activeEvent == null || activeArena == null) {
                    sender.sendMessage(Util.formatMsg("&cNo cached event/arena data found. &7Set it with &8/event set"));
                    return true;
                }

                //##########################################################################################################################
                //##################################################### /event spawn #######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("spawn")) {
                    if (cwe.getEM().getAllSpawns() != null && cwe.getEM().getAllSpawns().size() > 0) {
                        cwe.getEM().teleportToArena(player, true);
                        sender.sendMessage(Util.formatMsg("&6Teleported to &5" + cwe.getEM().getEvent().getName() + " &6arena &5" + cwe.getEM().getArena() + "&6."));
                    } else {
                        sender.sendMessage(Util.formatMsg("&cNo cached event/arena data found. &7Set it with &8/event set"));
                    }
                    return true;
                }

                //##########################################################################################################################
                //##################################################### /event reset #######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("reset")) {
                    if (cwe.getEM().getStatus() == EventStatus.OPEN || cwe.getEM().getStatus() == EventStatus.STARTED || cwe.getEM().getStatus() == EventStatus.STARTING || cwe.getEM().getStatus() == EventStatus.ENDED) {
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
                    if (cwe.getEM().getStatus() == EventStatus.OPEN || cwe.getEM().getStatus() == EventStatus.STARTED || cwe.getEM().getStatus() == EventStatus.STARTING || cwe.getEM().getStatus() == EventStatus.ENDED) {
                        sender.sendMessage(Util.formatMsg("&cThe game is already running or opened."));
                        return true;
                    }
                    activeEvent.getEventClass().Open();
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
                    if (cwe.getEM().getStatus() != EventStatus.STARTED && cwe.getEM().getStatus() != EventStatus.ENDED) {
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
                    cwe.getEM().stopGame(winner);
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
                for (String p : cwe.getEM().getPlayers().keySet()) {
                    playerStr = playerStr + "&5" + p + "&8, ";
                }
                sender.sendMessage(CWUtil.integrateColor("&6Players&8: &5" + playerStr));
            }
            return true;
        }
        return false;
    }
}
