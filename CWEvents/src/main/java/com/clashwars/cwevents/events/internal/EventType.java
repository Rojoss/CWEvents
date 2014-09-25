package com.clashwars.cwevents.events.internal;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwevents.events.Bomberman;
import com.clashwars.cwevents.events.KOH;
import com.clashwars.cwevents.events.Race;
import com.clashwars.cwevents.events.Spleef;

import java.util.HashMap;
import java.util.Map;

public enum EventType {
    SPLEEF("Spleef", "spl", new Spleef()),
    RACE("Race", "rac", new Race()),
    KOH("Koh", "koh", new KOH()),
    BOMBERMAN("Bomberman", "bom", new Bomberman());

    //Vars
    private static Map<String, EventType> types;
    private String name;
    private BaseEvent eventClass;
    private String prefix;

    //Constructor
    private EventType(String name, String prefix, BaseEvent eventClass) {
        this.name = name;
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


    //Get name
    public String getName() {
        return name;
    }

    //Get prefix
    public String getPreifx() {
        return prefix;
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
