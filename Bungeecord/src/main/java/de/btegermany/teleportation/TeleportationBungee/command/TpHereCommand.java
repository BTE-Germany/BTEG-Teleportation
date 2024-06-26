package de.btegermany.teleportation.TeleportationBungee.command;

import de.btegermany.teleportation.TeleportationBungee.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBungee.message.withresponse.RequestLastLocationMessage;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.util.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

import static de.btegermany.teleportation.TeleportationBungee.TeleportationBungee.getFormattedMessage;

public class TpHereCommand extends Command implements TabExecutor {

    private final Utils utils;
    private final RegistriesProvider registriesProvider;
    private final PluginMessenger pluginMessenger;

    public TpHereCommand(Utils utils, RegistriesProvider registriesProvider, PluginMessenger pluginMessenger) {
        super("TpHere");
        this.utils = utils;
        this.registriesProvider = registriesProvider;
        this.pluginMessenger = pluginMessenger;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!(sender instanceof ProxiedPlayer player)) {
            return;
        }

        // check permissions
        if(!player.hasPermission("teleportation.tphere")) {
            player.sendMessage(new ComponentBuilder("ᾠ §cDu §cbist §cnicht §cberechtigt, §cdiesen §cCommand §causzuführen!").create());
            return;
        }

        if(args.length != 1) {
            player.sendMessage(getFormattedMessage("Bitte gib einen Spieler an!"));
            return;
        }
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);


        if(target != null) {
            player.sendMessage(getFormattedMessage(target.getName() + " wird zu dir teleportiert..."));
            RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(player, this.registriesProvider, () -> {
                this.utils.teleport(target, player);
            });
            this.pluginMessenger.sendMessageToServers(requestLastLocationMessage, player.getServer().getInfo());
        } else {
            player.sendMessage(getFormattedMessage("Der Spieler wurde nicht gefunden!"));
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
