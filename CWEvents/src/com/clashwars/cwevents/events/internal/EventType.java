package com.clashwars.cwevents.events.internal;

import java.util.HashMap;
import java.util.Map;

import com.clashwars.cwevents.events.KOH;
import com.clashwars.cwevents.events.Race;
import com.clashwars.cwevents.events.Spleef;
import com.clashwars.cwevents.utils.Util;

public enum EventType {
	SPLEEF("Spleef", new Spleef()),
	RACE("Race", new Race()),
	KOH("KOH", new KOH());
	
	//Vars
	private static Map<String, EventType> types;
	private String name;
	private BaseEvent eventClass;
	
	//Constructor
	private EventType (String name, BaseEvent eventClass) {
		this.name = name;
		this.eventClass = eventClass;
	}
	
	//Get EventType by name
	public static EventType fromString(String name) {
        if (types == null) {
            initTypes();
        }
        name = name.toLowerCase();
        
        return types.get(Util.capitalize(name));
    }
	
	
	//Get name
	public String getName() {
        return name;
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