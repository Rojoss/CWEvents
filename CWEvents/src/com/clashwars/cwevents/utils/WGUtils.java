package com.clashwars.cwevents.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.clashwars.cwevents.CWEvents;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
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
		LocalWorld localWorld = BukkitUtil.getLocalWorld(world);
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(localWorld, -1);
		
		Region weRegion = null;
		if (region.getTypeName().equalsIgnoreCase("cuboid")) {
			weRegion = new CuboidRegion(localWorld, region.getMinimumPoint(), region.getMaximumPoint().toBlockPoint());
		} else {
			weRegion = new Polygonal2DRegion(localWorld, region.getPoints(), region.getMinimumPoint().getBlockY(), region.getMaximumPoint().getBlockY());
		}
		
		try {
			editSession.setBlocks(weRegion, new BaseBlock(blockID));
		} catch (MaxChangedBlocksException e) {
			return false;
		}
		return true;
	}
	
	public static boolean regionReplace(World world, ProtectedRegion region, int fromBlock, int toBlock) {
		LocalWorld localWorld = BukkitUtil.getLocalWorld(world);
		Set<BaseBlock> fromBlocks = new HashSet<BaseBlock>();
		fromBlocks.add(new BaseBlock(fromBlock));
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(localWorld, -1);
		
		Region weRegion = null;
		if (region.getTypeName().equalsIgnoreCase("cuboid")) {
			weRegion = new CuboidRegion(localWorld, region.getMinimumPoint(), region.getMaximumPoint());
			
		} else {
			weRegion = new Polygonal2DRegion(localWorld, region.getPoints(), region.getMinimumPoint().getBlockY(), region.getMaximumPoint().getBlockY());
			
		}
		
		try {
			editSession.replaceBlocks(weRegion, fromBlocks, new BaseBlock(toBlock));
		} catch (MaxChangedBlocksException e) {
			return false;
		}
		return true;
	}
	
	public static void pasteSchematic(World world, File file, Location location, boolean noAir, int rotation) throws MaxChangedBlocksException, DataException, IOException {
		pasteSchematic(world, file, BukkitUtil.toVector(location), noAir, rotation);
	}
	
	public static void pasteSchematic(World world, File file, Vector origin, boolean noAir, int rotation) throws DataException, IOException, MaxChangedBlocksException {
		if (rotation  != 90 && rotation != 180 && rotation != 270 && rotation != 360 && rotation != 0) {
			rotation = 0;
		}
		EditSession es = new EditSession(new BukkitWorld(world), 999999999);
		CuboidClipboard cc = CuboidClipboard.loadSchematic(file);
		cc.rotate2D(rotation);
		cc.paste(es, origin, noAir);
	}
}
