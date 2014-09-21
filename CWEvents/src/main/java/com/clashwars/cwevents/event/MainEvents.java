package com.clashwars.cwevents.event;

import com.clashwars.cwevents.CWEvents;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class MainEvents implements Listener {
    private CWEvents cwe;

    public MainEvents(CWEvents cwe) {
        this.cwe = cwe;
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        cwe.getEM().resetPlayer(event.getPlayer());
        event.getPlayer().getInventory().setItem(8, cwe.GetEventItem());
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        cwe.getEM().leaveEvent(event.getPlayer(), true);
    }

    @EventHandler
    public void kick(PlayerKickEvent event) {
        cwe.getEM().leaveEvent(event.getPlayer(), true);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getItem() == null) {
            return;
        }
        if (item.getType() == Material.INK_SACK) {
            cwe.getEM().joinEvent(event.getPlayer());
        }
    }

    @EventHandler
    public void invClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        if (event.getCurrentItem().getType() == Material.INK_SACK) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void itemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop() == null || event.getItemDrop().getItemStack() == null) {
            return;
        }
        if (event.getItemDrop().getItemStack().getType() == Material.INK_SACK) {
            event.getItemDrop().remove();
            event.getPlayer().getInventory().setItem(8, cwe.GetEventItem());
        }
    }
}
