package com.clashwars.cwevents.events;

import org.bukkit.entity.Player;

import com.clashwars.cwevents.events.internal.BaseEvent;

public class Spleef extends BaseEvent {
	
	public void Reset() {
		cwe.getServer().broadcastMessage("Spleef Reset");
	}
	
	public void Open() {
		super.Open();
		cwe.getServer().broadcastMessage("Spleef Open");
	}
	
	public void Close() {
		cwe.getServer().broadcastMessage("Spleef Close");
		super.Close();
	}
	
	public void Start() {
		super.Start();
		cwe.getServer().broadcastMessage("Spleef Start");
	}
	
	public void Begin() {
		cwe.getServer().broadcastMessage("Spleef Begin");
	}
	
	public void Stop() {
		cwe.getServer().broadcastMessage("Spleef Stop");
		super.Stop();
	}

	public void onPlayerLeft(Player player) {
		cwe.getServer().broadcastMessage("Spleef player left");
	}

	public void onPlayerJoin(Player player) {
		cwe.getServer().broadcastMessage("Spleef player joined");
	}
}
