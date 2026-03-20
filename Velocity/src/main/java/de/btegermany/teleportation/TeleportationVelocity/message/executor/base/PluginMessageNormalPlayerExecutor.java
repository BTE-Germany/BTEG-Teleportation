package de.btegermany.teleportation.TeleportationVelocity.message.executor.base;

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.btegermany.teleportation.TeleportationAPI.message.executor.PluginMessageNormalExecutor;

import java.util.UUID;

public abstract class PluginMessageNormalPlayerExecutor implements PluginMessageNormalExecutor {

    protected final ProxyServer proxyServer;

    public PluginMessageNormalPlayerExecutor(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void execute(ByteArrayDataInput dataInput) {
        UUID playerUUID = UUID.fromString(dataInput.readUTF());

        this.proxyServer.getPlayer(playerUUID).ifPresent(player -> {
            this.execute(dataInput, player);
        });
    }

    public abstract void execute(ByteArrayDataInput dataInput, Player player);

}
