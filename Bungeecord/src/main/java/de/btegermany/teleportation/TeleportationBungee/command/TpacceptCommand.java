package de.btegermany.teleportation.TeleportationBungee.command;


import de.btegermany.teleportation.TeleportationBungee.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBungee.message.withresponse.RequestLastLocationMessage;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.registry.TpasRegistry;
import de.btegermany.teleportation.TeleportationBungee.util.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static de.btegermany.teleportation.TeleportationBungee.TeleportationBungee.getFormattedMessage;

public class TpacceptCommand extends Command implements TabExecutor {

    private final Utils utils;
    private final RegistriesProvider registriesProvider;
    private final PluginMessenger pluginMessenger;

    public TpacceptCommand(Utils utils, RegistriesProvider registriesProvider, PluginMessenger pluginMessenger) {
        super("Tpaccept");
        this.utils = utils;
        this.registriesProvider = registriesProvider;
        this.pluginMessenger = pluginMessenger;
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
            // will accept the tpa the target player sent
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if(target == null) {
                player.sendMessage(getFormattedMessage("Der Spieler wurde nicht gefunden!"));
                return;
            }
            if(tpasRegistry.isRegistered(target) && tpasRegistry.getTpa(target).equals(player.getUniqueId())) {
                target.sendMessage(getFormattedMessage("Deine Anfrage wurde angenommen!"));
                RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(player, this.registriesProvider, () -> {
                    this.utils.teleport(target, player);
                    tpasRegistry.unregister(target);
                });
                this.pluginMessenger.sendMessageToServers(requestLastLocationMessage, player.getServer().getInfo());
            } else {
                player.sendMessage(getFormattedMessage("Du hast keine Anfrage von diesem Spieler erhalten!"));
            }
        } else if(args.length == 0 && requests == 1) {
            // will accept the only tpa the player received
            for(Map.Entry<UUID, UUID> entry : tpasRegistry.getTpas().entrySet()) {
                if(!entry.getValue().equals(player.getUniqueId())) {
                    continue;
                }
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(entry.getKey());
                target.sendMessage(getFormattedMessage("Deine Anfrage wurde angenommen!"));
                utils.teleport(target, player);
                tpasRegistry.unregister(target);
                return;
            }
        } else {
            player.sendMessage(getFormattedMessage("Bitte gib den Spieler an, der die Anfrage gesendet hat!"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        HashSet<String> results = new HashSet<>();
        if(sender instanceof ProxiedPlayer player) {
            if(player.hasPermission("teleportation.tpa")) {
                if(args.length == 1) {
                    for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        if(registriesProvider.getTpasRegistry().getTpas().containsValue(p.getUniqueId()) && p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                            results.add(p.getName());
                        }
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