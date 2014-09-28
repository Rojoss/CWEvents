package com.clashwars.cwevents.events;

import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwevents.Util;
import com.clashwars.cwevents.events.internal.BaseEvent;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.events.internal.EventType;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.Random;

public class Spleef extends BaseEvent {

    Random random = new Random();

    public boolean checkSetup(EventType event, String arena, CommandSender sender) {
        String name = em.getRegionName(event, arena, "floor");
        if (CWWorldGuard.getRegion(world, name) == null) {
            sender.sendMessage(Util.formatMsg("&cInvalid arena name or region not set properly. &7Missing region &8'&4" + name + "&8'&7!"));
            return false;
        }
        return true;
    }

    public void Reset() {
        CWWorldGuard.regionReplace(world, CWWorldGuard.getRegion(world, em.getRegionName("floor")), BlockID.AIR, BlockID.SNOW_BLOCK);
        CWWorldGuard.setFlag(world, em.getRegionName("floor"), DefaultFlag.BUILD, "deny");
    }

    public void Open() {
        Reset();
        super.Open();
    }

    public void Close() {
        super.Close();
    }

    public void Start() {
        super.Start();
    }

    @SuppressWarnings("deprecation")
    public void Begin() {
        cwe.getServer().broadcastMessage("Spleef Begin");
        for (String p : em.getPlayers()) {
            cwe.getServer().getPlayer(p).getInventory().addItem(new ItemStack(Material.DIAMOND_SPADE, 1));
            cwe.getServer().getPlayer(p).updateInventory();
        }
        CWWorldGuard.setFlag(world, em.getRegionName("floor"), DefaultFlag.BUILD, "allow");
    }

    public void Stop() {
        super.Stop();
        CWWorldGuard.setFlag(world, em.getRegionName("floor"), DefaultFlag.BUILD, "deny");
    }

    public void onPlayerLeft(Player player) {
    }

    public void onPlayerJoin(Player player) {
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent event) {
        if (em.getEvent() != EventType.SPLEEF) {
            return;
        }
        Player player = (Player) event.getPlayer();
        if (!(em.getPlayers().contains(player.getName()))) {
            return;
        }
        Block block = event.getBlock();
        if (block.getType() != Material.SNOW_BLOCK) {
            event.setCancelled(true);
            return;
        }
        if (em.getStatus() != EventStatus.STARTED) {
            player.sendMessage(Util.formatMsg("&cThe game hasn't started yet!"));
            event.setCancelled(true);
            return;
        }
        //Make block falling.
        event.setCancelled(true);
        player.getWorld().spawnFallingBlock(block.getLocation().add(0, -0.2, 0), Material.SNOW_BLOCK, (byte) 0).setDropItem(false);
        block.setType(Material.AIR);
        //Give snowball
        float randomFloat = random.nextFloat();
        if (randomFloat <= 0.05f) {
            player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 1));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void blockLand(final EntityChangeBlockEvent event) {
        if (em.getEvent() != EventType.SPLEEF) {
            return;
        }
        if (!(event.getEntity() instanceof FallingBlock)) {
            return;
        }
        if (event.getBlock().getType() != Material.AIR) {
            return;
        }
        FallingBlock fallingBlock = (FallingBlock) event.getEntity();
        if (fallingBlock.getMaterial() != Material.SNOW_BLOCK) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getBlock().setType(Material.AIR);
            }
        }.runTaskLater(cwe, 1L);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void takeDamge(EntityDamageEvent event) {
        if (em.getEvent() != EventType.SPLEEF) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (!(em.getPlayers().contains(player.getName()))) {
            return;
        }
        if (em.getStatus() == EventStatus.STARTED) {
            if (event.getCause() == DamageCause.LAVA || (event.getCause() == DamageCause.FALL && event.getDamage() >= 2)) {
                em.broadcast(Util.formatMsg("&b&l" + player.getName() + " &3fell and is out!"));
                em.leaveEvent(player, true);
            }
        } else {
            player.teleport(em.getSpawn());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void snowballLand(ProjectileHitEvent event) {
        if (em.getEvent() != EventType.SPLEEF) {
            return;
        }
        if (event.getEntity().getType() != EntityType.SNOWBALL) {
            return;
        }
        Projectile proj = event.getEntity();

        BlockIterator bi = new BlockIterator(proj.getWorld(), proj.getLocation().toVector(), proj.getVelocity().normalize(), 0, 4);
        Block hit = null;
        while (bi.hasNext()) {
            hit = bi.next();
            if (hit.getType() == Material.SNOW_BLOCK) {
                break;
            }
        }
        breakBlock(hit, 1.0f);
        breakBlock(hit.getRelative(BlockFace.NORTH), 0.4f);
        breakBlock(hit.getRelative(BlockFace.EAST), 0.4f);
        breakBlock(hit.getRelative(BlockFace.SOUTH), 0.4f);
        breakBlock(hit.getRelative(BlockFace.WEST), 0.4f);
    }

    private void breakBlock(Block block, float chance) {
        if (block.getType() == Material.SNOW_BLOCK) {
            float randomFloat = random.nextFloat();
            if (randomFloat <= chance) {
                block.getWorld().spawnFallingBlock(block.getLocation().add(0, -0.2, 0), Material.SNOW_BLOCK, (byte) 0);
                block.setType(Material.AIR);
            }
        }
    }
}
