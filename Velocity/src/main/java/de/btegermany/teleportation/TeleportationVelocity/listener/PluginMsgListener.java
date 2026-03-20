package de.btegermany.teleportation.TeleportationVelocity.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity;
import de.btegermany.teleportation.TeleportationVelocity.data.Database;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoData;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.message.executor.*;
import de.btegermany.teleportation.TeleportationAPI.message.executor.PluginMessageExecutor;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.util.WarpIdsManager;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PluginMsgListener {

    private final RegistriesProvider registriesProvider;
    private final Map<String, PluginMessageExecutor> messageExecutors;

    public PluginMsgListener(PluginMessenger pluginMessenger, Database database, RegistriesProvider registriesProvider, ProxyServer proxyServer, Logger logger, WarpIdsManager warpIdsManager, GeoData geoData) {
        this.registriesProvider = registriesProvider;

        this.messageExecutors = Map.of(
                "gui_data_request", new GuiDataExecutor(registriesProvider, pluginMessenger, proxyServer),
                "execute_command", new ExecuteCommandExecutor(proxyServer, logger),
                "warp_delete", new WarpExecutor.DeleteExecutor(proxyServer, registriesProvider.getWarpsRegistry(), database, logger),
                "warp_create", new WarpExecutor.CreateExecutor(proxyServer, registriesProvider.getWarpsRegistry(), warpIdsManager),
                "warp_change", new WarpExecutor.ChangeExecutor(proxyServer, registriesProvider.getWarpsRegistry(), database, logger),
                "players_online", new PlayersOnlineExecutor(proxyServer, geoData),
                "tp_random_warp", new TpRandomWarpExecutor(proxyServer, registriesProvider.getWarpsRegistry()),
                "tag_add", new TagExecutor.AddExecutor(proxyServer, registriesProvider),
                "tag_remove", new TagExecutor.RemoveExecutor(proxyServer, registriesProvider),
                "tag_edit", new TagExecutor.EditExecutor(proxyServer, registriesProvider)
        );
    }

    @Subscribe
    public void onPluginMessageFromBackend(PluginMessageEvent event) {
        if (!(event.getSource() instanceof ServerConnection) || !event.getIdentifier().equals(TeleportationVelocity.PLUGIN_CHANNEL)) {
            return;
        }

        ByteArrayDataInput dataInput = ByteStreams.newDataInput(event.getData());

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
