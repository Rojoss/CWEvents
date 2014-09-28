package com.clashwars.cwevents.events.internal;

public enum EventStatus {
    NONE("&7Unknown"),
    OPEN("&aOpen"),
    STARTING("&2Starting"),
    STARTED("&2Started"),
    STOPPED("&4Stopped"),
    ENDED("&4Ended"),
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
