package com.clashwars.cwevents.events.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.clashwars.cwevents.CWEvents;
import com.clashwars.cwevents.runnables.StartGameRunnable;

public class BaseEvent implements Listener {

	public CWEvents cwe;
	public EventManager em;
	
	public void Init(CWEvents cwe, EventManager em) {
		this.cwe = cwe;
		this.em = em;
	}
	
	public void Reset() {
		//Overridden
	}
	
	public void Open() {
		//Overridden
	}
	
	public void Start() {
		new StartGameRunnable(cwe, 5).runTaskTimer(cwe.getPlugin(), 0, 20);
	}
	
	public void Begin() {
		//Overridden
	}
	
	@SuppressWarnings("deprecation")
	public void Stop() {
		for (String p : em.getPlayers()) {
			em.leaveEvent(cwe.getServer().getPlayer(p));
		}
	}

	public void onPlayerLeft(Player player) {
		//Overridden
	}

	public void onPlayerJoin(Player player) {
		//Overridden
	}
}
