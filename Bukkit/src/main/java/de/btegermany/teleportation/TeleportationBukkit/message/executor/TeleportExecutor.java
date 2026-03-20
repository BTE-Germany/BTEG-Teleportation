package de.btegermany.teleportation.TeleportationBukkit.message.executor;

import com.google.common.io.ByteArrayDataInput;
import de.btegermany.teleportation.TeleportationAPI.message.executor.PluginMessageNormalExecutor;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.tp.PendingTpLocation;
import de.btegermany.teleportation.TeleportationBukkit.tp.PendingTpNormen;
import de.btegermany.teleportation.TeleportationBukkit.tp.PendingTpPlayer;
import de.btegermany.teleportation.TeleportationBukkit.tp.TeleportationHandler;

import java.util.Optional;
import java.util.UUID;

public class TeleportExecutor {

    public static class PlayerExecutor implements PluginMessageNormalExecutor {

        private final TeleportationHandler teleportationHandler;
        private final TeleportationBukkit plugin;

        public PlayerExecutor(TeleportationHandler teleportationHandler, TeleportationBukkit plugin) {
            this.teleportationHandler = teleportationHandler;
            this.plugin = plugin;
        }

        @Override
        public void execute(ByteArrayDataInput dataInput) {
            UUID playerUUID = UUID.fromString(dataInput.readUTF());
            UUID targetUUID = UUID.fromString(dataInput.readUTF());
            String originServerName = dataInput.readUTF();

            this.teleportationHandler.handle(new PendingTpPlayer(playerUUID, targetUUID, originServerName, this.plugin));
        }

    }

    public static class CoordsExecutor implements PluginMessageNormalExecutor {

        private final TeleportationHandler teleportationHandler;
        private final TeleportationBukkit plugin;

        public CoordsExecutor(TeleportationHandler teleportationHandler, TeleportationBukkit plugin) {
            this.teleportationHandler = teleportationHandler;
            this.plugin = plugin;
        }

        @Override
        public void execute(ByteArrayDataInput dataInput) {
            UUID playerUUID = UUID.fromString(dataInput.readUTF());
            String[] coords = dataInput.readUTF().split(",");
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);
            double z = Double.parseDouble(coords[2]);
            // default value null. Otherwise, use the set float value
            Float yaw = Optional.of(dataInput.readUTF()).filter(str -> !str.equals("null")).map(Float::parseFloat).orElse(null);
            Float pitch = Optional.of(dataInput.readUTF()).filter(str -> !str.equals("null")).map(Float::parseFloat).orElse(null);
            String worldName = Optional.of(dataInput.readUTF()).filter(str -> !str.equals("null")).orElse(null);
            String originServerName = dataInput.readUTF();

            this.teleportationHandler.handle(new PendingTpLocation(playerUUID, x, y, z, yaw, pitch, worldName, originServerName, this.plugin));
        }

    }

    public static class NormenExecutor implements PluginMessageNormalExecutor {

        private final TeleportationHandler teleportationHandler;
        private final TeleportationBukkit plugin;

        public NormenExecutor(TeleportationHandler teleportationHandler, TeleportationBukkit plugin) {
            this.teleportationHandler = teleportationHandler;
            this.plugin = plugin;
        }

        @Override
        public void execute(ByteArrayDataInput dataInput) {
            UUID playerUUID = UUID.fromString(dataInput.readUTF());
            String normenWorld = dataInput.readUTF();
            float yaw = Float.parseFloat(dataInput.readUTF());
            float pitch = Float.parseFloat(dataInput.readUTF());
            String originServerName = dataInput.readUTF();

            this.teleportationHandler.handle(new PendingTpNormen(playerUUID, normenWorld, originServerName, yaw, pitch, this.plugin));
        }

    }

}
