package com.clashwars.cwevents.events;

import com.clashwars.cwcore.CWCore;
import com.clashwars.cwcore.CooldownManager;
import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwevents.events.internal.BaseEvent;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import com.sk89q.worldguard.bukkit.event.RegionEnterEvent;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;

public class Race extends BaseEvent {

    int playersFinished = 0;
    CooldownManager cdm;

    public Race() {
        regionsNeeded.add("lobby");
        regionsNeeded.add("finish");
        cdm = CWCore.inst().getCDM();
    }

    public void Reset() {
        super.Reset();
        CWWorldGuard.setFlag(world, em.getRegionName("lobby"), DefaultFlag.EXIT, "deny");
    }

    public void Open() {
        super.Open();
    }

    public void Start() {
        super.Start();
    }

    public void Begin() {
        CWWorldGuard.setFlag(world, em.getRegionName("lobby"), DefaultFlag.EXIT, "allow");
        playersFinished = 0;
    }

    public void Stop() {
        super.Stop();
        CWWorldGuard.setFlag(world, em.getRegionName("lobby"), DefaultFlag.EXIT, "deny");
    }

    public void onPlayerLeft(Player player) {

    }

    public void onPlayerJoin(Player player) {
        player.getInventory().addItem(new CWItem(Material.LEASH, 1, (short) 0, "&6&lLasso", new String[]{"&7Use this on other players.", "&7It will pull them towards you!"}));
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 8));

        //Random colored boots.
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
        ItemMeta meta = boots.getItemMeta();
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
        armorMeta.setColor(CWUtil.getRandomColor());
        boots.setItemMeta(armorMeta);
        player.getInventory().setBoots(boots);
    }

    @EventHandler
    public void lassoUse(PlayerInteractEntityEvent event) {
        if (em.getEvent() != EventType.RACE) {
            return;
        }
        if (em.getStatus() != EventStatus.STARTED) {
            return;
        }
        if (!em.getPlayers().contains(event.getPlayer().getName())) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getItemInHand().getType() != Material.LEASH || event.getRightClicked() == null || (event.getRightClicked() instanceof Player)) {
            return;
        }
        if (cdm.getCooldown(player.getName() + "-lasso").onCooldown()) {
            player.sendMessage(CWUtil.formatMsg("&cLasso is on cooldown."));
            return;
        }
        cdm.createCooldown(player.getName() + "-lasso", 3000);
        if (CWUtil.random(0,4) != 1) {
            player.sendMessage(CWUtil.formatMsg("&cMiss!"));
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0f, 2.0f);
            return;
        }
        Player target = (Player)event.getRightClicked();
        Vector dir = player.getLocation().getDirection();
        target.setVelocity(new Vector(dir.getX(), 0.3f, dir.getZ()));
        world.playSound(target.getLocation(), Sound.BAT_TAKEOFF, 1.0f, 1.8f);

    }

    @EventHandler
    public void respawn(PlayerRespawnEvent event) {
        if (em.getEvent() != EventType.RACE) {
            return;
        }
        if (em.getStatus() != EventStatus.STARTED) {
            return;
        }
        if (!em.getPlayers().contains(event.getPlayer().getName())) {
            return;
        }
        if (em.getPlayers().contains(event.getPlayer().getName())) {
            em.broadcast(CWUtil.formatMsg("&b&l" + event.getPlayer().getDisplayName() + " &3died and has to start over again!"));
            event.getPlayer().teleport(em.getSpawn());
        }
    }

    @EventHandler
    public void regionEnter(RegionEnterEvent event) {
        if (em.getEvent() != EventType.RACE) {
            return;
        }
        if (em.getStatus() != EventStatus.STARTED) {
            return;
        }
        if (!em.getPlayers().contains(event.getPlayer().getName())) {
            return;
        }
        Player player = event.getPlayer();
        for (ProtectedRegion region : event.getRegions()) {
            if (region.getId().equalsIgnoreCase(em.getRegionName("finish"))) {
                playersFinished++;
                if (playersFinished == 1) {
                    em.broadcast(CWUtil.formatMsg("&a&l" + player.getName() + " &6wins the race!"));
                    em.playSound(Sound.ORB_PICKUP, 0.8f, 0f);
                } else {
                    em.broadcast(CWUtil.formatMsg("&5" + player.getName() + " &6finished on place &5" + playersFinished + "&6."));
                    em.playSound(Sound.ORB_PICKUP, 0.4f, 2f);
                }
                return;
            }
        }
    }
}