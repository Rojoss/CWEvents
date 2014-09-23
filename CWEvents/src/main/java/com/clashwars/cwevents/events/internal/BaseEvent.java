package com.clashwars.cwevents.events.internal;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwevents.CWEvents;
import com.clashwars.cwevents.runnables.StartGameRunnable;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class BaseEvent implements Listener {

    public CWEvents cwe;
    public EventManager em;
    public World world;

    public List<String> regionsNeeded = new ArrayList<String>();
    public List<String> locationsNeeded = new ArrayList<String>();

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
        cwe.getServer().broadcastMessage(CWUtil.formatMsg("&a&l" + em.getEvent().getName() + " &2&lhas opened! &6Arena&8: &5" + em.getArena()));
        cwe.getServer().broadcastMessage(CWUtil.formatMsg("&4The game will start soon so join quickly!"));
    }

    @SuppressWarnings("deprecation")
    public void Close() {
        em.broadcast(CWUtil.formatMsg("&cThe game has been closed before it was started!"));
        for (String p : em.getPlayers()) {
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
        em.broadcast(CWUtil.formatMsg("&cThe game has been stopped/ended!"));
        for (String p : em.getPlayers()) {
            em.leaveEvent(cwe.getServer().getPlayer(p), true);
        }
    }

    public void onPlayerLeft(Player player) {
        //Overridden
    }

    public void onPlayerJoin(Player player) {
        //Overridden
    }
}
