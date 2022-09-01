package de.jaskerx.btegteleportation.bukkit.main;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import de.jaskerx.btegteleportation.bukkit.listeners.PlayerJoinListener;
import de.jaskerx.btegteleportation.bukkit.listeners.PluginMsgListener;

public class Main extends JavaPlugin {

	private static Main instance;
	public static HashMap<UUID, PendingTpPlayer> pendingTps = new HashMap<>();
	public static HashMap<UUID, Location> pendingTpBacks = new HashMap<>();
	
	@Override
	public void onEnable() {
		instance = this;
		
		getServer().getMessenger().registerIncomingPluginChannel(this, "Teleportation", new PluginMsgListener());
		getServer().getMessenger().registerOutgoingPluginChannel(this, "Teleportation");
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
	}
	
	public static Main getInstance() {
		return instance;
	}
}
