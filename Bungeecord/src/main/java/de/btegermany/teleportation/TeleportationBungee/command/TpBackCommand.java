package de.btegermany.teleportation.TeleportationBungee.command;


import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.util.LastLocation;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TpBackCommand extends Command {

    private final RegistriesProvider registriesProvider;

    public TpBackCommand(RegistriesProvider registriesProvider) {
        super("TpBack");
        this.registriesProvider = registriesProvider;
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
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("teleport_coords");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(lastLocation.getCoordinates());
        out.writeUTF(String.valueOf(lastLocation.getYaw()));
        out.writeUTF(String.valueOf(lastLocation.getPitch()));
        // if needed connect to the right server
        if(!lastLocation.getServerInfo().equals(player.getServer().getInfo())) {
            player.connect(lastLocation.getServerInfo());
        }
        // send teleportation data and unregister "used" LastLocation
        lastLocation.getServerInfo().sendData(TeleportationBungee.PLUGIN_CHANNEL, out.toByteArray());
        registriesProvider.getLastLocationsRegistry().unregister(player);
    }

}
