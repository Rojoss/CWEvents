package com.clashwars.cwevents.events;

import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwevents.Util;
import com.clashwars.cwevents.events.internal.BaseEvent;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import com.clashwars.cwevents.runnables.KohRunnable;
import com.sk89q.worldguard.internal.event.RegionEnterEvent;
import com.sk89q.worldguard.internal.event.RegionLeaveEvent;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import java.lang.IllegalStateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KOH extends BaseEvent {

    private List<String> capturingPlayers = new ArrayList<String>();
    private KohRunnable kohRunnable;
    private BukkitTask task;
    private Map<String, Integer> lives = new HashMap<String, Integer>();

    public boolean checkSetup(EventType event, String arena, CommandSender sender) {
        String name = em.getRegionName(event, arena, "hill");
        if (CWWorldGuard.getRegion(world, name) == null) {
            sender.sendMessage(Util.formatMsg("&cInvalid arena name or region not set properly. &7Missing region &8'&4" + name + "&8'&7!"));
            return false;
        }
        return true;
    }


    public void Reset() {
        super.Reset();
        lives.clear();
        kohRunnable = null;
        capturingPlayers.clear();
        CWWorldGuard.setFlag(world, "koh_area", DefaultFlag.PVP, "deny");
        CWWorldGuard.setFlag(world, "koh_area", DefaultFlag.POTION_SPLASH, "deny");
    }

    public void Open() {
        Reset();
        super.Open();
    }

    public void Start() {
        super.Start();
    }

    public void Begin() {
        kohRunnable = new KohRunnable(this);
        kohRunnable.runTaskTimer(cwe, 0 , 20);

        for (String p : em.getPlayers().keySet()) {
            cwe.getStats().getLocalStats(p).incKohGamesPlayed(1);
        }



        cwe.getServer().getScheduler().scheduleSyncDelayedTask(cwe, new Runnable() {
            public void run() {
                em.broadcast(Util.formatMsg("&6You can now &4&lPvP&6!"));
                CWWorldGuard.setFlag(world, "koh_area", DefaultFlag.PVP, "allow");
                CWWorldGuard.setFlag(world, "koh_area", DefaultFlag.POTION_SPLASH, "allow");
            }
        }, 100L);
    }

    public void Stop() {
        super.Stop();
        if (kohRunnable != null) {
            kohRunnable.cancel();
        }
        kohRunnable = null;
        CWWorldGuard.setFlag(world, "koh_area", DefaultFlag.PVP, "deny");
        CWWorldGuard.setFlag(world, "koh_area", DefaultFlag.POTION_SPLASH, "deny");
    }

    public void onPlayerLeft(Player player) {
        if (lives.containsKey(player.getName())) {
            lives.remove(player.getName());
        }
    }

    public void onPlayerJoin(Player player) {
        equip(player);
        lives.put(player.getName(), 3);
    }

    public void capture(Player capturer) {
        em.broadcast(Util.formatMsg("&a&l" + capturer.getName() + " &6&lis the king of the hill!"));
        cwe.getStats().getLocalStats(capturer).incKohWins(1);
        em.stopGame(capturer);
    }

    private void equip(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.getInventory().clear();
        ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
        item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        player.getInventory().setHelmet(item);

        item = new ItemStack(Material.DIAMOND_CHESTPLATE);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
        item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        player.getInventory().setChestplate(item);

        item = new ItemStack(Material.DIAMOND_LEGGINGS);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
        item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        player.getInventory().setLeggings(item);

        item = new ItemStack(Material.DIAMOND_BOOTS);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
        item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        item.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 3);
        player.getInventory().setBoots(item);

        item = new ItemStack(Material.DIAMOND_SWORD);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
        player.getInventory().addItem(item);

        item = new ItemStack(Material.BOW);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        player.getInventory().addItem(item);

        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 8));
        player.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 16418)); /* speed */
        player.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 16449)); /* regen */
        player.getInventory().addItem(new ItemStack(Material.POTION, 3, (short) 16396)); /* harming */
        player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        player.updateInventory();
    }

    @EventHandler
    public void respawn(final PlayerRespawnEvent event) {
        if (em.getEvent() != EventType.KOH) {
            return;
        }
        if (em.getStatus() != EventStatus.STARTED && em.getStatus() != EventStatus.ENDED) {
            return;
        }
        if (!em.getPlayers().containsKey(event.getPlayer().getName())) {
            return;
        }
        final Player player = event.getPlayer();
        cwe.getStats().getLocalStats(player).incKohDeaths(1);
        lives.put(player.getName(), lives.get(player.getName()) - 1);
        if (lives.get(player.getName()) <= 0) {
            //No more lives remove player.
            em.broadcast(Util.formatMsg("&b&l" + player.getName() + " &3died and is out of the game!"));
            player.sendMessage(Util.formatMsg("&cYou have no more lives!"));
            em.spectateEvent(player);

            //If one player remaining end the game.
            if (em.getPlayers().size() == 1) {
                final Player winner = cwe.getServer().getPlayer(em.getPlayers().keySet().iterator().next());
                cwe.getStats().getLocalStats(winner).incKohWins(1);
                em.broadcast(Util.formatMsg("&a&l" + winner.getName() + " &6is the last player alive and wins!"));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        em.stopGame(winner);
                    }
                }.runTaskLater(cwe, 30L);
            }
        } else {
            if (lives.get(player.getName()) == 1) {
                em.broadcast(Util.formatMsg("&b&l" + player.getName() + " &3died! &8[&4❤&8]"));
                player.sendMessage(Util.formatMsg("&cThis is your last life!!! &4Be careful!"));
            } else {
                String hearts = "";
                for (int i = 0; i < lives.get(player.getName()); i++) {
                    hearts += "❤";
                }
                em.broadcast(Util.formatMsg("&b&l" + player.getName() + " &3died! &8[&4" + hearts + "&8]"));
                player.sendMessage(Util.formatMsg("&cYou have &4" + lives.get(player.getName()) + " &clives remaining."));
            }
            cwe.getServer().getScheduler().scheduleSyncDelayedTask(cwe, new Runnable() {
                public void run() {
                    em.teleportToArena(player, false);
                    equip(player);
                }
            }, 20L);
        }
    }

    @EventHandler
    public void kill(PlayerDeathEvent event) {
        if (event.getEntity() == null) {
            return;
        }
        Player player = event.getEntity();
        //Kill stat
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            if (!em.getPlayers().containsKey(killer.getName())) {
                return;
            }
            cwe.getStats().getLocalStats(killer).incKohKills(1);
        }
        //Remove player from hill if died on hill.
        if (capturingPlayers.contains(player.getName())) {
            capturingPlayers.remove(player.getName());
            if (capturingPlayers.size() == 1) {
                //Only 1 player remaining on the hill.
                kohRunnable.startCapture(cwe.getServer().getPlayer(capturingPlayers.get(0)));
            } else if (capturingPlayers.size() == 0) {
                //Nobody left on the hill.
                kohRunnable.stopCapture();
            }
        }
    }

    @EventHandler
    public void regionEnter(RegionEnterEvent event) {
        if (em.getEvent() != EventType.KOH) {
            return;
        }
        if (em.getStatus() != EventStatus.STARTED) {
            return;
        }
        if (em.getPlayers().containsKey(event.getPlayer().getName()) == false) {
            return;
        }
        Player player = event.getPlayer();
        for (ProtectedRegion region : event.getRegions()) {
            if (region.getId().equalsIgnoreCase(em.getRegionName("hill"))) {
                if (!capturingPlayers.contains(player.getName())) {
                    capturingPlayers.add(player.getName());
                    if (capturingPlayers.size() == 1) {
                        //One player on the hill.
                        kohRunnable.startCapture(player);
                    } else {
                        //Multiple people on the hill.
                        kohRunnable.stopCapture();
                    }
                }
                return;
            }
        }
    }

    @EventHandler
    public void regionLeave(RegionLeaveEvent event) {
        if (em.getEvent() != EventType.KOH) {
            return;
        }
        if (em.getStatus() != EventStatus.STARTED) {
            return;
        }
        if (!em.getPlayers().containsKey(event.getPlayer().getName())) {
            return;
        }
        Player player = event.getPlayer();
        for (ProtectedRegion region : event.getRegions()) {
            if (region.getId().equalsIgnoreCase(em.getRegionName("hill"))) {
                if (capturingPlayers.contains(player.getName())) {
                    capturingPlayers.remove(player.getName());
                    if (capturingPlayers.size() == 1) {
                        //Only 1 player remaining on the hill.
                        kohRunnable.startCapture(cwe.getServer().getPlayer(capturingPlayers.get(0)));
                    } else if (capturingPlayers.size() == 0) {
                        //Nobody left on the hill.
                        kohRunnable.stopCapture();
                    }
                }
                return;
            }
        }
    }
}
