package com.clashwars.cwevents.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.cwevents.CWEvents;
import org.bukkit.Location;

import java.util.HashMap;

public class LocCfg extends EasyConfig {

    public HashMap<String, Location> locations = new HashMap<String, Location>();

    public LocCfg(String fileName) {
        this.setFile(fileName);
    }

    public void setLocation(String name, Location loc) {
        setLocation(name, loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public void setLocation(String name, String world, double xPos, double yPos, double zPos, float yaw, float pitch) {
        Location newLoc = locations.get(name);
        if (newLoc == null) {
            newLoc = new Location(CWEvents.inst().getServer().getWorld(world), xPos, yPos, zPos, yaw, pitch);
        } else {
            newLoc.setWorld(CWEvents.inst().getServer().getWorld(world));
            newLoc.setX(xPos);
            newLoc.setY(yPos);
            newLoc.setZ(zPos);
            newLoc.setYaw(yaw);
            newLoc.setPitch(pitch);
        }
        locations.put(name, newLoc);
        save();
    }

    public boolean removeLoction(String name) {
        if (!locations.containsKey(name)) {
            return false;
        }
        locations.remove(name);
        save();
        return true;
    }

    //Get location by name with ignored casing.
    public Location getLoc(String name) {
        return locations.get(name);
    }

    //Get a name with proper casing from name with any casing.
    public String getName(String name) {
        for (String n : locations.keySet()) {
            if (n.equalsIgnoreCase(name)) {
                return n;
            }
        }
        return "";
    }

    public HashMap<String, Location> getLocations() {
        return locations;
    }
}
