package com.clashwars.cwevents.events;

import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwevents.events.internal.BaseEvent;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import com.clashwars.cwevents.runnables.KohRunnable;
import com.sk89q.worldguard.bukkit.event.RegionEnterEvent;
import com.sk89q.worldguard.bukkit.event.RegionLeaveEvent;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KOH extends BaseEvent {

    private List<String> capturingPlayers = new ArrayList<String>();
    private KohRunnable kohRunnable;

    public KOH() {
        regionsNeeded.add("lobby");
        regionsNeeded.add("arena");
        regionsNeeded.add("hill");
        kohRunnable = new KohRunnable(this);
    }


    public void Reset() {
        super.Reset();
        kohRunnable.cancel();
        CWWorldGuard.setFlag(world, em.getRegionName("lobby"), DefaultFlag.EXIT, "deny");
        CWWorldGuard.setFlag(world, em.getRegionName("arena"), DefaultFlag.PVP, "deny");
        CWWorldGuard.setFlag(world, em.getRegionName("arena"), DefaultFlag.POTION_SPLASH, "deny");
    }

    public void Open() {
        super.Open();
    }

    public void Start() {
        super.Start();
    }

    public void Begin() {
        CWWorldGuard.setFlag(world, em.getRegionName("lobby"), DefaultFlag.EXIT, "allow");
        CWWorldGuard.setFlag(world, em.getRegionName("arena"), DefaultFlag.PVP, "allow");
        CWWorldGuard.setFlag(world, em.getRegionName("arena"), DefaultFlag.POTION_SPLASH, "allow");
        kohRunnable.runTaskTimer(cwe, 20, 20);
    }

    public void Stop() {
        super.Stop();
        kohRunnable.cancel();
        CWWorldGuard.setFlag(world, em.getRegionName("lobby"), DefaultFlag.EXIT, "deny");
        CWWorldGuard.setFlag(world, em.getRegionName("arena"), DefaultFlag.PVP, "deny");
        CWWorldGuard.setFlag(world, em.getRegionName("arena"), DefaultFlag.POTION_SPLASH, "deny");
    }

    public void onPlayerLeft(Player player) {

    }

    public void onPlayerJoin(Player player) {
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
    }

    public void capture(Player capturer) {
        em.broadcast(CWUtil.formatMsg("&a&l" + capturer.getName() + " &6&lis the king of the hill!"));
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent event) {
        if (em.getEvent() != EventType.KOH) {
            return;
        }
        if (em.getStatus() != EventStatus.STARTED) {
            return;
        }
        if (em.getPlayers().contains(event.getPlayer().getName())) {
            em.broadcast(CWUtil.formatMsg("&b&l" + event.getPlayer().getDisplayName() + " &3died and is out of the game!"));
            em.resetPlayer(event.getPlayer());
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
        Player player = event.getPlayer();
        for (ProtectedRegion region : event.getRegions()) {
            if (region.getId().equalsIgnoreCase(em.getRegionName("hill"))) {
                if (capturingPlayers.contains(player.getName())) {
                    capturingPlayers.remove(player.getName());
                    if (capturingPlayers.size() == 1) {
                        //Only 1 player remaining on the hill.
                        kohRunnable.startCapture(player);
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
