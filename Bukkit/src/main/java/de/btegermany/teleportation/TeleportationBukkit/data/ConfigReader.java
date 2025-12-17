package de.btegermany.teleportation.TeleportationBukkit.data;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigReader {

    private static final String CONFIG = "config.yaml";
    private final TeleportationBukkit plugin;

    public ConfigReader(TeleportationBukkit plugin) {
        this.plugin = plugin;
    }

    public boolean readWarpsEnabled() {
        File file = new File(this.plugin.getDataFolder(), CONFIG);
        if (!file.exists()) {
            this.plugin.saveResource(CONFIG, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        return config.getBoolean("warps.enabled", true);
    }
}
