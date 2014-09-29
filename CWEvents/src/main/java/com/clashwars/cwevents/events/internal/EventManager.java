package com.clashwars.cwevents.events.internal;

import com.clashwars.cwevents.CWEvents;
import com.clashwars.cwevents.Util;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class EventManager {
    private CWEvents cwe;

    private EventType event;
    private String arena;
    private int slots = -1;
    private EventStatus status;
    private Map<String, Integer> players = new HashMap<String, Integer>();

    private List<String> allSpawns = new ArrayList<String>();
    private List<String> spawns = new ArrayList<String>();

    public EventManager(CWEvents cwe) {
        this.cwe = cwe;
    }

    @SuppressWarnings("deprecation")
    public void joinEvent(Player player) {
        //Make sure the event is joinable.
        if (event == null || arena == null) {
            player.sendMessage(Util.formatMsg("&cThere is no active event set."));
            return;
        }
        if (status != EventStatus.OPEN) {
            player.sendMessage(Util.formatMsg("&cYou can't join right now. &4Status&8: &7" + status.getName()));
            return;
        }

        //Make sure player isn't already in the event.
        if (players.containsKey(player.getName())) {
            player.sendMessage(Util.formatMsg("&cYou're already in the game."));
            player.sendMessage(Util.formatMsg("&cYou can use &4/event spawn &cif you didn't get teleported."));
            return;
        }

        //Check for full event and kick out players with low priority if there are any.
        if (slots > 0 && players.size() >= slots) {
            int priority = getPlayerPriority(player);
            Player kick = null;
            //Find a player to kick if none found kick is null and player can't join.
            for (String p : players.keySet()) {
                if (priority > getPlayerPriority(cwe.getServer().getPlayer(p))) {
                    kick = cwe.getServer().getPlayer(p);
                    break;
                }
            }
            //Prevent join if full and no player can be kicked or else kick the found player.
            if (kick == null) {
                player.sendMessage(Util.formatMsg("&6This event is full!"));
                return;
            } else {
                kick.sendMessage(Util.formatMsg("&cYou have been kicked out because someone with higher priority joined."));
                leaveEvent(player, true);
            }
        }

        //Join
        for (int i = 0; i < 100; i++) {
            if (players.values().contains(i)) {
                continue;
            }
            players.put(player.getName(), i);
            break;
        }
        if (teleportToArena(player, false)) {
            player.sendMessage(Util.formatMsg("&3You have joined &9" + event.getName() + "! &3Arena&8: &9" + arena));
            broadcast(Util.formatMsg("&9" + player.getDisplayName() + " &3joined the event. &8Players: &7" + players.size()));
            resetPlayer(player);
            event.getEventClass().onPlayerJoin(player);
            updateEventItem();
        }
    }


    public boolean leaveEvent(Player player, boolean force) {
        if (players.containsKey(player.getName())) {
            event.getEventClass().onPlayerLeft(player);
            resetPlayer(player);
            player.teleport(player.getWorld().getSpawnLocation());
            if (force) {
                player.sendMessage(Util.formatMsg("&cYou have been removed from &4" + event.getName() + "! &cArena&8: &4" + arena));
            } else {
                player.sendMessage(Util.formatMsg("&3You have left &9" + event.getName() + "! &3Arena&8: &9" + arena));
                broadcast(Util.formatMsg("&9" + player.getDisplayName() + " &3left the event."));
            }
            players.remove(player.getName());
            updateEventItem();
            return true;
        }
        return false;
    }


    public boolean teleportToArena(Player player, boolean force) {
        if (event == null) {
            return false;
        }
        if (force) {
            if (allSpawns != null && allSpawns.size() > 0) {
                player.teleport(cwe.getLoc(allSpawns.get(0)));
                return true;
            }
            return false;
        }
        if (event.getEventClass().allowMultiplePeoplePerSpawn()) {
            if (spawns == null || spawns.size() <= 0) {
                spawns = new ArrayList<String>(allSpawns);
            }
            player.teleport(cwe.getLoc(spawns.get(0)));
            spawns.remove(0);
            return true;
        } else {
            if (!players.containsKey(player.getName())) {
                return false;
            }
            String locName = event.getPreifx() + "_" + arena + "_s" + players.get(player.getName());
            if (!cwe.getLocConfig().getLocations().containsKey(locName)) {
                return false;
            }
            player.teleport(cwe.getLoc(locName));
            return true;
        }
    }


    public boolean checkSetup(EventType eventt, String arenaa, int slotss, CommandSender sender) {
        allSpawns.clear();
        if (eventt.getEventClass().allowMultiplePeoplePerSpawn()) {
            //Require at least 1 spawn as it can be used by multiple people.
            for (String locName : cwe.getLocConfig().getLocations().keySet()) {
                if (locName.toLowerCase().startsWith((eventt.getPreifx() + "_" + arenaa + "_s").toLowerCase())) {
                    allSpawns.add(locName);
                }
            }
            if (allSpawns.size() < 1) {
                sender.sendMessage(Util.formatMsg("&cInvalid arena name or spawn location not set."));
                sender.sendMessage(Util.formatMsg("&cThis event needs to have at least 1 spawn."));
                sender.sendMessage(Util.formatMsg("&cSet spawn points like&8: &4" + eventt.getPreifx() + "_" + arenaa + "_s0 &c_s1 etc... "));
                return false;
            }
        } else {
            //Require a spawn for each slot.
            if (slotss <= 1) {
                slotss = 12;
            }
            String locName = "";
            for (int i = 0; i < slotss; i++) {
                locName = eventt.getPreifx() + "_" + arenaa + "_s" + i;
                if (!cwe.getLocConfig().getLocations().keySet().contains(locName)) {
                    sender.sendMessage(Util.formatMsg("&cInvalid arena name or spawn locations not set."));
                    sender.sendMessage(Util.formatMsg("&cThis event needs a spawn location for each slot."));
                    sender.sendMessage(Util.formatMsg("&cMissing spawn location&8: &4" + locName));
                    return false;
                }
                allSpawns.add(locName);
            }
        }
        spawns = new ArrayList<String>(allSpawns);
        return true;
    }


    public EventType getEvent() {
        return event;
    }

    public void setEvent(EventType event) {
        this.event = event;
    }


    public String getArena() {
        return arena;
    }

    public void setArena(String arena) {
        this.arena = arena;
    }


    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }


    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }


    public List<String> getAllSpawns() {
        return allSpawns;
    }

    public List<String> getSpawns() {
        return spawns;
    }


    public Map<String, Integer> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, Integer> players) {
        this.players = players;
    }


    public void resetPlayer(Player player) {
		player.closeInventory();
		player.resetMaxHealth();
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setSaturation(9999);
		player.setFireTicks(0);
		player.setGameMode(GameMode.SURVIVAL);
		player.setFlying(false);
		player.getInventory().clear();
		player.getInventory().setHelmet(new ItemStack(Material.AIR));
		player.getInventory().setChestplate(new ItemStack(Material.AIR));
		player.getInventory().setLeggings(new ItemStack(Material.AIR));
		player.getInventory().setBoots(new ItemStack(Material.AIR));
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
    }


    public void updateEventItem() {
        for (Player p : cwe.getServer().getOnlinePlayers()) {
            if (!players.containsKey(p.getName())) {
                p.getInventory().setItem(0, cwe.GetEventItem());
                p.getInventory().setItem(8, cwe.getLeaveItem());
            }
        }
    }


    public void broadcast(String msg) {
        for (Player p : cwe.getServer().getOnlinePlayers()) {
            if (players.containsKey(p.getName())) {
                p.sendMessage(msg);
            } else if (p.hasPermission("event.notifiy") || p.isOp()) {
                p.sendMessage(msg);
            }
        }
    }

    public void playSound(Sound sound, float volume, float pitch) {
        for (Player p : cwe.getServer().getOnlinePlayers()) {
            if (players.containsKey(p.getName())) {
                p.playSound(p.getLocation(), sound, volume, pitch);
            } else if (p.hasPermission("event.notifiy") || p.isOp()) {
                p.playSound(p.getLocation(), sound, volume, pitch);
            }
        }
    }


    public int getPlayerPriority(Player player) {
        int priority = 0;
        if (player.hasPermission("event.priority.1")) {
            priority = 1;
        }
        if (player.hasPermission("event.priority.2")) {
            priority = 2;
        }
        if (player.hasPermission("event.priority.3")) {
            priority = 3;
        }
        if (player.hasPermission("event.priority.4")) {
            priority = 4;
        }
        if (player.hasPermission("event.priority.5")) {
            priority = 5;
        }
        return priority;
    }

    public String getRegionName(EventType event, String arena, String type) {
        return event.getPreifx() + "_" + arena + "_" + type;
    }

    public String getRegionName(String arena, String type) {
        return getRegionName(event, arena, type);
    }

    public String getRegionName(String type) {
        return getRegionName(event, arena, type);
    }
}
