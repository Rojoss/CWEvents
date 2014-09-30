package com.clashwars.cwevents.events.internal;

public class SpectateData {

    private String player;
    private boolean following = false;
    private int playerIndex = 0;

    public SpectateData(String player, int playerIndex) {
        this.player = player;
        this.playerIndex = playerIndex;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }
}
