package com.clashwars.cwevents.events.internal;

import com.clashwars.cwevents.CWEvents;
import com.clashwars.cwevents.Util;
import com.clashwars.cwevents.runnables.StartGameRunnable;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class BaseEvent implements Listener {

    public CWEvents cwe;
    public EventManager em;
    public World world;

    public void Init(CWEvents cwe, EventManager em) {
        this.cwe = cwe;
        this.em = em;
        this.world = cwe.getServer().getWorlds().get(0);
    }

    public void Setup() {

    }

    public void Reset() {
        //Overridden
    }

    public void Open() {
        cwe.getEM().setStatus(EventStatus.OPEN);
        cwe.getEM().updateEventItem();

        cwe.getServer().broadcastMessage(Util.formatMsg("&a&l" + em.getEvent().getName() + " &2&lhas opened! &6Arena&8: &5" + em.getArena()));
        cwe.getServer().broadcastMessage(Util.formatMsg("&4The game will start soon so join quickly!"));

        //Broadcast to the pvp server.
        //TODO: Broadcast to pvp server.

        //Automatic make players with autojoin enabled join.
        for (Player player : cwe.getServer().getOnlinePlayers()) {
            if (em.getPlayers().containsKey(player.getName())) {
                continue;
            }
            if (cwe.getAutoJoinCfg().getAutoJoin(player)) {
                player.sendMessage(Util.formatMsg("&6Automatically joined. &8/autojoin &7to toggle this off."));
                em.joinEvent(player);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void Close() {
        em.broadcast(Util.formatMsg("&cThe game has been closed before it was started!"));
        Set<String> playerClone = new HashSet<String>(em.getPlayers().keySet());
        for (String p : playerClone) {
            em.leaveEvent(cwe.getServer().getPlayer(p), true);
        }
        Set<String> spectatorClone = new HashSet<String>(em.getSpectators().keySet());
        for (String p : spectatorClone) {
            em.leaveEvent(cwe.getServer().getPlayer(p), true);
        }
    }

    public void Start() {
        new StartGameRunnable(cwe, 5).runTaskTimer(cwe, 0, 20);
    }

    public void Begin() {
        //Overridden
    }

    @SuppressWarnings("deprecation")
    public void Stop() {
        em.broadcast(Util.formatMsg("&cThe game has been stopped/ended!"));
        Set<String> playerClone = new HashSet<String>(em.getPlayers().keySet());
        for (String p : playerClone) {
            em.leaveEvent(cwe.getServer().getPlayer(p), true);
        }
        Set<String> spectatorClone = new HashSet<String>(em.getSpectators().keySet());
        for (String p : spectatorClone) {
            em.leaveEvent(cwe.getServer().getPlayer(p), true);
        }
        cwe.getStats().syncAllStats();
    }

    public void onPlayerLeft(Player player) {
        //Overridden
    }

    public void onPlayerJoin(Player player) {
        //Overridden
    }

    public boolean checkSetup(EventType event, String arena, CommandSender sender) {
        //Overridden
        return true;
    }

    public boolean allowMultiplePeoplePerSpawn() {
        return true;
    }
}
