package de.btegermany.teleportation.TeleportationVelocity.message.executor.base;

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.btegermany.teleportation.TeleportationAPI.message.executor.PluginMessageWithResponseExecutor;

import java.util.UUID;

public abstract class PluginMessageWithResponsePlayerExecutor implements PluginMessageWithResponseExecutor {

    protected final ProxyServer proxyServer;

    public PluginMessageWithResponsePlayerExecutor(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void execute(ByteArrayDataInput dataInput, int requestId) {
        UUID playerUUID = UUID.fromString(dataInput.readUTF());

        this.proxyServer.getPlayer(playerUUID).ifPresent(player -> {
            this.execute(dataInput, requestId, player);
        });
    }

    public abstract void execute(ByteArrayDataInput dataInput, int responseId, Player player);

}
