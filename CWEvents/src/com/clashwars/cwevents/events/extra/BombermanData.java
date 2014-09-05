package com.clashwars.cwevents.events.extra;

public class BombermanData {
	
	private String playerName;
	private long fuseTime;
	private float explosionSize;
	private int speed;
	private int bombs;
	
	public BombermanData(String playerName) {
		this.setPlayerName(playerName);
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

	public float getExplosionSize() {
		return explosionSize;
	}

	public void setExplosionSize(float explosionSize) {
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
	
	
	
}
