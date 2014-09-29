package com.clashwars.cwevents.events.extra;

public class BombermanData {

    private String playerName;
    private int lives;
    private long fuseTime;
    private int explosionSize;
    private int speed;
    private int bombs;
    private int pierceBombs;

    public BombermanData(String playerName) {
        this.setPlayerName(playerName);
        lives = 3;
        fuseTime = 80;
        explosionSize = 1;
        speed = 0;
        bombs = 1;
        pierceBombs = 0;
    }


    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }


    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
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


    public int getPierceBombs() {
        return pierceBombs;
    }

    public void setPierceBombs(int pierceBombs) {
        this.pierceBombs = pierceBombs;
    }

}
