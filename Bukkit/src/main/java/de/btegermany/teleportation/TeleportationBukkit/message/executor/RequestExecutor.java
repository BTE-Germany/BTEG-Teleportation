package de.btegermany.teleportation.TeleportationBukkit.message.executor;

import com.google.common.io.ByteArrayDataInput;
import de.btegermany.teleportation.TeleportationAPI.message.executor.PluginMessageWithResponseExecutor;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.message.response.LastLocationResponseMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.response.PlayerWorldResponseMessage;

import java.util.UUID;

public class RequestExecutor {

    public static class LastLocationExecutor implements PluginMessageWithResponseExecutor {

        private final PluginMessenger pluginMessenger;

        public LastLocationExecutor(PluginMessenger pluginMessenger) {
            this.pluginMessenger = pluginMessenger;
        }

        @Override
        public void execute(ByteArrayDataInput dataInput, int requestId) {
            UUID playerUUID = UUID.fromString(dataInput.readUTF());
            this.pluginMessenger.send(new LastLocationResponseMessage(requestId, playerUUID));
        }

    }

    public static class PlayerWorldExecutor implements PluginMessageWithResponseExecutor {

        private final PluginMessenger pluginMessenger;

        public PlayerWorldExecutor(PluginMessenger pluginMessenger) {
            this.pluginMessenger = pluginMessenger;
        }

        @Override
        public void execute(ByteArrayDataInput dataInput, int requestId) {
            UUID playerUUID = UUID.fromString(dataInput.readUTF());
            this.pluginMessenger.send(new PlayerWorldResponseMessage(requestId, playerUUID));
        }

    }

}
