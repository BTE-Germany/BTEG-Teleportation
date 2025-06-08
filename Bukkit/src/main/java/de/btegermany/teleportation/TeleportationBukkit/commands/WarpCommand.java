package de.btegermany.teleportation.TeleportationBukkit.commands;

import de.btegermany.teleportation.TeleportationBukkit.gui.WarpGui;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.GuiArgs;
import de.btegermany.teleportation.TeleportationBukkit.message.*;
import de.btegermany.teleportation.TeleportationAPI.State;
import de.btegermany.teleportation.TeleportationBukkit.message.withresponse.GetGuiDataMessage;
import de.btegermany.teleportation.TeleportationBukkit.util.TabExecutorEnhanced;
import de.btegermany.teleportation.TeleportationBukkit.util.WarpGettingChanged;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.util.WarpInCreation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit.getFormattedErrorMessage;

public class WarpCommand implements CommandExecutor, TabExecutorEnhanced {

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
            new WarpGui(new GuiArgs(player, this.pluginMessenger, this.registriesProvider)).open();
            return true;
        }

        if(args[0].equalsIgnoreCase("help")) {
            return false;
        }

        if(!args[0].equals("create") && !args[0].equals("delete") && !args[0].equals("change") && !args[0].equals("tag")) {
            if(args[0].equals("random")) {
                this.pluginMessenger.send(new TpToRandomWarpMessage(player));
                return true;
            }
            this.findWarps(player, args);
            return true;
        }

        if(args.length < 2) {
            return false;
        }

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

                if (this.isHeadIdInvalid(headId)) {
                    player.sendMessage(TeleportationBukkit.getFormattedErrorMessage("Bitte überprüfe die headId."));
                    return true;
                }

                headId = headId.replace("http://textures.minecraft.net/texture/", "");

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
                this.pluginMessenger.send(new DeleteWarpMessage(player, id));
            }
            case "change" -> {
                if (args.length < 3) {
                    return false;
                }
                int id = Integer.parseInt(args[1]);
                String column = args[2].toLowerCase();
                column = column.equalsIgnoreCase("headId") ? "head_id" : column;
                String value = args.length == 3 ? "null" : String.join(" ", Stream.of(args).skip(3).filter(arg -> !arg.isEmpty()).toArray(String[]::new));

                if (column.equals("head_id")) {
                    if (this.isHeadIdInvalid(value)) {
                        player.sendMessage(TeleportationBukkit.getFormattedErrorMessage("Bitte überprüfe die headId."));
                        return true;
                    }

                    value = value.replace("http://textures.minecraft.net/texture/", "");
                }

                WarpGettingChanged warpGettingChanged = new WarpGettingChanged(id, column);
                warpGettingChanged.setValue(value);
                if (args.length == 3 && (column.equals("yaw") || column.equals("pitch") || column.equals("height"))) {
                    warpGettingChanged.setValue(column.equals("yaw") ? String.valueOf(player.getLocation().getYaw()) : (column.equals("pitch") ? String.valueOf(player.getLocation().getPitch()) : String.valueOf(player.getLocation().getY())));
                }
                if (!column.equals("head_id") && warpGettingChanged.getValue().equals("null")) {
                    return false;
                }
                this.pluginMessenger.send(new ChangeWarpMessage(player, warpGettingChanged));
            }
            case "tag" -> {
                if (args.length < 4) {
                    return false;
                }
                String tag = args[1];

                switch (args[2].toLowerCase()) {
                    case "add" -> this.pluginMessenger.send(new WarpAddTagMessage(player, tag, Integer.parseInt(args[3])));
                    case "remove" -> this.pluginMessenger.send(new WarpRemoveTagMessage(player, tag, Integer.parseInt(args[3])));
                    case "edit" -> this.pluginMessenger.send(new WarpEditTagMessage(player, tag, args[3]));
                }
            }
        }
        return true;
    }

    private void findWarps(Player player, String[] args) {
        StringBuilder searchBuilder = new StringBuilder();
        for(int i = 0; i < args.length; i++) {
            if(i >= 1) {
                searchBuilder.append(" ");
            }
            searchBuilder.append(args[i]);
        }
        String search = new String(searchBuilder);
        this.pluginMessenger.send(new GetGuiDataMessage(this.registriesProvider, this.pluginMessenger, player.getUniqueId().toString(), String.format("search_%s", search), 0, 1));
    }

    private boolean isHeadIdInvalid(String headId) {
        return headId == null || !headId.matches("(http://textures\\.minecraft\\.net/texture/)?[a-z0-9]+");
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        List<String> result = new ArrayList<>();

        switch (args.length) {
            case 1 -> {
                result.addAll(TabExecutorEnhanced.super.getValidSuggestions(args[0], this.registriesProvider.getCitiesRegistry().getCities().toArray(String[]::new)));
                if(!sender.hasPermission("bteg.warps.manage")) {
                    break;
                }
                result.addAll(TabExecutorEnhanced.super.getValidSuggestions(args[0], "help", "create", "delete", "change", "tag"));
            }
            case 2 -> {
                if(!sender.hasPermission("bteg.warps.manage") || !args[0].equalsIgnoreCase("tag")) {
                    break;
                }
                result.addAll(TabExecutorEnhanced.super.getValidSuggestions(args[1], this.registriesProvider.getWarpTagsRegistry().getTags().toArray(String[]::new)));
            }
            case 3 -> {
                if(!sender.hasPermission("bteg.warps.manage")) {
                    break;
                }

                if(args[0].equalsIgnoreCase("change")) {
                    result.addAll(TabExecutorEnhanced.super.getValidSuggestions(args[2], "name", "city", "state", "coordinates", "headId", "yaw", "pitch", "height"));
                    break;
                }
                if(args[0].equalsIgnoreCase("tag")) {
                    result.addAll(TabExecutorEnhanced.super.getValidSuggestions(args[2], "add", "remove", "edit"));
                }
            }
        }

        return result;
    }
}
