package de.btegermany.teleportation.TeleportationBungee.commands;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static de.btegermany.teleportation.TeleportationBungee.TeleportationBungee.getFormattedMessage;
import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class TpacceptCommand extends Command implements TabExecutor {

    public TpacceptCommand() {
        super("Tpaccept");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer) {

            ProxiedPlayer p = (ProxiedPlayer) sender;
            if(!p.hasPermission("teleportation.tpa")) {
                p.sendMessage(new ComponentBuilder("§b§lBTEG §7» §cDu §cbist §cnicht §cberechtigt, §cdiesen §cCommand §causzuführen!").create());
                return;
            }

            long requests = TpaCommand.tpas.values().stream().filter(uuid -> uuid.equals(p.getUniqueId())).count();
            if(args.length == 1) {
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
                if(target != null) {
                    if(TpaCommand.tpas.containsKey(target.getUniqueId()) && TpaCommand.tpas.get(target.getUniqueId()).equals(p.getUniqueId())) {
                        target.sendMessage(getFormattedMessage("Deine Anfrage wurde angenommen!"));
                        TeleportCommand.teleport(target, p);
                        TpaCommand.tpas.remove(target.getUniqueId());
                    } else {
                        p.sendMessage(getFormattedMessage("Du hast keine Anfrage von diesem Spieler erhalten!"));
                    }
                } else {
                    p.sendMessage(getFormattedMessage("Der Spieler wurde nicht gefunden!"));
                }
            } else if(args.length == 0 && requests == 1) {
                for(Map.Entry<UUID, UUID> entry : TpaCommand.tpas.entrySet()) {
                    if(entry.getValue().equals(p.getUniqueId())) {
                        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(entry.getKey());
                        target.sendMessage(getFormattedMessage("Deine Anfrage wurde angenommen!"));
                        TeleportCommand.teleport(target, p);
                        TpaCommand.tpas.remove(target.getUniqueId());
                        return;
                    }
                }
            } else {
                p.sendMessage(getFormattedMessage("Bitte gib den Spieler an, der die Anfrage gesendet hat!"));
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        HashSet<String> results = new HashSet<>();
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if(player.hasPermission("teleportation.tpa")) {
                if(args.length == 1) {
                    for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        if(TpaCommand.tpas.containsValue(p.getUniqueId()) && p.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                            results.add(p.getName());
                    }
                }
                if(!TpaCommand.tpas.containsValue(player.getUniqueId())) {
                    player.sendMessage(getFormattedMessage("Du hast keine Anfragen erhalten."));
                }
            }
        }
        return results;
    }

}