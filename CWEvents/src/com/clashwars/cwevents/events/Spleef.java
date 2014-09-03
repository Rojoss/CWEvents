package com.clashwars.cwevents.events;

import org.bukkit.entity.Player;

import com.clashwars.cwevents.events.internal.BaseEvent;

public class Spleef extends BaseEvent {
	
	public void Reset() {
		cwe.getServer().broadcastMessage("Spleef Reset");
	}
	
	public void Open() {
		cwe.getServer().broadcastMessage("Spleef Open");
	}
	
	public void Start() {
		super.Start();
		cwe.getServer().broadcastMessage("Spleef Start");
	}
	
	public void Begin() {
		cwe.getServer().broadcastMessage("Spleef Begin");
	}
	
	public void Stop() {
		super.Stop();
		cwe.getServer().broadcastMessage("Spleef Stop");
	}

	public void onPlayerLeft(Player player) {
		cwe.getServer().broadcastMessage("Spleef player left");
	}

	public void onPlayerJoin(Player player) {
		cwe.getServer().broadcastMessage("Spleef player joined");
	}
}
