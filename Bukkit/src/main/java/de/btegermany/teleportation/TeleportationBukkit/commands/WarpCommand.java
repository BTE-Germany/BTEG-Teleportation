package de.btegermany.teleportation.TeleportationBukkit.commands;

import de.btegermany.teleportation.TeleportationBukkit.gui.WarpGui;
import de.btegermany.teleportation.TeleportationBukkit.message.*;
import de.btegermany.teleportation.TeleportationBukkit.util.State;
import de.btegermany.teleportation.TeleportationBukkit.util.WarpGettingChanged;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.util.WarpInCreation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit.getFormattedErrorMessage;

public class WarpCommand implements CommandExecutor, TabExecutor {

    private final PluginMessenger pluginMessenger;
    private final RegistriesProvider registriesProvider;

    public WarpCommand(PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {
        this.pluginMessenger = pluginMessenger;
        this.registriesProvider = registriesProvider;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String name, @Nonnull String[] args) {

        if(!(sender instanceof Player player)) {
            sender.sendMessage(getFormattedErrorMessage("Diesen Command können nur Spieler ausführen!"));
            return true;
        }

        if(args.length == 0) {
            new WarpGui(player, pluginMessenger, registriesProvider).open();
        }

        if(args.length >= 1 && !args[0].equals("create") && !args[0].equals("delete") && !args[0].equals("change")) {
            findWarps(player, args);
            return true;
        }

        if(args.length >= 2) {
            if(!player.hasPermission("bteg.warps.manage")) {
                return true;
            }

            switch (args[0]) {
                case "create" -> {
                    String seperatorRegex = ";";
                    String[] inputSeperated = Arrays.stream(String.join(" ", Stream.of(args).skip(1).filter(arg -> !arg.isEmpty()).toArray(String[]::new)).split(seperatorRegex)).map(String::trim).toArray(String[]::new);
                    if (inputSeperated.length < 3) {
                        return false;
                    }
                    String headId = inputSeperated.length >= 4 && !(inputSeperated[3].isEmpty() || inputSeperated[3].matches(" *; *")) ? inputSeperated[3] : null;

                    WarpInCreation warpInCreation = new WarpInCreation(player);
                    warpInCreation.setName(inputSeperated[0]);
                    warpInCreation.setCity(inputSeperated[1]);
                    warpInCreation.setState(State.getStateFromInput(inputSeperated[2]));
                    if (warpInCreation.getState() == null) {
                        player.sendMessage(TeleportationBukkit.getFormattedMessage(String.format("§9\"%s\" §6ist weder Name noch eine gültige Abkürzung eines Bundeslandes. Bitte überprüfe deine Eingabe.", inputSeperated[2])));
                        return true;
                    }
                    warpInCreation.setHeadId(headId);
                    this.pluginMessenger.send(new CreateWarpMessage(warpInCreation));
                }
                case "delete" -> {
                    int id = Integer.parseInt(args[1]);
                    pluginMessenger.send(new DeleteWarpMessage(player, id));
                }
                case "change" -> {
                    if (args.length < 3) {
                        return false;
                    }
                    int id = Integer.parseInt(args[1]);
                    String column = args[2].toLowerCase();
                    column = column.equalsIgnoreCase("headId") ? "head_id" : column;
                    String value = args.length == 3 ? "null" : String.join(" ", Stream.of(args).skip(3).filter(arg -> !arg.isEmpty()).toArray(String[]::new));

                    WarpGettingChanged warpGettingChanged = new WarpGettingChanged(id, column);
                    warpGettingChanged.setValue(value);
                    if (args.length == 3 && (column.equals("yaw") || column.equals("pitch") || column.equals("height"))) {
                        warpGettingChanged.setValue(column.equals("yaw") ? String.valueOf(player.getLocation().getYaw()) : (column.equals("pitch") ? String.valueOf(player.getLocation().getPitch()) : String.valueOf(player.getLocation().getY())));
                    }
                    if (!column.equals("head_id") && warpGettingChanged.getValue().equals("null")) {
                        return false;
                    }
                    pluginMessenger.send(new ChangeWarpMessage(player, warpGettingChanged));
                }
            }
            return true;
        }

        return true;
    }

    public void findWarps(Player player, String[] args) {
        StringBuilder searchBuilder = new StringBuilder();
        for(int i = 0; i < args.length; i++) {
            if(i >= 1) {
                searchBuilder.append(" ");
            }
            searchBuilder.append(args[i]);
        }
        String search = new String(searchBuilder);
        pluginMessenger.send(new GetGuiDataMessage(player.getUniqueId().toString(), String.format("search_%s", search), 0, 1));
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        return new ArrayList<>();
    }
}
