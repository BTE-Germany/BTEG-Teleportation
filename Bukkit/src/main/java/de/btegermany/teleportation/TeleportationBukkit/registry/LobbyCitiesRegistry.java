package de.btegermany.teleportation.TeleportationBukkit.registry;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.util.LobbyCity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LobbyCitiesRegistry {

    private final TeleportationBukkit plugin;
    private final Set<LobbyCity> lobbyCities;
    private final File lobbyCitiesConfigFile;
    private final FileConfiguration lobbyCitiesConfig;

    public LobbyCitiesRegistry(TeleportationBukkit plugin, String configFileName) {
        this.plugin = plugin;
        this.lobbyCities = new HashSet<>();

        this.lobbyCitiesConfigFile = new File(this.plugin.getDataFolder(), configFileName);
        if(!this.lobbyCitiesConfigFile.exists()) {
            this.lobbyCitiesConfigFile.getParentFile().mkdirs();
            this.plugin.saveResource(configFileName, false);
        }
        this.lobbyCitiesConfig = new YamlConfiguration();
        try {
            this.lobbyCitiesConfig.load(this.lobbyCitiesConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void loadLobbyCities() {
        for(String city : lobbyCitiesConfig.getKeys(false)) {
            LobbyCity lobbyCity = new LobbyCity.LobbyCityBuilder()
                    .setCity(city)
                    .setCenterLat(Double.parseDouble(lobbyCitiesConfig.getString(String.format("%s.center-latitude", city))))
                    .setCenterLon(Double.parseDouble(lobbyCitiesConfig.getString(String.format("%s.center-longitude", city))))
                    .setRadius(Integer.parseInt(lobbyCitiesConfig.getString(String.format("%s.radius-km", city))))
                    .setWorld(this.plugin.getServer().getWorld(UUID.fromString(lobbyCitiesConfig.getString(String.format("%s.world", city)))))
                    .setX(Integer.parseInt(lobbyCitiesConfig.getString(String.format("%s.x", city))))
                    .setY(Integer.parseInt(lobbyCitiesConfig.getString(String.format("%s.y", city))))
                    .setZ(Integer.parseInt(lobbyCitiesConfig.getString(String.format("%s.z", city))))
                    .build();
            lobbyCities.add(lobbyCity);

            Location armorStandLocation = lobbyCity.getBlock().getLocation();
            armorStandLocation.setX(armorStandLocation.getX() + 0.5);
            armorStandLocation.setZ(armorStandLocation.getZ() + 0.5);
            lobbyCity.getBlock().getWorld()
                    .getNearbyEntities(armorStandLocation, 0, 1, 0)
                    .stream()
                    .filter(entity -> entity instanceof ArmorStand)
                    .forEach(Entity::remove);

            ArmorStand armorStand = (ArmorStand) armorStandLocation.getWorld().spawnEntity(armorStandLocation, EntityType.ARMOR_STAND);
            armorStand.setGravity(false);
            armorStand.setCanPickupItems(false);
            armorStand.setCustomNameVisible(true);
            armorStand.setVisible(false);
            armorStand.setCustomName(ChatColor.GOLD + "" + ChatColor.BOLD + city);
        }
    }

    public void register(LobbyCity lobbyCity) {
        this.lobbyCitiesConfig.set(lobbyCity.getCity() + ".center-latitude", lobbyCity.getCenterLat());
        this.lobbyCitiesConfig.set(lobbyCity.getCity() + ".center-longitude", lobbyCity.getCenterLon());
        this.lobbyCitiesConfig.set(lobbyCity.getCity() + ".radius-km", lobbyCity.getRadius());
        this.lobbyCitiesConfig.set(lobbyCity.getCity() + ".x", lobbyCity.getBlock().getX());
        this.lobbyCitiesConfig.set(lobbyCity.getCity() + ".y", lobbyCity.getBlock().getY());
        this.lobbyCitiesConfig.set(lobbyCity.getCity() + ".z", lobbyCity.getBlock().getZ());
        this.lobbyCitiesConfig.set(lobbyCity.getCity() + ".world", lobbyCity.getBlock().getWorld().getUID().toString());
        try {
            this.lobbyCitiesConfig.save(this.lobbyCitiesConfigFile);
            this.lobbyCities.add(lobbyCity);
            Location armorStandLocation = lobbyCity.getBlock().getLocation();
            armorStandLocation.setX(armorStandLocation.getX() + 0.5);
            armorStandLocation.setZ(armorStandLocation.getZ() + 0.5);
            ArmorStand armorStand = (ArmorStand) armorStandLocation.getWorld().spawnEntity(armorStandLocation, EntityType.ARMOR_STAND);
            armorStand.setGravity(false);
            armorStand.setCanPickupItems(false);
            armorStand.setCustomNameVisible(true);
            armorStand.setVisible(false);
            armorStand.setCustomName(ChatColor.GOLD + "" + ChatColor.BOLD + lobbyCity.getCity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unregister(LobbyCity lobbyCity) {
        this.lobbyCitiesConfig.set(lobbyCity.getCity(), null);
        try {
            this.lobbyCitiesConfig.save(this.lobbyCitiesConfigFile);
            this.lobbyCities.remove(lobbyCity);
            Location armorStandLocation = lobbyCity.getBlock().getLocation();
            armorStandLocation.setX(armorStandLocation.getX() + 0.5);
            armorStandLocation.setZ(armorStandLocation.getZ() + 0.5);
            lobbyCity.getBlock().getWorld()
                    .getNearbyEntities(armorStandLocation, 0, 1, 0)
                    .stream()
                    .filter(entity -> entity instanceof ArmorStand)
                    .forEach(Entity::remove);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean doesExist(String city) {
        return this.lobbyCitiesConfig.getKeys(false).contains(city);
    }

    public Set<LobbyCity> getLobbyCities() {
        return lobbyCities;
    }

}
