package de.jaskerx.bteg.teleportation.bungee.commands;

import java.util.HashSet;
import java.util.Set;

import de.jaskerx.bteg.teleportation.bungee.main.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class TpHereCommand extends Command implements TabExecutor {

	public TpHereCommand() {
		super("TpHere");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer p = (ProxiedPlayer) sender;
			if(!p.hasPermission("teleportation.tphere")) {
				p.sendMessage(new ComponentBuilder("?b?lBTEG ?7? ?cDu ?cbist ?cnicht ?cberechtigt, ?cdiesen ?cCommand ?causzuf?hren!").create());
				return;
			}
			
			if(args.length != 1) {
				p.sendMessage(Main.getFormattedMessage("Bitte gib einen Spieler an!"));
				return;
			}
			ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
			
			
			if(target != null) {
				
				p.sendMessage(Main.getFormattedMessage(target.getName() + " wird zu dir teleportiert..."));
				TeleportCommand.teleport(target, p);
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

}
