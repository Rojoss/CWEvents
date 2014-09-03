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
		} else if (seconds > 1) {
			cwe.getEM().broadcast(Util.formatMsg("&6Starting in " + seconds + " seconds!"));
		}
		
		if (seconds <= 0) {
			if (cwe.getEM().getPlayers().size() < 2) {
				cwe.getEM().broadcast(Util.formatMsg("&6Not enough players to start the game..."));
				cwe.getEM().getEvent().getEventClass().Open();
				cwe.getEM().setStatus(EventStatus.OPEN);
				cwe.getEM().updateEventItem();
			} else {
				cwe.getEM().getEvent().getEventClass().Begin();
				cwe.getEM().setStatus(EventStatus.STARTED);
				cwe.getEM().updateEventItem();
				cwe.getEM().broadcast(Util.formatMsg("&6The game has started!"));
			}
			this.cancel();
		}
		seconds--;
	}
}
