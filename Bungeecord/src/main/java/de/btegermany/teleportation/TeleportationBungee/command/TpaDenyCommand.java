package de.btegermany.teleportation.TeleportationBungee.command;


import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.registry.TpasRegistry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static de.btegermany.teleportation.TeleportationBungee.TeleportationBungee.getFormattedMessage;

public class TpaDenyCommand extends Command implements TabExecutor {

    private final RegistriesProvider registriesProvider;

    public TpaDenyCommand(RegistriesProvider registriesProvider) {
        super("TpaDeny");
        this.registriesProvider = registriesProvider;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TpasRegistry tpasRegistry = registriesProvider.getTpasRegistry();

        if(!(sender instanceof ProxiedPlayer player)) {
            return;
        }

        // check permissions
        if(!player.hasPermission("teleportation.tpa")) {
            player.sendMessage(new ComponentBuilder("ᾠ §cDu §cbist §cnicht §cberechtigt, §cdiesen §cCommand §causzuführen!").create());
            return;
        }

        // get amount of tpas the player received
        long requests = tpasRegistry.getTpas().values().stream().filter(uuid -> uuid.equals(player.getUniqueId())).count();
        if(args.length == 1) {
            // will deny the tpa the target player sent
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if(target == null) {
                player.sendMessage(getFormattedMessage("Der Spieler wurde nicht gefunden!"));
                return;
            }
            if(tpasRegistry.isRegistered(target) && tpasRegistry.getTpa(target).equals(player.getUniqueId())) {
                target.sendMessage(getFormattedMessage("Deine Anfrage wurde abgelehnt!"));
                player.sendMessage(getFormattedMessage("Du hast die Anfrage von " + target.getName() + " abgelehnt."));
                tpasRegistry.unregister(target);
            } else {
                player.sendMessage(getFormattedMessage("Du hast keine Anfrage von diesem Spieler erhalten!"));
            }
        } else if(args.length == 0 && requests == 1) {
            // will deny the only tpa the player received
            for(Map.Entry<UUID, UUID> entry : tpasRegistry.getTpas().entrySet()) {
                if(!entry.getValue().equals(player.getUniqueId())) {
                    continue;
                }
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(entry.getKey());
                target.sendMessage(getFormattedMessage("Deine Anfrage wurde abgelehnt!"));
                player.sendMessage(getFormattedMessage("Du hast die Anfrage von " + target.getName() + " abgelehnt."));
                tpasRegistry.unregister(target);
                return;
            }
        } else {
            player.sendMessage(getFormattedMessage("Bitte gib den Spieler an, der die Anfrage gesendet hat!"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        Set<String> results = new HashSet<>();
        if(sender instanceof ProxiedPlayer player) {
            if(player.hasPermission("teleportation.tpa")) {
                if(args.length == 1) {
                    for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        if(registriesProvider.getTpasRegistry().getTpas().containsValue(p.getUniqueId()) && p.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                            results.add(p.getName());
                    }
                }
                if(!registriesProvider.getTpasRegistry().getTpas().containsValue(player.getUniqueId())) {
                    player.sendMessage(getFormattedMessage("Du hast keine Anfragen erhalten."));
                }
            }
        }
        return results;
    }

}
