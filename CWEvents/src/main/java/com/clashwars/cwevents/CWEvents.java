package com.clashwars.cwevents;

import com.clashwars.cwcore.CWCore;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwevents.commands.Commands;
import com.clashwars.cwevents.config.LocConfig;
import com.clashwars.cwevents.event.MainEvents;
import com.clashwars.cwevents.event.PluginMessageEvents;
import com.clashwars.cwevents.events.internal.EventManager;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import java.util.logging.Logger;

public class CWEvents extends JavaPlugin {
    private static CWEvents instance;

    private Commands cmds;
    private EventManager em;
    private LocConfig locCfg;

    private final Logger log = Logger.getLogger("Minecraft");

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
        log("Disabled.");
    }

    @SuppressWarnings("deprecation")
    public void onEnable() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CWCore");
        if (plugin == null || !(plugin instanceof CWCore)) {
            log("CWCore dependency couldn't be loaded!");
            setEnabled(false);
            return;
        }

        instance = this;

        locCfg = new LocConfig("plugins/CWEvents/locs.yml");
        locCfg.load();

        cmds = new Commands(this);

        em = new EventManager(this);

        for (EventType event : EventType.values()) {
            event.getEventClass().Init(this, em);
        }

        registerEvents();
        registerChannels();

        for (Player p : getServer().getOnlinePlayers()) {
            em.resetPlayer(p);
        }
        em.updateEventItem();


        log("Successfully enabled.");
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MainEvents(this), this);
        for (EventType event : EventType.values()) {
            if (event.getEventClass() != null) {
                pm.registerEvents(event.getEventClass(), this);
            }
        }
    }

    private void registerChannels() {
        Messenger msg = getServer().getMessenger();

        msg.registerIncomingPluginChannel(this, "CWBungee", new PluginMessageEvents(this));
        msg.registerOutgoingPluginChannel(this, "CWBungee");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return cmds.onCommand(sender, cmd, label, args);
    }

    public void log(Object msg) {
        log.info("[CWEvents " + getDescription().getVersion() + "]: " + msg.toString());
    }


    public static CWEvents inst() {
        return instance;
    }

    public EventManager getEM() {
        return em;
    }

    public LocConfig getLocConfig() {
        return locCfg;
    }

    public void tpLoc(Player player, String name) {
        String properName = locCfg.getName(name);
        if (properName != "") {
            player.teleport(locCfg.getLoc(properName));
        }
    }

    public Location getLoc(String name) {
        String properName = locCfg.getName(name);
        if (properName != "") {
            return locCfg.getLoc(properName);
        }
        return null;
    }

    public CWItem GetEventItem() {
        if (em.getEvent() == null || em.getArena() == null) {
            return new CWItem(Material.INK_SACK, 1, (short)1, "&4&lNo Event").addLore("&7There is currently no open event!");
        } else {
            if (em.getStatus() == EventStatus.OPEN) {
                return new CWItem(Material.INK_SACK, 1, (short) 10, "&6&lJoin &5&l" + em.getEvent().getName(), new String[]{
                        "&7Use this item to join &8" + em.getEvent().getName() + "&7.", "&6&lEvent&8&l: &5" + em.getEvent().getName(),
                        "&6&lArena&8&l: &5" + em.getArena(), "&6&lPlayers&8&l: &a" + em.getPlayers().size() + "&7/&2" + em.getSlots()});
            } else {
                return new CWItem(Material.INK_SACK, 1, (short) 8, "&c&lNot Joinable", new String[]{
                        "&7The event is not joinable.", "&6&lEvent&8&l: &5" + em.getEvent().getName(), "&6&lArena&8&l: &5" + em.getArena(),
                        "&6&lStatus&8&l: &5" + em.getStatus().getName()});
            }
        }
    }
}
