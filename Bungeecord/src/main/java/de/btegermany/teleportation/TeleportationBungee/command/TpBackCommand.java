package de.btegermany.teleportation.TeleportationBungee.command;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.util.LastLocation;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TpBackCommand extends Command {

    private final RegistriesProvider registriesProvider;
    private final PluginMessenger pluginMessenger;

    public TpBackCommand(RegistriesProvider registriesProvider, PluginMessenger pluginMessenger) {
        super("TpBack");
        this.registriesProvider = registriesProvider;
        this.pluginMessenger = pluginMessenger;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!(sender instanceof ProxiedPlayer player)) {
            return;
        }

        // check permissions
        if(!player.hasPermission("teleportation.tpback")) {
            player.sendMessage(new ComponentBuilder("ᾠ §cDu §cbist §cnicht §cberechtigt, §cdiesen §cCommand §causzuführen!").create());
            return;
        }

        // teleport player to last location
        teleport(player);
    }

    private void teleport(ProxiedPlayer player) {
        // check if there is a location to teleport back to
        if(!registriesProvider.getLastLocationsRegistry().isRegistered(player)) {
            player.sendMessage(TeleportationBungee.getFormattedMessage("Wenn du dich zuvor teleportiert hast, warte bitte einen Moment (wenige Sekunden) und führe dann den Command erneut aus."));
            return;
        }
        LastLocation lastLocation = registriesProvider.getLastLocationsRegistry().getLastLocation(player);
        String[] coordinatesSplit = lastLocation.getCoordinates().split(",");
        double x = Double.parseDouble(coordinatesSplit[0]);
        double y = Double.parseDouble(coordinatesSplit[1]);
        double z = Double.parseDouble(coordinatesSplit[2]);
        this.pluginMessenger.teleportToCoords(player, lastLocation.serverInfo(), x, y, z, lastLocation.yaw(), lastLocation.pitch(), lastLocation.world());
    }

}
