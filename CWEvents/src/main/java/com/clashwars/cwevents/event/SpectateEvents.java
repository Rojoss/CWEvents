package com.clashwars.cwevents.event;

import com.clashwars.cwevents.CWEvents;
import com.clashwars.cwevents.events.internal.EventManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.*;

public class SpectateEvents {

    private CWEvents cwe;
    private EventManager em;

    public SpectateEvents(CWEvents cwe) {
        this.cwe = cwe;
        em = cwe.getEM();
    }

    public boolean isSpectating(Player player) {
        if (em == null || em.getSpectators() == null || em.getSpectators().isEmpty() || player == null) {
            return false;
        }
        return em.getSpectators().containsKey(player.getName());
    }


    //BLOCKS
    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        if (isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    //VEHICLES
    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            if (isSpectating((Player)event.getEntered())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
     public void onVehicleExit(VehicleExitEvent event) {
        if (event.getExited() instanceof Player) {
            if (isSpectating((Player)event.getExited())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVehiclDamage(VehicleDamageEvent event) {
        if (event.getAttacker() instanceof Player) {
            if (isSpectating((Player)event.getAttacker())) {
                event.setCancelled(true);
            }
        }
    }

    //DAMAGE
    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (isSpectating((Player)event.getDamager())) {
                event.setCancelled(true);
            }
        }
        if (event.getEntity() instanceof Player) {
            if (isSpectating((Player)event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (isSpectating((Player)event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    //PLAYER
    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
        if (event.getRightClicked() instanceof Player) {
            if (isSpectating((Player)event.getRightClicked())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketUse(PlayerBucketEvent event) {
        if (isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent event) {
        if (isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    //INV
    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInvOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof  Player) {
            if (isSpectating((Player) event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    //TODO: Might need more checks.
}
