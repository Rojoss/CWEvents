package com.clashwars.cwevents.utils;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.clashwars.cwevents.CWEvents;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WGUtils {
	
	public static ProtectedRegion getRegion(World world, String name) {
		return WGBukkit.getRegionManager(world).getRegion(name);
	}
	
	public static boolean setFlag(World world, String regionID, StateFlag flag, String value) {
		ProtectedRegion region = WGBukkit.getRegionManager(world).getRegion(regionID);
		try {
			region.setFlag(flag, flag.parseInput(WGBukkit.getPlugin(), CWEvents.instance.getServer().getConsoleSender(), value));
		} catch (InvalidFlagFormat e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
		
	public static boolean getFlag(Player player, StateFlag flag) {
		ApplicableRegionSet set = WGBukkit.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation());
		LocalPlayer localPlayer = WGBukkit.getPlugin().wrapPlayer(player);
		return set.allows(flag, localPlayer);
	}
	
	public static boolean canPvP(Player player) {
		ApplicableRegionSet set = WGBukkit.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation());
		LocalPlayer localPlayer = WGBukkit.getPlugin().wrapPlayer(player);
		return set.allows(DefaultFlag.PVP, localPlayer);
	}
	
	public static boolean regionFill(World world, ProtectedRegion region, int blockID) {
		LocalWorld localWorld = null;
		//Polygonal2DRegion polyRegion = new Polygonal2DRegion(localWorld, region.getPoints(), region.getMinimumPoint().getBlockY(), region.getMaximumPoint().getBlockY());
		CuboidRegion cuboidRegion = new CuboidRegion(localWorld, region.getMinimumPoint(), region.getMaximumPoint());
		
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(localWorld, -1);
		try {
			editSession.setBlocks(cuboidRegion, new BaseBlock(blockID));
		} catch (MaxChangedBlocksException e) {
			return false;
		}
		return true;
	}
	
	public static boolean regionReplace(World world, ProtectedRegion region, int fromBlock, int toBlock) {
		LocalWorld localWorld = BukkitUtil.getLocalWorld(world);
		Set<BaseBlock> fromBlocks = new HashSet<BaseBlock>();
		fromBlocks.add(new BaseBlock(fromBlock));
		//Polygonal2DRegion polyRegion = new Polygonal2DRegion(localWorld, region.getPoints(), region.getMinimumPoint().getBlockY(), region.getMaximumPoint().getBlockY());
		CuboidRegion cuboidRegion = new CuboidRegion(localWorld, region.getMinimumPoint(), region.getMaximumPoint());
		
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(localWorld, -1);
		try {
			editSession.replaceBlocks(cuboidRegion, fromBlocks, new BaseBlock(toBlock));
		} catch (MaxChangedBlocksException e) {
			return false;
		}
		return true;
	}
}
