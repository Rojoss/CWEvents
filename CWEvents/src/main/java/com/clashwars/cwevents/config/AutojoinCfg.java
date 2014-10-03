package com.clashwars.cwevents.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.cwevents.CWEvents;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AutojoinCfg extends EasyConfig {

    public List<UUID> autojoiners = new ArrayList<UUID>();

    public AutojoinCfg(String fileName) {
        this.setFile(fileName);
    }

    public void setAutoJoin(Player player, boolean auto) {
        setAutoJoin(player.getUniqueId(), auto);
    }

    public void setAutoJoin(UUID uuid, boolean auto) {
        if (autojoiners.contains(uuid)) {
            if (!auto) {
                autojoiners.remove(uuid);
                save();
            }
        } else {
            if (auto) {
                autojoiners.add(uuid);
                save();
            }
        }
    }

    public boolean getAutoJoin(Player player) {
        return getAutoJoin(player.getUniqueId());
    }

    public boolean getAutoJoin(UUID uuid) {
        return autojoiners.contains(uuid);
    }

    public List<UUID> getAutoJoiners() {
        return autojoiners;
    }
}
