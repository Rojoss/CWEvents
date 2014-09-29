package com.clashwars.cwevents.events.internal;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwevents.events.Bomberman;
import com.clashwars.cwevents.events.KOH;
import com.clashwars.cwevents.events.Race;
import com.clashwars.cwevents.events.Spleef;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public enum EventType {
    SPLEEF("Spleef", "spl", ChatColor.AQUA, new Spleef()),
    RACE("Race", "rac", ChatColor.GREEN, new Race()),
    KOH("Koh", "koh", ChatColor.GOLD, new KOH()),
    BOMBERMAN("Bomberman", "bom", ChatColor.RED, new Bomberman());

    //Vars
    private static Map<String, EventType> types;
    private String name;
    private BaseEvent eventClass;
    private String prefix;
    private ChatColor color;

    //Constructor
    private EventType(String name, String prefix, ChatColor color, BaseEvent eventClass) {
        this.name = name;
        this.color = color;
        this.eventClass = eventClass;
        this.prefix = prefix;
    }

    //Get EventType by name
    public static EventType fromString(String name) {
        if (types == null) {
            initTypes();
        }
        name = name.toLowerCase();

        return types.get(CWUtil.capitalize(name));
    }

    //Get EventType by prefix
    public static EventType fromPrefix(String prefix) {
        for (EventType type : values()) {
            if (type.getPreifx().equalsIgnoreCase(prefix)) {
                return type;
            }
        }
        return null;
    }


    //Get name
    public String getName() {
        return name;
    }

    //Get prefix
    public String getPreifx() {
        return prefix;
    }

    //Get color
    public ChatColor getColor() {
        return color;
    }

    //Get class
    public BaseEvent getEventClass() {
        return eventClass;
    }


    //Fill hashmap with types.
    private static void initTypes() {
        types = new HashMap<String, EventType>();
        for (EventType c : values()) {
            types.put(c.name, c);
        }
    }
}
