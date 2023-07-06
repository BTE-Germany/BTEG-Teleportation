package de.btegermany.teleportation.TeleportationBukkit;

import com.tchristofferson.pagedinventories.PagedInventoryAPI;
import de.btegermany.teleportation.TeleportationBukkit.commands.WarpCommand;
import de.btegermany.teleportation.TeleportationBukkit.listener.PluginMsgListener;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.listener.AsyncPlayerChatListener;
import de.btegermany.teleportation.TeleportationBukkit.listener.PlayerJoinListener;
import de.btegermany.teleportation.TeleportationBukkit.message.BukkitPlayersMessage;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.tp.TeleportationHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TeleportationBukkit extends JavaPlugin {

	public static String PLUGIN_CHANNEL = "bungeecord:btegtp";
	private static PagedInventoryAPI pagedInventoryAPI;
	private PluginMessenger pluginMessenger;
	private ScheduledExecutorService scheduledExecutorServiceProxyPlayerSynchronization;

	@Override
	public void onEnable() {
		pagedInventoryAPI = new PagedInventoryAPI(this);
		this.pluginMessenger = new PluginMessenger(this);
		RegistriesProvider registriesProvider = new RegistriesProvider();

		TeleportationHandler teleportationHandler = new TeleportationHandler(this.pluginMessenger);
		this.getServer().getMessenger().registerIncomingPluginChannel(this, PLUGIN_CHANNEL, new PluginMsgListener(teleportationHandler, this.pluginMessenger, registriesProvider));
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, PLUGIN_CHANNEL);
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(teleportationHandler, this.pluginMessenger), this);
		this.getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(this.pluginMessenger, registriesProvider), this);
		this.getCommand("nwarp").setExecutor(new WarpCommand(this.pluginMessenger, registriesProvider));

		startProxyPlayerSynchronization();
	}

	@Override
	public void onDisable() {
		this.scheduledExecutorServiceProxyPlayerSynchronization.shutdownNow();
	}

	public static String getFormattedMessage(String text) {
		String[] words = text.split(" ");
		StringBuilder builder = new StringBuilder("§b§lBTEG §7»");
		for(String word : words) {
			builder.append(" §6").append(word);
		}
		return new String(builder);
	}

	public static String getFormattedErrorMessage(String text) {
		String[] words = text.split(" ");
		StringBuilder builder = new StringBuilder("§b§lBTEG §7»");
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
