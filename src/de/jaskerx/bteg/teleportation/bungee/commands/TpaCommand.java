package de.jaskerx.bteg.teleportation.bungee.commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import de.jaskerx.bteg.teleportation.bungee.main.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class TpaCommand extends Command implements TabExecutor {

	public static HashMap<UUID, UUID> tpas = new HashMap<>();
	
	public TpaCommand() {
		super("Tpa");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
		
			ProxiedPlayer p = (ProxiedPlayer) sender;
			if(!p.hasPermission("teleportation.tpa")) {
				p.sendMessage(new ComponentBuilder("§b§lBTEG §7» §cDu §cbist §cnicht §cberechtigt, §cdiesen §cCommand §causzuführen!").create());
				return;
			}

			if(args.length != 1) {
				p.sendMessage(Main.getFormattedMessage("Bitte gib einen Spieler an!"));
				return;
			}
			ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
			
			if(target != null) {
				if(tpas.containsKey(p.getUniqueId())) {
					cancel(p);
				}
				TextComponent compAccept = new TextComponent("/tpaccept " + p.getName());
				compAccept.setColor(ChatColor.GREEN);
				compAccept.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/tpaccept " + p.getName()));
				TextComponent compDeny = new TextComponent("/tpadeny " + p.getName());
				compDeny.setColor(ChatColor.RED);
				compDeny.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/tpadeny " + p.getName()));
				
				tpas.put(p.getUniqueId(), target.getUniqueId());
				target.sendMessage(new ComponentBuilder("§b§lBTEG §7» §6Du §6hast §6eine §6Teleport-Anfrage §6von §6" + p.getDisplayName() + " §6erhalten. §6Nutze ").append(compAccept).append(" §6zum §6Akzeptieren §6und ").append(compDeny).append(" §6zum §6Ablehnen §6der §6Anfrage.").create());
				p.sendMessage(Main.getFormattedMessage("Die Anfrage wurde gesendet! Um sie abzubrechen, nutze /tpacancel."));
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
	
	
	
	public static void cancel(ProxiedPlayer p) {
		if(tpas.containsKey(p.getUniqueId())) {
			ProxiedPlayer target = ProxyServer.getInstance().getPlayer(tpas.get(p.getUniqueId()));
			tpas.remove(p.getUniqueId());
			p.sendMessage(Main.getFormattedMessage("Die Anfrage wurde abgebrochen."));
			if(target != null) {
				target.sendMessage(Main.getFormattedMessage(p.getDisplayName() + " hat die Anfrage abgebrochen!"));
			}
		} else {
			p.sendMessage(Main.getFormattedMessage("Du hast keine Anfrage gestellt!"));
		}
	}

}
