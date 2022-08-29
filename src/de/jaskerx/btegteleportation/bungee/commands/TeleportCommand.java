package de.jaskerx.btegteleportation.bungee.commands;


import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.jaskerx.btegteleportation.bungee.main.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class TeleportCommand extends Command implements TabExecutor {

	public TeleportCommand() {
		super("Tp");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer p = (ProxiedPlayer) sender;
			if(!p.hasPermission("teleportation.tp")) {
				p.sendMessage(new ComponentBuilder("§b§lBTEG §7» §cDu §cbist §cnicht §cberechtigt, §cdiesen §cCommand §causzuführen!").create());
				return;
			}
			if(args.length != 1) {
				p.sendMessage(Main.getFormattedMessage("Bitte gib einen Spieler an!"));
				return;
			}
			ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
			
			
			if(target != null) {
				
				teleport(p, target);
			} else {
				p.sendMessage(Main.getFormattedMessage("Der Spieler wurde nicht gefunden!"));
			}
		}
	}
	
	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		Set<String> results = new HashSet<>();
		if(args.length == 1) {
			for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
				if(p.getName().toLowerCase().startsWith(args[0].toLowerCase()))
					results.add(p.getName());
			}
		}
		return results;
	}
	
	
	
	public static void teleport(ProxiedPlayer p, ProxiedPlayer t) {
		
		Server serverOld = p.getServer();
		p.sendMessage(Main.getFormattedMessage("Du wirst zu " + t.getName() + " teleportiert..."));
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(p.getUniqueId().toString());
        out.writeUTF(t.getUniqueId().toString());
        if(serverOld.getInfo().getName().equals(t.getServer().getInfo().getName())) {
        	out.writeUTF("true");
        } else {
        	p.connect(t.getServer().getInfo());
        	out.writeUTF("false");
        }
        t.getServer().getInfo().sendData("Teleportation", out.toByteArray());
	}

}
