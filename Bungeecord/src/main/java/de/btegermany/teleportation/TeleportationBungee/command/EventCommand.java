package de.btegermany.teleportation.TeleportationBungee.command;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBungee.message.withresponse.RequestLastLocationMessage;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.util.Warp;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class EventCommand extends Command {

    private final RegistriesProvider registriesProvider;
    private final PluginMessenger pluginMessenger;

    public EventCommand(RegistriesProvider registriesProvider, PluginMessenger pluginMessenger) {
        super("event");
        this.registriesProvider = registriesProvider;
        this.pluginMessenger = pluginMessenger;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer player)) {
            sender.sendMessage(TeleportationBungee.getFormattedMessage("Dieser Command kann nur von Spielern ausgeführt werden."));
            return;
        }

        Warp warp = this.registriesProvider.getWarpsRegistry().getWarps().stream().filter(warp1 -> warp1.getName().equalsIgnoreCase("event")).findFirst().orElse(null);
        if(warp == null) {
            player.sendMessage(TeleportationBungee.getFormattedMessage("Gerade findet kein Event statt."));
            return;
        }

        RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(player, this.registriesProvider, () -> {
            this.pluginMessenger.performCommand(player, "nwarp event");
        });
        this.pluginMessenger.sendMessageToServers(requestLastLocationMessage, player.getServer().getInfo());
    }

}
