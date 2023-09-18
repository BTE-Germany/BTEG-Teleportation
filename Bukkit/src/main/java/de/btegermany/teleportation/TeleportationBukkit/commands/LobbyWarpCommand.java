package de.btegermany.teleportation.TeleportationBukkit.commands;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.message.GetGuiDataMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.util.LobbyCity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit.getFormattedErrorMessage;
import static de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit.getFormattedMessage;

public class LobbyWarpCommand implements CommandExecutor {

    private final PluginMessenger pluginMessenger;
    private final File lobbyCitiesConfigFile;
    private final FileConfiguration lobbyCitiesConfig;

    public LobbyWarpCommand(PluginMessenger pluginMessenger, File lobbyCitiesConfigFile, FileConfiguration lobbyCitiesConfig) {
        this.pluginMessenger = pluginMessenger;
        this.lobbyCitiesConfigFile = lobbyCitiesConfigFile;
        this.lobbyCitiesConfig = lobbyCitiesConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(getFormattedErrorMessage("Diesen Command können nur Spieler ausführen!"));
            return true;
        }
        Player player = (Player) sender;

        if(args.length < 1) {
            sender.sendMessage(getFormattedErrorMessage("Bitte gib eine Stadt an!"));
            return true;
        }

        if(args[0].equalsIgnoreCase("add")) {
            if(!player.hasPermission("bteg.warps.manage")) {
                return true;
            }
            if(args.length < 5) {
                return false;
            }

            String city = args[1].substring(0, 1).toUpperCase() + args[1].substring(1).toLowerCase();

            if(this.lobbyCitiesConfig.getKeys(false).contains(city)) {
                player.sendMessage(getFormattedErrorMessage("Diese Stadt existiert schon!"));
                return true;
            }

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

            this.lobbyCitiesConfig.set(city + ".center-latitude", lobbyCity.getCenterLat());
            this.lobbyCitiesConfig.set(city + ".center-longitude", lobbyCity.getCenterLon());
            this.lobbyCitiesConfig.set(city + ".radius-km", lobbyCity.getRadius());
            this.lobbyCitiesConfig.set(city + ".x", lobbyCity.getBlock().getX());
            this.lobbyCitiesConfig.set(city + ".y", lobbyCity.getBlock().getY());
            this.lobbyCitiesConfig.set(city + ".z", lobbyCity.getBlock().getZ());
            this.lobbyCitiesConfig.set(city + ".world", lobbyCity.getBlock().getWorld().getUID().toString());
            try {
                this.lobbyCitiesConfig.save(this.lobbyCitiesConfigFile);
                TeleportationBukkit.lobbyCities.add(lobbyCity);
                player.sendMessage(getFormattedMessage(String.format("%s wurde hinzugefügt!", city)));
                Location armorStandLocation = player.getLocation().getBlock().getLocation();
                armorStandLocation.setX(armorStandLocation.getX() + 0.5);
                armorStandLocation.setZ(armorStandLocation.getZ() + 0.5);
                ArmorStand armorStand = (ArmorStand) armorStandLocation.getWorld().spawnEntity(armorStandLocation, EntityType.ARMOR_STAND);
                armorStand.setGravity(false);
                armorStand.setCanPickupItems(false);
                armorStand.setCustomNameVisible(true);
                armorStand.setVisible(false);
                armorStand.setCustomName(ChatColor.GOLD + city);
            } catch (IOException e) {
                player.sendMessage(getFormattedErrorMessage("Ein Fehler ist aufgetreten!"));
                e.printStackTrace();
            }
            return true;
        }

        String city = args[0];

        this.pluginMessenger.send(new GetGuiDataMessage(player.getUniqueId().toString(), String.format("lobbywarp_%s", city), 0, 1));

        return true;
    }

}
