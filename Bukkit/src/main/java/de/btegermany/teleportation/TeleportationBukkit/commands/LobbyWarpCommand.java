package de.btegermany.teleportation.TeleportationBukkit.commands;

import de.btegermany.teleportation.TeleportationBukkit.message.withresponse.GetGuiDataMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.util.LobbyCity;
import de.btegermany.teleportation.TeleportationBukkit.util.TabExecutorEnhanced;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit.getFormattedErrorMessage;
import static de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit.getFormattedMessage;

public class LobbyWarpCommand implements CommandExecutor, TabExecutorEnhanced {

    private final PluginMessenger pluginMessenger;
    private final RegistriesProvider registriesProvider;

    public LobbyWarpCommand(PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {
        this.pluginMessenger = pluginMessenger;
        this.registriesProvider = registriesProvider;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {

        if(!(sender instanceof Player player)) {
            sender.sendMessage(getFormattedErrorMessage("Diesen Command können nur Spieler ausführen!"));
            return true;
        }

        // check args length
        if(args.length < 1) {
            sender.sendMessage(getFormattedErrorMessage("Bitte gib eine Stadt an!"));
            return true;
        }

        // add city
        if(args[0].equalsIgnoreCase("add")) {
            // check permissions
            if(!player.hasPermission("bteg.warps.manage")) {
                return true;
            }
            // check args length
            if(args.length < 5) {
                return false;
            }

            // format city name
            String city = args[1].substring(0, 1).toUpperCase() + args[1].substring(1).toLowerCase();

            // check if city already exists
            if(this.registriesProvider.getLobbyCitiesRegistry().doesExist(city)) {
                player.sendMessage(getFormattedErrorMessage("Diese Stadt existiert schon!"));
                return true;
            }

            // create LobbyCity object from args
            double centerLat = Double.parseDouble(args[2].replace(",", ""));
            double centerLon = Double.parseDouble(args[3]);
            int radius = Integer.parseInt(args[4]);
            LobbyCity lobbyCity = new LobbyCity.LobbyCityBuilder()
                    .setCity(city)
                    .setCenterLat(centerLat)
                    .setCenterLon(centerLon)
                    .setRadius(radius)
                    .setWorld(player.getLocation().getWorld())
                    .setX(player.getLocation().getBlockX())
                    .setY(player.getLocation().getBlockY())
                    .setZ(player.getLocation().getBlockZ())
                    .build();

            // save city
            this.registriesProvider.getLobbyCitiesRegistry().register(lobbyCity);
            player.sendMessage(getFormattedMessage(String.format("%s wurde hinzugefügt!", city)));
            return true;
        }

        // remove city
        if(args[0].equalsIgnoreCase("remove")) {
            // check permissions
            if(!player.hasPermission("bteg.warps.manage")) {
                return true;
            }
            // check args length
            if(args.length < 2) {
                return false;
            }

            // find the city if it has been registered
            Optional<LobbyCity> lobbyCityOptional = this.registriesProvider.getLobbyCitiesRegistry().getLobbyCities().stream().filter(lobbyCity -> lobbyCity.getCity().equalsIgnoreCase(args[1])).findFirst();
            if(lobbyCityOptional.isEmpty()) {
                player.sendMessage(getFormattedErrorMessage("Für diese Stadt wurde keine Druckplatte festgelegt!"));
                return true;
            }
            this.registriesProvider.getLobbyCitiesRegistry().unregister(lobbyCityOptional.get());
            player.sendMessage(getFormattedMessage("Die Stadt wurde gelöscht."));
            return true;
        }

        // get gui data for city
        String city = args[0];
        this.pluginMessenger.send(new GetGuiDataMessage(this.registriesProvider, this.pluginMessenger, player.getUniqueId().toString(), String.format("lobbywarp_%s", city), 0, 1));

        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] args) {
        if(!commandSender.hasPermission("bteg.warps.manage")) {
            return null;
        }

        List<String> result = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                result.addAll(this.getValidSuggestions(args[0], "add", "remove"));
            }
        }
        return result;
    }
}
