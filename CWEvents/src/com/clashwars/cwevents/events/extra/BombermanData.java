package com.clashwars.cwevents.events.extra;

public class BombermanData {
	
	private String playerName;
	private int id;
	private long fuseTime;
	private int explosionSize;
	private int speed;
	private int bombs;
	
	public BombermanData(String playerName) {
		this.setPlayerName(playerName);
		id = -1;
		fuseTime = 80;
		explosionSize = 1;
		speed = 0;
		bombs = 1;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public long getFuseTime() {
		return fuseTime;
	}

	public void setFuseTime(long fuseTime) {
		this.fuseTime = fuseTime;
	}

	public int getExplosionSize() {
		return explosionSize;
	}

	public void setExplosionSize(int explosionSize) {
		this.explosionSize = explosionSize;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getBombs() {
		return bombs;
	}

	public void setBombs(int bombs) {
		this.bombs = bombs;
	}

	public int getID() {
		return id;
	}
	
	public void setID(int playerID) {
		this.id = playerID;
	}
	
	
	
}
