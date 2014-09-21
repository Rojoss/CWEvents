package com.clashwars.cwevents.events;

import com.clashwars.cwevents.events.internal.BaseEvent;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import com.clashwars.cwevents.utils.Util;
import com.clashwars.cwevents.utils.WGUtils;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class KOH extends BaseEvent {

    public KOH() {
        regionsNeeded.add("lobby");
        regionsNeeded.add("arena");
    }


    public void Reset() {
        super.Reset();
        WGUtils.setFlag(world, em.getRegionName("lobby"), DefaultFlag.EXIT, "deny");
        WGUtils.setFlag(world, em.getRegionName("arena"), DefaultFlag.PVP, "deny");
        WGUtils.setFlag(world, em.getRegionName("arena"), DefaultFlag.POTION_SPLASH, "deny");
    }

    public void Open() {
        super.Open();
    }

    public void Start() {
        super.Start();
    }

    public void Begin() {
        WGUtils.setFlag(world, em.getRegionName("lobby"), DefaultFlag.EXIT, "allow");
        WGUtils.setFlag(world, em.getRegionName("arena"), DefaultFlag.PVP, "allow");
        WGUtils.setFlag(world, em.getRegionName("arena"), DefaultFlag.POTION_SPLASH, "allow");
    }

    public void Stop() {
        super.Stop();
        WGUtils.setFlag(world, em.getRegionName("lobby"), DefaultFlag.EXIT, "deny");
        WGUtils.setFlag(world, em.getRegionName("arena"), DefaultFlag.PVP, "deny");
        WGUtils.setFlag(world, em.getRegionName("arena"), DefaultFlag.POTION_SPLASH, "deny");
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

    @EventHandler
    public void respawn(PlayerRespawnEvent event) {
        if (em.getEvent() != EventType.KOH) {
            return;
        }
        if (em.getStatus() != EventStatus.STARTED) {
            return;
        }
        if (em.getPlayers().contains(event.getPlayer().getName())) {
            em.broadcast(Util.formatMsg("&b&l" + event.getPlayer().getDisplayName() + " &3died and is out of the game!"));
            em.resetPlayer(event.getPlayer());
        }
    }
}
