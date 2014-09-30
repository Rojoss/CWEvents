package com.clashwars.cwevents.event;

import com.clashwars.cwevents.CWEvents;
import com.clashwars.cwevents.Util;
import com.clashwars.cwevents.events.internal.EventManager;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import com.clashwars.cwevents.events.internal.SpectateData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class MainEvents implements Listener {
    private CWEvents cwe;
    private EventManager em;

    public MainEvents(CWEvents cwe) {
        this.cwe = cwe;
        em = cwe.getEM();
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        em.resetPlayer(event.getPlayer());
        event.getPlayer().getInventory().setItem(0, cwe.GetEventItem());
        event.getPlayer().getInventory().setItem(8, cwe.getLeaveItem());
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        em.leaveEvent(event.getPlayer(), true);
    }

    @EventHandler
    public void kick(PlayerKickEvent event) {
        em.leaveEvent(event.getPlayer(), true);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getItem() == null) {
            return;
        }
        if (item.getType() == Material.INK_SACK) {
            if (!em.getSpectators().containsKey(event.getPlayer().getName())) {
                if (em.getStatus() == EventStatus.STARTING || em.getStatus() == EventStatus.STARTED || em.getStatus() == EventStatus.ENDED) {
                    em.spectateEvent(event.getPlayer());
                } else {
                    em.joinEvent(event.getPlayer());
                }
            } else {
                if (em.getSpectators().get(event.getPlayer().getName()).isFollowing()) {
                    em.setFollowing(event.getPlayer(), false);
                } else {
                    em.setFollowing(event.getPlayer(), true);
                }
            }
        }
        if (item.getType() == Material.REDSTONE_BLOCK) {
            if (em.getSpectators().containsKey(event.getPlayer().getName())) {
                em.leaveEvent(event.getPlayer(), false);
            } else {
                cwe.joinPvP(event.getPlayer());
            }
        }
        if (item.getType() == Material.SKULL_ITEM) {
            if (em.getSpectators().containsKey(event.getPlayer().getName())) {
                SpectateData data = em.getSpectators().get(event.getPlayer().getName());
                if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (!em.getSpectators().get(event.getPlayer().getName()).isFollowing()) {
                        if (em.getPlayerByID(data.getPlayerIndex()) != null) {
                            event.getPlayer().sendMessage(Util.formatMsg("&6Teleported to &5" + em.getPlayerByID(data.getPlayerIndex())));
                            event.getPlayer().teleport(cwe.getServer().getPlayer(em.getPlayerByID(data.getPlayerIndex())));
                        } else {
                            event.getPlayer().sendMessage(Util.formatMsg("&cThis player is no longer in the game."));
                        }
                    } else {
                        event.getPlayer().sendMessage(Util.formatMsg("&cCan't teleport to players while following a player."));
                    }
                } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    boolean match = false;
                    int firstID = -1;
                    for (int ID : em.getPlayers().values()) {
                        if (firstID < 0) {
                            firstID = ID;
                        }
                        if (match) {
                            data.setPlayerIndex(ID);
                            em.updateSpectatorInv(event.getPlayer());
                            return;
                        }
                        if (ID == data.getPlayerIndex()) {
                            match = true;
                        }
                    }
                    if (firstID > 0) {
                        data.setPlayerIndex(firstID);
                    } else {
                        event.getPlayer().sendMessage(Util.formatMsg("&cNo other players to switch to."));
                    }
                    em.updateSpectatorInv(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void invClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        if (event.getWhoClicked().isOp()) {
            return;
        }
        if (event.getCurrentItem().getType() == Material.INK_SACK || event.getCurrentItem().getType() == Material.REDSTONE_BLOCK || em.getSpectators().containsKey(event.getWhoClicked().getName())) {
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
            event.getPlayer().getInventory().setItem(0, cwe.GetEventItem());
        }
        if (event.getItemDrop().getItemStack().getType() == Material.REDSTONE_BLOCK) {
            event.getItemDrop().remove();
            event.getPlayer().getInventory().setItem(8, cwe.getLeaveItem());
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        //Prevent movement at start.
        if (em.getEvent() != EventType.KOH && em.getEvent() != EventType.RACE) {
            return;
        }
        if (em.getPlayers() == null || !em.getPlayers().containsKey(event.getPlayer().getName())) {
            return;
        }
        if (em.getStatus() == EventStatus.STARTING || em.getStatus() == EventStatus.OPEN) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()) {
                event.setCancelled(true);
                event.getPlayer().teleport(event.getFrom());
            }
        }
    }

}
