package com.clashwars.cwevents.stats;

import com.clashwars.cwevents.CWEvents;
import com.google.gson.Gson;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class StatsManager {

    private CWEvents cwe;
    private Map<UUID, Stats> localStats = new HashMap<UUID, Stats>();
    private Map<UUID, Stats> allStats = new HashMap<UUID, Stats>();
    Gson gson;

    public StatsManager(CWEvents cwe) {
        this.cwe = cwe;
        gson = new Gson();
    }

    //Merge the local stats with the databse stats.
    public void syncAllStats() {
        Map<UUID, Stats> mergedStats = new HashMap<UUID, Stats>();

        //Get all stats from sql and fill this map.
        Map<UUID, Stats> sqlStats = new HashMap<UUID, Stats>();
        try {
            Statement statement = cwe.getSql().createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM Stats;");

            while (res.next()) {
                UUID uuid = UUID.fromString(res.getString("UUID"));
                if (!sqlStats.containsKey(uuid)) {
                    Stats statsSql = gson.fromJson(res.getString("Stats"), Stats.class);
                    sqlStats.put(uuid, statsSql);
                }
            }
        } catch (SQLException e) {
            cwe.log("Can't connect to database.");
            e.printStackTrace();
            return;
        }

        //Get all users from sql and local together.
        Set<UUID> uuids = new HashSet<UUID>();
        uuids.addAll(sqlStats.keySet());
        uuids.addAll(localStats.keySet());

        //Go through all users and merge the stats.
        //Also keep track of all changes in sql so we don't have to update all users when not needed.
        List<UUID> sqlUsersChanged = new ArrayList<UUID>();
        List<UUID> newSqlUsers = new ArrayList<UUID>();
        for (UUID uuid : uuids) {
            //Stats online and local. Merge together.
            if (sqlStats.containsKey(uuid) && localStats.containsKey(uuid)) {
                Stats tempSql = sqlStats.get(uuid);
                Stats tempLocal = localStats.get(uuid);

                Stats tempMerged = new Stats();
                tempMerged.setKohGamesPlayed(tempSql.getKohGamesPlayed() + tempLocal.getKohGamesPlayed());
                tempMerged.setKohWins(tempSql.getKohWins() + tempLocal.getKohWins());
                tempMerged.setKohKills(tempSql.getKohKills() + tempLocal.getKohKills());
                tempMerged.setKohDeaths(tempSql.getKohDeaths() +  tempLocal.getKohDeaths());

                tempMerged.setRaceGamesPlayed(tempSql.getRaceGamesPlayed() + tempLocal.getRaceGamesPlayed());
                tempMerged.setRaceWins(tempSql.getRaceWins() + tempLocal.getRaceWins());
                tempMerged.setRaceLassoUses(tempSql.getRaceLassoUses() + tempLocal.getRaceLassoUses());
                tempMerged.setRaceDeaths(tempSql.getRaceDeaths() + tempLocal.getRaceDeaths());

                tempMerged.setSpleefGamesPlayed(tempSql.getSpleefGamesPlayed() + tempLocal.getSpleefGamesPlayed());
                tempMerged.setSpleefWins(tempSql.getSpleefWins() + tempLocal.getSpleefWins());
                tempMerged.setSpleefBlocks(tempSql.getSpleefBlocks() + tempLocal.getSpleefBlocks());
                tempMerged.setSpleefSnowballsFarmed(tempSql.getSpleefSnowballsFarmed() + tempLocal.getSpleefSnowballsFarmed());

                tempMerged.setBombermanGamesPlayed(tempSql.getBombermanGamesPlayed() + tempLocal.getBombermanGamesPlayed());
                tempMerged.setBombermanWins(tempSql.getBombermanWins() + tempLocal.getBombermanWins());
                tempMerged.setBombermanKills(tempSql.getBombermanKills() + tempLocal.getBombermanKills());
                tempMerged.setBombermanDeaths(tempSql.getBombermanDeaths() + tempLocal.getBombermanDeaths());
                tempMerged.setBombermanBombsPlaced(tempSql.getBombermanBombsPlaced() + tempLocal.getBombermanBombsPlaced());
                tempMerged.setBombermanPowerups(tempSql.getBombermanPowerups() + tempLocal.getBombermanPowerups());

                sqlUsersChanged.add(uuid);
                mergedStats.put(uuid, tempMerged);
                continue;
            }
            //Stats online only. Put them in local/merged stats.
            if (sqlStats.containsKey(uuid)) {
                mergedStats.put(uuid, sqlStats.get(uuid));
            }
            //Stats local only. Put them in online stats.
            if (localStats.containsKey(uuid)) {
                newSqlUsers.add(uuid);
                mergedStats.put(uuid, localStats.get(uuid));
            }
        }
        //Update local stats with the merged stats.
        allStats = mergedStats;
        localStats.clear();

        //Insert new users to database.
        for (UUID uuid : newSqlUsers) {
            try {
                Statement statement = cwe.getSql().createStatement();
                statement.executeUpdate("INSERT INTO Stats (UUID, Stats) VALUES ('" + uuid.toString() + "', '" + gson.toJson(mergedStats.get(uuid)) + "');");
            } catch (SQLException e) {
                cwe.log("Can't connect to database. User " + uuid + " not inserted.");
                e.printStackTrace();
            }
        }

        //Update sql stats with merged stats.
        for (UUID uuid : sqlUsersChanged) {
            try {
                Statement statement = cwe.getSql().createStatement();
                if (statement.executeUpdate("UPDATE Stats SET Stats='" + gson.toJson(mergedStats.get(uuid)) + "' WHERE UUID='" + uuid.toString() + "';") < 1) {
                    cwe.log("Failed at updating stats for user " + uuid + ".");
                }
            } catch (SQLException e) {
                cwe.log("Can't connect to database. User " + uuid + " not updated.");
                e.printStackTrace();
                return;
            }
        }
    }

    //Get local stats from player.
    //Creates new stats if nothing was found.
    //It only creates local stats so if the player is already in the database the stats will be merged.
    public Stats getLocalStats(String player) {
        return getLocalStats(cwe.getServer().getPlayer(player).getUniqueId());
    }

    public Stats getLocalStats(Player player) {
        return getLocalStats(player.getUniqueId());
    }

    public Stats getLocalStats(UUID uuid) {
        if (!localStats.containsKey(uuid)) {
            localStats.put(uuid, new Stats());
        }
        return localStats.get(uuid);
    }

    //Get merged stats from player.
    //Returns null if not found.
    //These stats should only be used to read values as modifying them wont modify them in the database.
    public Stats geStats(String player) {
        return getLocalStats(cwe.getServer().getPlayer(player).getUniqueId());
    }

    public Stats getStats(OfflinePlayer player) {
        return getLocalStats(player.getUniqueId());
    }

    public Stats getStats(UUID uuid) {
        if (!allStats.containsKey(uuid)) {
            allStats.put(uuid, new Stats());
        }
        return allStats.get(uuid);
    }
}
