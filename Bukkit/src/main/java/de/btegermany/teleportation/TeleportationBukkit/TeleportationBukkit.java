package de.btegermany.teleportation.TeleportationBukkit;

import com.tchristofferson.pagedinventories.PagedInventoryAPI;
import de.btegermany.teleportation.TeleportationBukkit.commands.LobbyWarpCommand;
import de.btegermany.teleportation.TeleportationBukkit.commands.WarpCommand;
import de.btegermany.teleportation.TeleportationBukkit.listener.PlayerInteractListener;
import de.btegermany.teleportation.TeleportationBukkit.listener.PluginMsgListener;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.listener.PlayerJoinListener;
import de.btegermany.teleportation.TeleportationBukkit.message.BukkitPlayersMessage;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.tp.TeleportationHandler;
//import li.cinnazeyy.langlibs.core.Language;
//import li.cinnazeyy.langlibs.core.file.LanguageFile;
//import li.cinnazeyy.langlibs.core.file.YamlFileFactory;
//import li.cinnazeyy.langlibs.core.language.LangLibAPI;
//import li.cinnazeyy.langlibs.core.language.LanguageUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TeleportationBukkit extends JavaPlugin {

	public static final String PLUGIN_CHANNEL = "bungeecord:btegtp";
	private static PagedInventoryAPI pagedInventoryAPI;
	private PluginMessenger pluginMessenger;
	private ScheduledExecutorService scheduledExecutorServiceProxyPlayerSynchronization;

	@Override
	public void onEnable() {
		/*YamlFileFactory.registerPlugin(this);
		LangLibAPI.register(this, new LanguageFile[] {
				new LanguageFile(Language.de_DE, 1.0),
				new LanguageFile(Language.en_GB, 1.0)
		});*/

		//initialize objects
		pagedInventoryAPI = new PagedInventoryAPI(this);
		RegistriesProvider registriesProvider = new RegistriesProvider(this);
		registriesProvider.getLobbyCitiesRegistry().loadLobbyCities();
		this.pluginMessenger = new PluginMessenger(this, registriesProvider);
		TeleportationHandler teleportationHandler = new TeleportationHandler(this.pluginMessenger);
		//LanguageUtil languageUtil = new LanguageUtil(this);

		//register plugin channel
		this.getServer().getMessenger().registerIncomingPluginChannel(this, PLUGIN_CHANNEL, new PluginMsgListener(teleportationHandler, this.pluginMessenger, registriesProvider));
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, PLUGIN_CHANNEL);

		// register listeners
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(teleportationHandler, this.pluginMessenger), this);
		this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(registriesProvider), this);

		// register commands
		this.getCommand("warp").setExecutor(new WarpCommand(this.pluginMessenger, registriesProvider));
		this.getCommand("lobbywarp").setExecutor(new LobbyWarpCommand(this.pluginMessenger, registriesProvider));

		this.startProxyPlayerSynchronization();
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
