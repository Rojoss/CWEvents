package com.clashwars.cwevents.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.clashwars.cwevents.CWEvents;

public class CWEventsPlugin extends JavaPlugin {
	private CWEvents	cwe;

	@Override
	public void onDisable() {
		cwe.onDisable();
	}

	@Override
	public void onEnable() {
		cwe = new CWEvents(this);
		cwe.onEnable();
	}

	public CWEvents getInstance() {
		return cwe;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return cwe.onCommand(sender, cmd, label, args);
	}
}
