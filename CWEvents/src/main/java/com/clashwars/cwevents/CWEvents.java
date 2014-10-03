package com.clashwars.cwevents;

import com.clashwars.cwcore.CWCore;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwevents.commands.Commands;
import com.clashwars.cwevents.config.AutojoinCfg;
import com.clashwars.cwevents.config.LocCfg;
import com.clashwars.cwevents.event.MainEvents;
import com.clashwars.cwevents.event.PluginMessageEvents;
import com.clashwars.cwevents.event.SpectateEvents;
import com.clashwars.cwevents.events.internal.EventManager;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import com.clashwars.cwevents.sql.MySql;
import com.clashwars.cwevents.config.SqlInfo;
import com.clashwars.cwevents.stats.StatsManager;
import com.gmail.filoghost.holograms.HolographicDisplays;
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
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
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class CWEvents extends JavaPlugin {
    private static CWEvents instance;

    private Commands cmds;
    private EventManager em;
    private StatsManager stats;

    private LocCfg locCfg;
    private AutojoinCfg autojoinCfg;

    private MySql sql = null;
    private Connection c = null;

    private Hologram lobbyHologram;

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
        Set<String> spectatorClone = new HashSet<String>(em.getSpectators().keySet());
        for (String p : spectatorClone) {
            if (getServer().getPlayer(p) != null) {
                em.leaveEvent(getServer().getPlayer(p), true);
            }
        }
        stats.syncAllStats();

        if (lobbyHologram != null) {
            lobbyHologram.delete();
            lobbyHologram = null;
        }

        em.setStatus(null);
        em.setArena(null);
        em.setEvent(null);
        em.updateEventItem();
        log("Disabled.");
    }

    @SuppressWarnings("deprecation")
    public void onEnable() {
        //Make sure CWCore is loaded.
        Plugin plugin = getServer().getPluginManager().getPlugin("CWCore");
        if (plugin == null || !(plugin instanceof CWCore)) {
            log("CWCore dependency couldn't be loaded!");
            setEnabled(false);
            return;
        }
        instance = this;

        locCfg = new LocCfg("plugins/CWEvents/locs.yml");
        locCfg.load();

        //Create hologram
        plugin = getServer().getPluginManager().getPlugin("HolographicDisplays");
        if (plugin == null || !(plugin instanceof HolographicDisplays)) {
            log("HolographicDisplays dependency couldn't be loaded!");
            log("No holograms will be created...");
        } else {
            lobbyHologram = HolographicDisplaysAPI.createHologram(this, getLoc("hologram_lobby"), CWUtil.integrateColor(new String[] {"&aLoading event data..."}));
        }
        updateHologram();

        //MySql connection
        SqlInfo sqli = new SqlInfo("plugins/CWEvents/sql.yml");
        sqli.load();

        sql = new MySql(this, sqli.getAddress(), sqli.getPort(), sqli.getDb(), sqli.getUser(), sqli.getPass());
        c = sql.openConnection();
        if (c == null) {
            log("Can't connect to database!");
            getPluginLoader().disablePlugin(this);
            return;
        }

        //AutoJoin config load
        autojoinCfg = new AutojoinCfg("plugins/CWEvents/autojoiners.yml");
        autojoinCfg.load();

        //Commands
        cmds = new Commands(this);

        //The events manager
        em = new EventManager(this);

        //Spectators scoreboard team.
        sbm = getServer().getScoreboardManager();
        sb = sbm.getMainScoreboard();
        if (!sb.getTeams().contains("Spectators") && getSpecTeam() == null) {
            sb.registerNewTeam("Spectators");
        }
        getSpecTeam().setCanSeeFriendlyInvisibles(true);
        getSpecTeam().setPrefix(CWUtil.integrateColor("&5"));

        //Init all events
        for (EventType event : EventType.values()) {
            event.getEventClass().Init(this, em);
        }

        //Register events and bungee message channel.
        registerEvents();
        registerChannels();

        //Stats
        stats = new StatsManager(this);
        stats.syncAllStats();

        //Reset all players.
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
        pm.registerEvents(new SpectateEvents(this), this);
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

    public StatsManager getStats() {
        return stats;
    }

    public Connection getSql() {
        return c;
    }

    public Scoreboard getSB() {
        return sb;
    }

    public Team getSpecTeam() {
        return sb.getTeam("Spectators");
    }

    public LocCfg getLocCfg() {
        return locCfg;
    }

    public AutojoinCfg getAutoJoinCfg() {
        return autojoinCfg;
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

    public void updateHologram() {
        if (lobbyHologram == null) {
            return;
        }
        lobbyHologram.teleport(getLoc("hologram_lobby"));
        lobbyHologram.clearLines();
        lobbyHologram.addLine(CWUtil.integrateColor("&5&l✦&d&l✦&5&l✦ &6&lEVENT STATUS &5&l✦&d&l✦&5&l✦"));
        if (em == null || em.getEvent() == null) {
            lobbyHologram.addLine(CWUtil.integrateColor("&4&lNo event right now..."));
            lobbyHologram.addLine(CWUtil.integrateColor("&7(Events are manually hosted by staff)"));
            lobbyHologram.addLine(CWUtil.integrateColor("&7(This means you can't always play events!)"));
            lobbyHologram.update();
            return;
        }
        lobbyHologram.addLine(CWUtil.integrateColor("&6&lEvent&8&l: &5" + em.getEvent().getColor() + em.getEvent().getName()));
        lobbyHologram.addLine(CWUtil.integrateColor("&6&lArena&8&l: &5" + em.getArena()));
        lobbyHologram.addLine(CWUtil.integrateColor("&6&lStatus&8&l: &5" + em.getStatus().getName()));
        lobbyHologram.addLine(CWUtil.integrateColor("&6&lPlayers&8&l: " + (em.getPlayers().size() < 1 ? "&c" : "&a") + em.getPlayers().size() + "&7/"
                + (em.getPlayers().size() < 1 ? "&4" : "&2") + (em.getSlots() < 1 ? "Inf" : em.getSlots()) + " &7- &8[&d" + em.getSpectators().size() + " &5spec&8]"));
        if (em.getStatus() == EventStatus.OPEN) {
            lobbyHologram.addLine(CWUtil.integrateColor("&aYou can &2join &athis event now!"));
        } else if (em.getStatus() == EventStatus.STARTED || em.getStatus() == EventStatus.STARTING) {
            lobbyHologram.addLine(CWUtil.integrateColor("&dYou can &5spectate &dthis event now!"));
        }
        lobbyHologram.update();
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
    public CWItem getStatsItem() {
        return new CWItem(Material.WRITTEN_BOOK, 1, (short) 0, "&5&lStats").addLore("&7Check your stats!!");
    }
}
