package com.clashwars.cwevents.events.internal;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwevents.CWEvents;
import com.clashwars.cwevents.Util;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.*;

public class EventManager {
    private CWEvents cwe;

    private EventType event;
    private String arena;
    private int slots = -1;
    private EventStatus status;
    private Map<String, Integer> players = new HashMap<String, Integer>();
    private Map<String, SpectateData> spectators = new HashMap<String, SpectateData>();

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
        if (status == EventStatus.STARTED || status == EventStatus.STARTING || status == EventStatus.ENDED) {
            player.sendMessage(Util.formatMsg("&cThe game was already started."));
            spectateEvent(player);
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
        if (spectators.containsKey(player.getName())) {
            resetPlayer(player);
            player.teleport(player.getWorld().getSpawnLocation());
            if (force) {
                player.sendMessage(Util.formatMsg("&cYou have been removed from &4" + event.getName() + "! &cArena&8: &4" + arena));
            } else {
                player.sendMessage(Util.formatMsg("&3You have stopped spectating &9" + event.getName() + "! &3Arena&8: &9" + arena));
                broadcast(Util.formatMsg("&9" + player.getDisplayName() + " &3stopped spectating."));
            }
            spectators.remove(player.getName());
            updateEventItem();
            return true;
        }
        return false;
    }

    public void spectateEvent(final Player player) {
        if (spectators.containsKey(player)) {
            return;
        }
        resetPlayer(player);
        if (players.containsKey(player.getName())) {
            leaveEvent(player, true);
        }
        spectators.put(player.getName(), new SpectateData(player.getName(), players.size() > 0 ? players.values().iterator().next() : -1));
        cwe.getSpecTeam().addPlayer(player);
        player.setAllowFlight(true);
        player.setFlying(true);
        for (String p : players.keySet()) {
            cwe.getServer().getPlayer(p).hidePlayer(player);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999999, 0));
        cwe.getServer().getScheduler().scheduleSyncDelayedTask(cwe, new Runnable() {
            public void run() {
                teleportToArena(player, true);
            }
        }, 10L);
        updateSpectatorInv(player);
        player.sendMessage(Util.formatMsg("&3You are now spectating &9" + event.getName() + "! &3Arena&8: &9" + arena));
        broadcast(Util.formatMsg("&9" + player.getDisplayName() + " &3is now spectating. &8Spectators: &7" + spectators.size()));
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
            if (!cwe.getLocCfg().getLocations().containsKey(locName)) {
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
            for (String locName : cwe.getLocCfg().getLocations().keySet()) {
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
                if (!cwe.getLocCfg().getLocations().keySet().contains(locName)) {
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
        for (Player p : cwe.getServer().getOnlinePlayers()) {
            p.showPlayer(player);
            player.showPlayer(p);
        }
        if (cwe.getSpecTeam().getPlayers().contains(player)) {
            cwe.getSpecTeam().removePlayer(player);
        }
    }


    public void stopGame(Player winner) {
        stopGame(winner.getUniqueId());
    }

    public void stopGame(UUID winner) {
        OfflinePlayer w = cwe.getServer().getOfflinePlayer(winner);
        if (winner != null && w != null && w.isOnline()) {
            ((Player)w).sendMessage(Util.formatMsg("&a&lYou won the game!"));
            ((Player)w).sendMessage(Util.formatMsg("&6When you join the pvp server you will receive a reward."));
        }

        setStatus(EventStatus.STOPPED);
        getEvent().getEventClass().Stop();

        cwe.getStats().syncAllStats();

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
    }


    public void updateEventItem() {
        for (Player p : cwe.getServer().getOnlinePlayers()) {
            if (!players.containsKey(p.getName()) && !spectators.containsKey(p.getName())) {
                p.getInventory().setItem(0, cwe.GetEventItem());
                p.getInventory().setItem(8, cwe.getLeaveItem());
                p.updateInventory();
            }
        }
    }

    public void updateSpectatorInv(Player player) {
        if (!spectators.containsKey(player.getName())) {
            return;
        }
        SpectateData data = spectators.get(player.getName());
        if (data.isFollowing()) {
            player.getInventory().setItem(0, new CWItem(Material.INK_SACK, 1, (byte)10).setName("&4&lStop Following")
                    .addLore("&7Stop following this player.").addLore("&7You will be able to move around freely again."));
        } else {
            player.getInventory().setItem(0, new CWItem(Material.INK_SACK, 1, (byte)9).setName("&4&lStart Following")
                    .addLore("&7Click to start following a player.").addLore("&7You will follow the player in your 5th slot."));
        }
        int ID = -1;
        boolean set = false;
        for (String p : players.keySet()) {
            ID = players.get(p);
            if (data.getPlayerIndex() == ID) {
                player.getInventory().setItem(4, new CWItem(Material.SKULL_ITEM).setName("&6&l" + p)
                        .addLore("&7This indicates the player you're following or can follow.")
                        .addLore("&2Left click&8: &aTeleport to this player.").addLore("&9Right click&8: &3Switch to another player.").setSkullOwner(p));
                set = true;
                break;
            }
        }
        if (!set) {
            player.getInventory().setItem(4, new CWItem(Material.SKULL_ITEM).setSkullOwner("steve").setName("&4&lNo players")
                    .addLore("&7There are no players in the game who you can spectate.")
                    .addLore("&9Right click&8: &3Try switch to another player."));
            set = true;
        }
        player.getInventory().setItem(8, new CWItem(Material.REDSTONE_BLOCK).setName("&4&lStop Spectating")
                .addLore("&7Use this to stop spectating.").addLore("&7You will be teleported back to the lobby."));
        player.updateInventory();
    }


    public void setFollowing(Player player, boolean follow) {
        if (!spectators.containsKey(player.getName())) {
            return;
        }
        SpectateData data = spectators.get(player.getName());
        Player target = null;
        if (data.getPlayerIndex() < 0) {
            player.sendMessage(Util.formatMsg("&cNo player selected."));
            player.sendMessage(Util.formatMsg("&cYou can try selecting one by right clicking the skull"));
            return;
        }
        if (getPlayerByID(data.getPlayerIndex()) == null) {
            player.sendMessage(Util.formatMsg("&cInvalid player."));
        }
        target = Bukkit.getServer().getPlayer(getPlayerByID(data.getPlayerIndex()));
        data.setFollowing(follow);
        if (target != null) {
            if (follow) {
                player.sendMessage(Util.formatMsg("&6You are now following &5" + target.getName()));
                player.teleport(target);
                player.hidePlayer(target);
            } else {
                player.sendMessage(Util.formatMsg("&cYou stopped following &5" + target.getName()));
                player.showPlayer(target);
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


    public String getPlayerByID(int ID) {
        for (String player : players.keySet()) {
            if (players.get(player) == ID) {
                return player;
            }
        }
        return null;
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

    public Map<String, SpectateData> getSpectators() {
        return spectators;
    }

    public void setSpectators(Map<String, SpectateData> spectators) {
        this.spectators = spectators;
    }
}
