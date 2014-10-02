package com.clashwars.cwevents.stats;

public class Stats {

    private int koh_gamesPlayed = 0;
    private int koh_wins = 0;
    private int koh_kills = 0;
    private int koh_deaths = 0;

    private int race_gamesPlayed = 0;
    private int race_wins = 0;
    private int race_deaths = 0;
    private int race_lassoUses = 0;

    private int spleef_gamesPlayed = 0;
    private int spleef_wins = 0;
    private int spleef_blocks = 0;
    private int spleef_snowballsFarmed = 0;

    private int bomberman_gamesPlayed = 0;
    private int bomberman_wins = 0;
    private int bomberman_bombsPlaced = 0;
    private int bomberman_kills = 0;
    private int bomberman_deaths = 0;
    private int bomberman_powerups = 0;

    public Stats() {
    }

    // KOH
    public int getKohGamesPlayed() {
        return koh_gamesPlayed;
    }
    public void setKohGamesPlayed(int val) {
        this.koh_gamesPlayed = val;
    }
    public void incKohGamesPlayed(int val) {
        this.koh_gamesPlayed += val;
    }

    public int getKohWins() {
        return koh_wins;
    }
    public void setKohWins(int val) {
        this.koh_wins = val;
    }
    public void incKohWins(int val) {
        this.koh_wins += val;
    }

    public int getKohKills() {
        return koh_kills;
    }
    public void setKohKills(int val) {
        this.koh_kills = val;
    }
    public void incKohKills(int val) {
        this.koh_kills += val;
    }

    public int getKohDeaths() {
        return koh_deaths;
    }
    public void setKohDeaths(int val) {
        this.koh_deaths = val;
    }
    public void incKohDeaths(int val) {
        this.koh_deaths += val;
    }


    // RACE
    public int getRaceGamesPlayed() {
        return race_gamesPlayed;
    }
    public void setRaceGamesPlayed(int val) {
        this.race_gamesPlayed = val;
    }
    public void incRaceGamesPlayed(int val) {
        this.race_gamesPlayed += val;
    }

    public int getRaceWins() {
        return race_wins;
    }
    public void setRaceWins(int val) {
        this.race_wins = val;
    }
    public void incRaceWins(int val) {
        this.race_wins += val;
    }

    public int getRaceDeaths() {
        return race_deaths;
    }
    public void setRaceDeaths(int val) {
        this.race_deaths = val;
    }
    public void incRaceDeaths(int val) {
        this.race_deaths += val;
    }

    public int getRaceLassoUses() {
        return race_lassoUses;
    }
    public void setRaceLassoUses(int val) {
        this.race_lassoUses = val;
    }
    public void incRaceLassoUses(int val) {
        this.race_lassoUses += val;
    }


    // SPLEEF
    public int getSpleefGamesPlayed() {
        return spleef_gamesPlayed;
    }
    public void setSpleefGamesPlayed(int val) {
        this.spleef_gamesPlayed = val;
    }
    public void incSpleefGamesPlayed(int val) {
        this.spleef_gamesPlayed += val;
    }

    public int getSpleefWins() {
        return spleef_wins;
    }
    public void setSpleefWins(int val) {
        this.spleef_wins = val;
    }
    public void incSpleefWins(int val) {
        this.spleef_wins += val;
    }

    public int getSpleefBlocks() {
        return spleef_blocks;
    }
    public void setSpleefBlocks(int val) {
        this.spleef_blocks = val;
    }
    public void incSpleefBlocks(int val) {
        this.spleef_blocks += val;
    }

    public int getSpleefSnowballsFarmed() {
        return spleef_snowballsFarmed;
    }
    public void setSpleefSnowballsFarmed(int val) {
        this.spleef_snowballsFarmed = val;
    }
    public void incSpleefSnowballsFarmed(int val) {
        this.spleef_snowballsFarmed += val;
    }


    // BOMBERMAN
    public int getBombermanGamesPlayed() {
        return bomberman_gamesPlayed;
    }
    public void setBombermanGamesPlayed(int val) {
        this.bomberman_gamesPlayed = val;
    }
    public void incBombermanGamesPlayed(int val) {
        this.bomberman_gamesPlayed += val;
    }

    public int getBombermanWins() {
        return bomberman_wins;
    }
    public void setBombermanWins(int val) {
        this.bomberman_wins = val;
    }
    public void incBombermanWins(int val) {
        this.bomberman_wins += val;
    }

    public int getBombermanBombsPlaced() {
        return bomberman_bombsPlaced;
    }
    public void setBombermanBombsPlaced(int val) {
        this.bomberman_bombsPlaced = val;
    }
    public void incBombermanBombsPlaced(int val) {
        this.bomberman_bombsPlaced += val;
    }

    public int getBombermanKills() {
        return bomberman_kills;
    }
    public void setBombermanKills(int val) {
        this.bomberman_kills = val;
    }
    public void incBombermanKills(int val) {
        this.bomberman_kills += val;
    }

    public int getBombermanDeaths() {
        return bomberman_deaths;
    }
    public void setBombermanDeaths(int val) {
        this.bomberman_deaths = val;
    }
    public void incBombermanDeaths(int val) {
        this.bomberman_deaths += val;
    }

    public int getBombermanPowerups() {
        return bomberman_powerups;
    }
    public void setBombermanPowerups(int val) {
        this.bomberman_powerups = val;
    }
    public void incBombermanPowerups(int val) {
        this.bomberman_powerups += val;
    }
}
