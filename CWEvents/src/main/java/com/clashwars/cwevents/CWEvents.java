package com.clashwars.cwevents;

import com.clashwars.cwcore.CWCore;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwevents.commands.Commands;
import com.clashwars.cwevents.config.LocConfig;
import com.clashwars.cwevents.event.MainEvents;
import com.clashwars.cwevents.event.PluginMessageEvents;
import com.clashwars.cwevents.events.internal.EventManager;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class CWEvents extends JavaPlugin {
    private static CWEvents instance;

    private Commands cmds;
    private EventManager em;
    private LocConfig locCfg;

    private ScoreboardManager sbm;
    private Scoreboard sb;

    private final Logger log = Logger.getLogger("Minecraft");

    public void onDisable() {
        Set<String> playerClone = new HashSet<String>(em.getPlayers().keySet());
        for (String p : playerClone) {
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

        sbm = getServer().getScoreboardManager();
        sb = sbm.getNewScoreboard();
        sb.registerNewTeam("Spectators");
        getSpecTeam().setCanSeeFriendlyInvisibles(true);
        getSpecTeam().setPrefix(CWUtil.integrateColor("&5"));

        for (EventType event : EventType.values()) {
            event.getEventClass().Init(this, em);
        }

        registerEvents();
        registerChannels();

        for (Player p : getServer().getOnlinePlayers()) {
            em.resetPlayer(p);
            p.teleport(p.getWorld().getSpawnLocation());
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

    public void joinPvP(Player player) {
        player.sendMessage(Util.formatMsg("&aTeleporting to the PvP server..."));
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            out.writeUTF("tpserver");
            out.writeUTF(player.getName());
            out.writeUTF("pvp");

            Bukkit.getOnlinePlayers()[0].sendPluginMessage(this, "CWBungee", b.toByteArray());
            //TODO: Wait a little and if player is still on server give error msg.
        } catch (Throwable e) {
            player.sendMessage(Util.formatMsg("&cError connecting to pvp server."));
            e.printStackTrace();
        }
    }


    public static CWEvents inst() {
        return instance;
    }

    public EventManager getEM() {
        return em;
    }

    public Scoreboard getSB() {
        return sb;
    }

    public Team getSpecTeam() {
        return sb.getTeam("Spectators");
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
            return new CWItem(Material.INK_SACK, 1, (short) 1, "&4&lNo Event").addLore("&7There is currently no open event!");
        } else {
            if (em.getStatus() == EventStatus.OPEN) {
                return new CWItem(Material.INK_SACK, 1, (short) 10, "&6&lJoin &5&l" + em.getEvent().getName(), new String[]{
                        "&7Use this item to join &8" + em.getEvent().getName() + "&7.", "&6&lEvent&8&l: &5" + em.getEvent().getName(),
                        "&6&lArena&8&l: &5" + em.getArena(), "&6&lPlayers&8&l: &a" + em.getPlayers().size() + "&7/&2" + em.getSlots()});
            } else if (em.getStatus() == EventStatus.STARTING || em.getStatus() == EventStatus.STARTED || em.getStatus() == EventStatus.ENDED) {
                return new CWItem(Material.INK_SACK, 1, (short) 5, "&5&lSpectate", new String[]{
                        "&7The event has already started.", "&7Use this item to spectate &8 " + em.getEvent().getName(), "&6&lEvent&8&l: &5" + em.getEvent().getName(),
                        "&6&lArena&8&l: &5" + em.getArena(), "&6&lStatus&8&l: &5" + em.getStatus().getName()});
            } else {
                return new CWItem(Material.INK_SACK, 1, (short) 8, "&c&lNot Joinable", new String[]{
                        "&7The event is not joinable.", "&6&lEvent&8&l: &5" + em.getEvent().getName(), "&6&lArena&8&l: &5" + em.getArena(),
                        "&6&lStatus&8&l: &5" + em.getStatus().getName()});
            }
        }
    }
    public CWItem getLeaveItem() {
        return new CWItem(Material.REDSTONE_BLOCK, 1, (short) 0, "&4&lLeave Events").addLore("&7Go back to the PvP server!!");
    }
}
