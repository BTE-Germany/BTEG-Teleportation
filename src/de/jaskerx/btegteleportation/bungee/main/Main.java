package de.jaskerx.btegteleportation.bungee.main;

import java.util.HashMap;
import java.util.UUID;

import de.jaskerx.btegteleportation.bungee.commands.TeleportCommand;
import de.jaskerx.btegteleportation.bungee.commands.TpBackCommand;
import de.jaskerx.btegteleportation.bungee.commands.TpHereCommand;
import de.jaskerx.btegteleportation.bungee.commands.TpaCancelCommand;
import de.jaskerx.btegteleportation.bungee.commands.TpaCommand;
import de.jaskerx.btegteleportation.bungee.commands.TpaDenyCommand;
import de.jaskerx.btegteleportation.bungee.commands.TpacceptCommand;
import de.jaskerx.btegteleportation.bungee.listeners.PluginMsgListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {
	
	public static HashMap<UUID, LastLocation> lastLocations = new HashMap<>();

	@Override
	public void onEnable() {
		
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new TeleportCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpaCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpacceptCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpaDenyCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpaCancelCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpHereCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new TpBackCommand());
		
		ProxyServer.getInstance().getPluginManager().registerListener(this, new PluginMsgListener());
		
		ProxyServer.getInstance().registerChannel("Teleportation");	
	}
	
	public static BaseComponent[] getFormattedMessage(String text) {
		String[] words = text.split(" ");
		StringBuilder builder = new StringBuilder("§b§lBTEG §7»");
		for(String word : words) {
			builder.append(" §6" + word);
		}
		return new ComponentBuilder(new String(builder)).create();
	}
	
}
