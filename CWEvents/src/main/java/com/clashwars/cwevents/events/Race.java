package com.clashwars.cwevents.events;

import com.clashwars.cwcore.CWCore;
import com.clashwars.cwcore.CooldownManager;
import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwevents.Util;
import com.clashwars.cwevents.events.internal.BaseEvent;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import com.sk89q.worldguard.internal.event.RegionEnterEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Race extends BaseEvent {

    private List<String> finished = new ArrayList<String>();
    private CooldownManager cdm;



    public Race() {
        cdm = CWCore.inst().getCDM();
    }

    public boolean checkSetup(EventType event, String arena, CommandSender sender) {
        String name = em.getRegionName(event, arena, "finish");
        if (CWWorldGuard.getRegion(world, name) == null) {
            sender.sendMessage(Util.formatMsg("&cInvalid arena name or region not set properly. &7Missing region &8'&4" + name + "&8'&7!"));
            return false;
        }
        return true;
    }

    public void Reset() {
        super.Reset();
    }

    public void Open() {
        Reset();
        super.Open();

    }

    public void Start() {
        super.Start();
    }

    public void Begin() {
        finished.clear();
    }

    public void Stop() {
        super.Stop();
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
        player.updateInventory();
    }

    @EventHandler
    public void lassoUse(PlayerInteractEntityEvent event) {
        if (em.getEvent() != EventType.RACE) {
            return;
        }
        if (em.getStatus() != EventStatus.STARTED) {
            return;
        }
        if (!em.getPlayers().containsKey(event.getPlayer().getName())) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getItemInHand().getType() != Material.LEASH || event.getRightClicked() == null || !(event.getRightClicked() instanceof Player)) {
            return;
        }
        CooldownManager.Cooldown cd = cdm.getCooldown(player.getName() + "-lasso");
        if (cd != null && cd.onCooldown()) {
            player.sendMessage(Util.formatMsg("&cLasso is on cooldown."));
            return;
        }
        cdm.createCooldown(player.getName() + "-lasso", 3000);
        if (CWUtil.randomFloat() < 0.5f) {
            player.sendMessage(Util.formatMsg("&cMiss!"));
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0f, 2.0f);
            return;
        }
        Player target = (Player) event.getRightClicked();
        Vector dir = player.getLocation().getDirection();
        dir.multiply(-1);
        target.setVelocity(new Vector(dir.getX() * 1.4, 0.35f, dir.getZ() * 1.4));
        world.playSound(target.getLocation(), Sound.BAT_TAKEOFF, 1.0f, 1.5f);
        ParticleEffect.CRIT.display(target.getLocation(), 0.5f, 1.0f, 0.5f, 0.001f, 50);
    }

    @EventHandler
    public void respawn(final PlayerRespawnEvent event) {
        if (em.getEvent() != EventType.RACE) {
            return;
        }
        if (em.getStatus() != EventStatus.STARTED) {
            return;
        }
        if (!em.getPlayers().containsKey(event.getPlayer().getName())) {
            return;
        }
        cwe.getServer().getScheduler().scheduleSyncDelayedTask(cwe, new Runnable() {
            public void run() {
                em.broadcast(Util.formatMsg("&b&l" + event.getPlayer().getDisplayName() + " &3died and has to start over again!"));
                em.teleportToArena(event.getPlayer(), false);
            }
        }, 20L);
    }

    @EventHandler
    public void regionEnter(RegionEnterEvent event) {
        if (em.getEvent() != EventType.RACE) {
            return;
        }
        if (em.getStatus() != EventStatus.STARTED) {
            return;
        }
        Player player = event.getPlayer();
        if (!em.getPlayers().containsKey(player.getName())) {
            return;
        }
        if (finished.contains(player.getName())) {
            return;
        }
        for (ProtectedRegion region : event.getRegions()) {
            if (region.getId().equalsIgnoreCase(em.getRegionName("finish"))) {
                finished.add(player.getName());
                if (finished.size() == 1) {
                    em.broadcast(Util.formatMsg("&a&l" + player.getName() + " &6wins the race!"));
                    em.playSound(Sound.ORB_PICKUP, 0.8f, 0f);
                    em.spectateEvent(player);
                } else {
                    em.broadcast(Util.formatMsg("&5" + player.getName() + " &6finished on place &5" + finished.size() + "&6."));
                    em.playSound(Sound.ORB_PICKUP, 0.4f, 2f);
                    em.spectateEvent(player);
                }
                return;
            }
        }
    }
}