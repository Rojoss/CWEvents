package com.clashwars.cwevents.event;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwevents.CWEvents;
import com.clashwars.cwevents.events.internal.EventManager;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import com.clashwars.cwevents.events.internal.SpectateData;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpectateEvents implements Listener {

    private CWEvents cwe;
    private EventManager em;
    private List<String> noTp = new ArrayList<String>();

    public SpectateEvents(CWEvents cwe) {
        this.cwe = cwe;
        em = cwe.getEM();
    }

    private boolean isSpectating(Player player) {
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


    //Make spectators follow players if following.
    @EventHandler
    public void move(PlayerMoveEvent event) {
        if (em.getPlayers() != null && em.getPlayers().containsKey(event.getPlayer().getName())) {
            int ID = em.getPlayers().get(event.getPlayer().getName());

            SpectateData data;
            for (String player : em.getSpectators().keySet()) {
                data = em.getSpectators().get(player);
                if (data.isFollowing() && ID == data.getPlayerIndex() && !noTp.contains(player)) {
                    cwe.getServer().getPlayer(player).teleport(event.getPlayer());
                }
            }
        }
    }



    //Make projectiles go through players.
    @EventHandler
    public void onProjectileHit(final EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Projectile && !(event.getDamager() instanceof ThrownPotion) && event.getEntity() instanceof Player && isSpectating((Player)event.getEntity())) {

            event.setCancelled(true);

            final Player hitPlayer = (Player)event.getEntity();
            final boolean wasFlying = hitPlayer.isFlying();
            final Location playerLoc = hitPlayer.getLocation();

            final Vector projVel = event.getDamager().getVelocity();
            final Location projLoc = event.getDamager().getLocation();

            hitPlayer.setFlying(true);
            noTp.add(hitPlayer.getName());
            hitPlayer.teleport(hitPlayer.getLocation().add(0,6,0));

            // Prevents the arrow from bouncing on the entity
            cwe.getServer().getScheduler().runTaskLater(cwe, new BukkitRunnable() {
                public void run() {
                    event.getDamager().teleport(projLoc);
                    event.getDamager().setVelocity(projVel);
                }
            }, 1L);

            // Teleport back the spectator
            cwe.getServer().getScheduler().runTaskLater(cwe, new BukkitRunnable() {
                public void run() {
                    hitPlayer.teleport(playerLoc);
                    noTp.remove(hitPlayer.getName());
                    hitPlayer.setFlying(wasFlying);
                }
            }, 5L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPotionSplash(final PotionSplashEvent event) {
        //Get all spectators hit by potion.
        final ArrayList<String> spectatorsAffected = new ArrayList<String>();
        for (LivingEntity le : event.getAffectedEntities()) {
            if (le instanceof Player && isSpectating((Player) le)) {
                spectatorsAffected.add(((Player) le).getName());
            }
        }

        if (!spectatorsAffected.isEmpty()) {
            //Check if there are spectators nearby if so they need to be tped up.
            Boolean tpNeeded = false;
            for(Entity entity : event.getEntity().getNearbyEntities(2, 2, 2)) {
                if (entity instanceof Player && isSpectating((Player)entity)) {
                    tpNeeded = true;
                }
            }

            // Teleport nearby spectators up and remove effect.
            final HashMap<String, Boolean> oldFlyMode = new HashMap<String, Boolean>();
            final HashMap<String, Location> oldLoc = new HashMap<String, Location>();
            for (String s : spectatorsAffected) {
                Player spectator = cwe.getServer().getPlayer(s);
                event.setIntensity(spectator, 0);

                if (tpNeeded) {
                    oldFlyMode.put(spectator.getName(), spectator.isFlying());
                    oldLoc.put(spectator.getName(), spectator.getLocation());
                    noTp.add(spectator.getName());
                    spectator.setFlying(true);
                    spectator.teleport(spectator.getLocation().add(0, 10, 0));
                }
            }

            if (tpNeeded) {
                final Location projLoc = event.getEntity().getLocation();
                final Vector projVel = event.getEntity().getVelocity();

                //Remove potion and recreate it a tick later to prevent splashing.
                cwe.getServer().getScheduler().runTaskLater(cwe, new BukkitRunnable() {
                    @Override
                    public void run() {
                        ThrownPotion clonedEntity = (ThrownPotion) event.getEntity().getWorld().spawnEntity(projLoc, event.getEntity().getType());

                        clonedEntity.setShooter(event.getEntity().getShooter());
                        clonedEntity.setTicksLived(event.getEntity().getTicksLived());
                        clonedEntity.setFallDistance(event.getEntity().getFallDistance());
                        clonedEntity.setBounce(event.getEntity().doesBounce());
                        clonedEntity.setItem(new CWItem(event.getEntity().getItem().getType(), 1, (byte)event.getEntity().getItem().getDurability()));
                        clonedEntity.setVelocity(projVel);

                        event.getEntity().remove();
                    }
                }, 1L);

                // Teleports back the spectators
                cwe.getServer().getScheduler().runTaskLater(cwe, new BukkitRunnable() {
                    @Override
                    public void run() {
                        for(String s : spectatorsAffected) {
                            Player spectator = cwe.getServer().getPlayer(s);

                            spectator.teleport(oldLoc.get(s));
                            spectator.setFlying(oldFlyMode.get(s));
                            noTp.remove(spectator.getName());
                        }
                    }
                }, 5L);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getProjectile() instanceof Arrow) {
                ((Arrow) event.getProjectile()).setBounce(false);
            }
        }
    }
}
