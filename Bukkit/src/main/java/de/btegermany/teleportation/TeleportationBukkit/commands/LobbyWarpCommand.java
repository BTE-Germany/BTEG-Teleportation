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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit.getFormattedErrorMessage;
import static de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit.getFormattedMessage;

public class LobbyWarpCommand implements CommandExecutor, TabExecutorEnhanced {

    private static final int CITY_INDEX = 4;
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

        if (args[0].equalsIgnoreCase("help")) {
            return false;
        }

        // add city
        if(args[0].equalsIgnoreCase("add")) {
            // check permissions
            if(!player.hasPermission("bteg.warps.manage")) {
                return true;
            }
            // check args length
            if(args.length <= CITY_INDEX) {
                return false;
            }

            String city = this.getCityFromArgs(CITY_INDEX, args);

            // check if city already exists
            if(this.registriesProvider.getLobbyCitiesRegistry().doesExist(city)) {
                player.sendMessage(getFormattedErrorMessage("Diese Stadt existiert schon!"));
                return true;
            }

            // create LobbyCity object from args
            double centerLat = Double.parseDouble(args[CITY_INDEX - 3].replace(",", ""));
            double centerLon = Double.parseDouble(args[CITY_INDEX - 2]);
            int radius = Integer.parseInt(args[CITY_INDEX - 1]);
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

            String city = this.getCityFromArgs(1, args);

            // find the city if it has been registered
            Optional<LobbyCity> lobbyCityOptional = this.registriesProvider.getLobbyCitiesRegistry().getLobbyCities().stream().filter(lobbyCity -> lobbyCity.getCity().equalsIgnoreCase(city)).findFirst();
            if(lobbyCityOptional.isEmpty()) {
                player.sendMessage(getFormattedErrorMessage("Für diese Stadt wurde keine Druckplatte festgelegt!"));
                return true;
            }
            this.registriesProvider.getLobbyCitiesRegistry().unregister(lobbyCityOptional.get());
            player.sendMessage(getFormattedMessage("Die Stadt wurde gelöscht."));
            return true;
        }

        // get gui data for city
        String city = this.getCityFromArgs(0, args);
        this.pluginMessenger.send(new GetGuiDataMessage(this.registriesProvider, this.pluginMessenger, player.getUniqueId().toString(), String.format("lobbywarp_%s", city), 0, 1));
        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] args) {
        if (!commandSender.hasPermission("bteg.warps.manage")) {
            return null;
        }

        if (args.length == 1) {
            return this.getValidSuggestions(args[0], "add", "remove");
        }

        if (args[0].equalsIgnoreCase("remove")) {
            String city = this.getCityFromArgs(1, args);
            return this.getValidSuggestions(city, this.registriesProvider.getLobbyCitiesRegistry().getLobbyCities().stream().map(LobbyCity::getCity).toArray(String[]::new));
        }

        return new ArrayList<>();
    }

    private String getCityFromArgs(int startIndex, String... args) {
        return String.join(" ", Arrays.copyOfRange(args, startIndex, args.length));
    }
}
