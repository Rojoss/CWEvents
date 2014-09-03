package com.clashwars.cwevents.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import com.clashwars.cwevents.CWEvents;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.utils.Util;


public class StartGameRunnable extends BukkitRunnable {
	
	private CWEvents cwe;
	private int seconds;
	
	public StartGameRunnable(CWEvents cwe, int seconds) {
		this.cwe = cwe;
		this.seconds = seconds;
	}
	
	
	@Override
	public void run() {
		if (seconds == 1) {
			cwe.getEM().broadcast(Util.formatMsg("&6Starting in " + seconds + " second!"));
		} else {
			cwe.getEM().broadcast(Util.formatMsg("&6Starting in " + seconds + " seconds!"));
		}
		seconds--;
		if (seconds <= 0) {
			cwe.getEM().getEvent().getEventClass().Begin();
			cwe.getEM().setStatus(EventStatus.STARTED);
			cwe.getEM().broadcast(Util.formatMsg("&6The game has started!"));
			this.cancel();
		}
	}
}
