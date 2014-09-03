package com.clashwars.cwevents.events.internal;

public enum EventStatus {
	NONE("&7Unknown"),
	OPEN("&aOpen"),
	STARTING("&eStarting"),
	STARTED("&5Started"),
	STOPPED("&4Stopped"),
	RESETTING("&cResetting"),
	CLOSED("&cClosed");
	
	private String name;
	
	EventStatus(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
