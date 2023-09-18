package de.btegermany.teleportation.TeleportationBukkit;

import com.tchristofferson.pagedinventories.PagedInventoryAPI;
import de.btegermany.teleportation.TeleportationBukkit.commands.LobbyWarpCommand;
import de.btegermany.teleportation.TeleportationBukkit.commands.WarpCommand;
import de.btegermany.teleportation.TeleportationBukkit.listener.PlayerInteractListener;
import de.btegermany.teleportation.TeleportationBukkit.listener.PluginMsgListener;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.listener.AsyncPlayerChatListener;
import de.btegermany.teleportation.TeleportationBukkit.listener.PlayerJoinListener;
import de.btegermany.teleportation.TeleportationBukkit.message.BukkitPlayersMessage;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.tp.TeleportationHandler;
import de.btegermany.teleportation.TeleportationBukkit.util.LobbyCity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TeleportationBukkit extends JavaPlugin {

	public static String PLUGIN_CHANNEL = "bungeecord:btegtp";
	private static PagedInventoryAPI pagedInventoryAPI;
	private PluginMessenger pluginMessenger;
	private ScheduledExecutorService scheduledExecutorServiceProxyPlayerSynchronization;

	//TODO: temp
	public static Set<LobbyCity> lobbyCities = new HashSet<>();

	@Override
	public void onEnable() {
		pagedInventoryAPI = new PagedInventoryAPI(this);
		this.pluginMessenger = new PluginMessenger(this);
		RegistriesProvider registriesProvider = new RegistriesProvider();

		File lobbyCitiesConfigFile = new File(this.getDataFolder(), "lobbycities.yml");
		if(!lobbyCitiesConfigFile.exists()) {
			lobbyCitiesConfigFile.getParentFile().mkdirs();
			this.saveResource("lobbycities.yml", false);
		}
		FileConfiguration lobbyCitiesConfig = new YamlConfiguration();
		try {
			lobbyCitiesConfig.load(lobbyCitiesConfigFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		for(String city : lobbyCitiesConfig.getKeys(false)) {
			LobbyCity lobbyCity = new LobbyCity.LobbyCityBuilder()
					.setCity(city)
					.setCenterLat(Double.parseDouble(lobbyCitiesConfig.getString(String.format("%s.center-latitude", city))))
					.setCenterLon(Double.parseDouble(lobbyCitiesConfig.getString(String.format("%s.center-longitude", city))))
					.setRadius(Integer.parseInt(lobbyCitiesConfig.getString(String.format("%s.radius-km", city))))
					.setWorld(this.getServer().getWorld(UUID.fromString(lobbyCitiesConfig.getString(String.format("%s.world", city)))))
					.setX(Integer.parseInt(lobbyCitiesConfig.getString(String.format("%s.x", city))))
					.setY(Integer.parseInt(lobbyCitiesConfig.getString(String.format("%s.y", city))))
					.setZ(Integer.parseInt(lobbyCitiesConfig.getString(String.format("%s.z", city))))
					.build();
			lobbyCities.add(lobbyCity);

			Location armorStandLocation = lobbyCity.getBlock().getLocation();
			armorStandLocation.setX(armorStandLocation.getX() + 0.5);
			armorStandLocation.setZ(armorStandLocation.getZ() + 0.5);
			for(Entity entity : lobbyCity.getBlock().getWorld().getNearbyEntities(armorStandLocation, 0, 1, 0)) {
				if(!(entity instanceof ArmorStand)) {
					continue;
				}
				entity.remove();
			}

			ArmorStand armorStand = (ArmorStand) armorStandLocation.getWorld().spawnEntity(armorStandLocation, EntityType.ARMOR_STAND);
			armorStand.setGravity(false);
			armorStand.setCanPickupItems(false);
			armorStand.setCustomNameVisible(true);
			armorStand.setVisible(false);
			armorStand.setCustomName(ChatColor.GOLD + "" + ChatColor.BOLD + city);
		}

		TeleportationHandler teleportationHandler = new TeleportationHandler(this.pluginMessenger);
		this.getServer().getMessenger().registerIncomingPluginChannel(this, PLUGIN_CHANNEL, new PluginMsgListener(teleportationHandler, this.pluginMessenger, registriesProvider));
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, PLUGIN_CHANNEL);
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(teleportationHandler, this.pluginMessenger), this);
		this.getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(this.pluginMessenger, registriesProvider), this);
		this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
		this.getCommand("nwarp").setExecutor(new WarpCommand(this.pluginMessenger, registriesProvider));
		this.getCommand("lobbywarp").setExecutor(new LobbyWarpCommand(this.pluginMessenger, lobbyCitiesConfigFile, lobbyCitiesConfig));

		startProxyPlayerSynchronization();
	}

	@Override
	public void onDisable() {
		this.scheduledExecutorServiceProxyPlayerSynchronization.shutdownNow();
	}

	public static String getFormattedMessage(String text) {
		String[] words = text.split(" ");
		StringBuilder builder = new StringBuilder("ᾠ");
		for(String word : words) {
			builder.append(" §6").append(word);
		}
		return new String(builder);
	}

	public static String getFormattedErrorMessage(String text) {
		String[] words = text.split(" ");
		StringBuilder builder = new StringBuilder("ᾠ");
		for(String word : words) {
			builder.append(" §c").append(word);
		}
		return new String(builder);
	}

	public void startProxyPlayerSynchronization() {
		this.scheduledExecutorServiceProxyPlayerSynchronization = Executors.newSingleThreadScheduledExecutor();
		this.scheduledExecutorServiceProxyPlayerSynchronization.scheduleAtFixedRate(() -> {
			this.pluginMessenger.send(new BukkitPlayersMessage(this.getServer().getOnlinePlayers()));
		}, 0, 1000, TimeUnit.MILLISECONDS);
	}

	public static PagedInventoryAPI getPagedInventoryAPI() {
		return pagedInventoryAPI;
	}
}
