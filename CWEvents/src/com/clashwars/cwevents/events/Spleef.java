package com.clashwars.cwevents.events;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

import com.clashwars.cwevents.events.internal.BaseEvent;
import com.clashwars.cwevents.events.internal.EventStatus;
import com.clashwars.cwevents.utils.Util;
import com.clashwars.cwevents.utils.WGUtils;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class Spleef extends BaseEvent {
	
	Random random = new Random();
	
	public void Reset() {
		WGUtils.regionFill(world, WGUtils.getRegion(world, em.getRegionName("floor")), BlockID.SNOW_BLOCK);
		WGUtils.setFlag(world, em.getRegionName("floor"), DefaultFlag.BUILD, "deny");
	}
	
	public void Open() {
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
		}
		WGUtils.setFlag(world, em.getRegionName("floor"), DefaultFlag.BUILD, "allow");
	}
	
	public void Stop() {
		super.Stop();
		WGUtils.setFlag(world, em.getRegionName("floor"), DefaultFlag.BUILD, "deny");
	}

	public void onPlayerLeft(Player player) {
	}

	public void onPlayerJoin(Player player) {
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (block.getType() == Material.SNOW_BLOCK) {
			//Make block falling.
			event.setCancelled(true);
			player.getWorld().spawnFallingBlock(block.getLocation(), Material.SNOW_BLOCK, (byte) 0).setDropItem(false);
			block.setType(Material.AIR);
			//Give snowball
			float randomFloat = random.nextFloat();
			if (randomFloat <= 0.05f) {
				player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 1));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void blockLand(EntityChangeBlockEvent event) {
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
		}.runTaskLater(cwe.getPlugin(), 1L);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void takeDamge(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player)event.getEntity();
		if (!(em.getPlayers().contains(player))) {
			return;
		}
		if (em.getStatus() == EventStatus.STARTED) {
			if (event.getCause() == DamageCause.LAVA || (event.getCause() == DamageCause.FALL && event.getDamage() >= 4)) {
				em.broadcast(Util.formatMsg("&b&l" + player.getName() + " &3fell and is out!"));;
				em.leaveEvent(player, true);
			}
		} else {
			player.teleport(em.getSpawn());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void snowballLand(ProjectileHitEvent event) {
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
				block.getWorld().spawnFallingBlock(block.getLocation(), Material.SNOW_BLOCK, (byte) 0);
				block.setType(Material.AIR);
			}
		}
	}
}
