package de.btegermany.teleportation.TeleportationBukkit;

import de.btegermany.teleportation.TeleportationBukkit.commands.LobbyWarpCommand;
import de.btegermany.teleportation.TeleportationBukkit.commands.TpCommand;
import de.btegermany.teleportation.TeleportationBukkit.commands.WarpCommand;
import de.btegermany.teleportation.TeleportationBukkit.data.ConfigReader;
import de.btegermany.teleportation.TeleportationBukkit.gui.PagedGuiHandler;
import de.btegermany.teleportation.TeleportationBukkit.listener.PlayerInteractListener;
import de.btegermany.teleportation.TeleportationBukkit.listener.PluginMsgListener;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.listener.PlayerJoinListener;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.tp.TeleportationHandler;
//import li.cinnazeyy.langlibs.core.Language;
//import li.cinnazeyy.langlibs.core.file.LanguageFile;
//import li.cinnazeyy.langlibs.core.file.YamlFileFactory;
//import li.cinnazeyy.langlibs.core.language.LangLibAPI;
//import li.cinnazeyy.langlibs.core.language.LanguageUtil;
import de.btegermany.teleportation.TeleportationBukkit.util.PlayersOnlineSynchronizer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class TeleportationBukkit extends JavaPlugin {

	public static final String PLUGIN_CHANNEL = "bteg:teleportation";
	private PlayersOnlineSynchronizer playersOnlineSynchronizer;

	@Override
	public void onEnable() {
		/*YamlFileFactory.registerPlugin(this);
		LangLibAPI.register(this, new LanguageFile[] {
				new LanguageFile(Language.de_DE, 1.0),
				new LanguageFile(Language.en_GB, 1.0)
		});*/

		//initialize objects
		ConfigReader configReader = new ConfigReader(this);
		RegistriesProvider registriesProvider = new RegistriesProvider(this);
		registriesProvider.getLobbyCitiesRegistry().loadLobbyCities();
		PluginMessenger pluginMessenger = new PluginMessenger(this, registriesProvider);
		TeleportationHandler teleportationHandler = new TeleportationHandler();
		PagedGuiHandler pagedGuiHandler = new PagedGuiHandler(pluginMessenger);
		//LanguageUtil languageUtil = new LanguageUtil(this);

		//register plugin channel
		this.getServer().getMessenger().registerIncomingPluginChannel(this, PLUGIN_CHANNEL, new PluginMsgListener(this, teleportationHandler, pluginMessenger, registriesProvider));
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, PLUGIN_CHANNEL);

		// register listeners
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, teleportationHandler), this);
		this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(registriesProvider), this);

		// register commands
		Objects.requireNonNull(this.getCommand("warp")).setExecutor(new WarpCommand(pluginMessenger, registriesProvider, configReader, pagedGuiHandler, this));
		Objects.requireNonNull(this.getCommand("lobbywarp")).setExecutor(new LobbyWarpCommand(pluginMessenger, registriesProvider, pagedGuiHandler, this));
		Objects.requireNonNull(this.getCommand("tp")).setExecutor(new TpCommand(pluginMessenger));

		this.playersOnlineSynchronizer = new PlayersOnlineSynchronizer(pluginMessenger, this);
		this.playersOnlineSynchronizer.startProxyPlayerSynchronization();
	}

	@Override
	public void onDisable() {
		this.playersOnlineSynchronizer.shutdownNow();
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

}
