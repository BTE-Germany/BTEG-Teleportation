package de.btegermany.teleportation.TeleportationBukkit.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import de.btegermany.teleportation.TeleportationAPI.message.executor.PluginMessageExecutor;
import de.btegermany.teleportation.TeleportationBukkit.message.executor.CommandPerformExecutor;
import de.btegermany.teleportation.TeleportationBukkit.message.executor.ListExecutor;
import de.btegermany.teleportation.TeleportationBukkit.message.executor.RequestExecutor;
import de.btegermany.teleportation.TeleportationBukkit.message.executor.TeleportExecutor;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.tp.TeleportationHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PluginMsgListener implements PluginMessageListener {

	private final RegistriesProvider registriesProvider;
	private final Map<String, PluginMessageExecutor> messageExecutors;

	public PluginMsgListener(TeleportationBukkit plugin, TeleportationHandler teleportationHandler, PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {
		this.registriesProvider = registriesProvider;

		this.messageExecutors = Map.of(
				"teleport_player", new TeleportExecutor.PlayerExecutor(teleportationHandler, plugin),
				"teleport_coords", new TeleportExecutor.CoordsExecutor(teleportationHandler, plugin),
				"teleport_normen", new TeleportExecutor.NormenExecutor(teleportationHandler, plugin),
				"command_perform", new CommandPerformExecutor(plugin),
				"list_cities", new ListExecutor.CitiesExecutor(registriesProvider.getCitiesRegistry()),
				"list_tags", new ListExecutor.TagsExecutor(registriesProvider.getWarpTagsRegistry()),
				"last_location_request", new RequestExecutor.LastLocationExecutor(pluginMessenger),
				"player_world_request", new RequestExecutor.PlayerWorldExecutor(pluginMessenger)
		);
	}

	@Override
	public void onPluginMessageReceived(@Nonnull String channel, @Nonnull Player player, @Nonnull byte[] message) {
		if(!channel.equals(TeleportationBukkit.PLUGIN_CHANNEL)) {
			return;
		}

		ByteArrayDataInput dataInput = ByteStreams.newDataInput(message);

		PluginMessage.MessageType messageType = PluginMessage.MessageType.valueOf(dataInput.readUTF());
		Integer requestId = (messageType == PluginMessage.MessageType.NORMAL) ? null : Integer.parseInt(dataInput.readUTF());

		String messageLabel = dataInput.readUTF();

		CompletableFuture.runAsync(() -> {
			switch (messageType) {
				case NORMAL, WITH_RESPONSE -> this.messageExecutors.get(messageLabel).execute(dataInput, requestId);

				case RESPONSE -> {
					this.registriesProvider.getPluginMessagesWithResponseRegistry().getPluginMessageWithResponse(requestId).accept(dataInput);
					this.registriesProvider.getPluginMessagesWithResponseRegistry().unregister(requestId);
				}
			}
		});
	}

}
