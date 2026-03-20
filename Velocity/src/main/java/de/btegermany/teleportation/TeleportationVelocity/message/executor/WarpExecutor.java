package de.btegermany.teleportation.TeleportationVelocity.message.executor;

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.btegermany.teleportation.TeleportationAPI.State;
import de.btegermany.teleportation.TeleportationVelocity.data.Database;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoData;
import de.btegermany.teleportation.TeleportationVelocity.message.executor.base.PluginMessageNormalPlayerExecutor;
import de.btegermany.teleportation.TeleportationVelocity.registry.WarpsRegistry;
import de.btegermany.teleportation.TeleportationVelocity.util.Warp;
import de.btegermany.teleportation.TeleportationVelocity.util.WarpIdsManager;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

public class WarpExecutor {

    public static class CreateExecutor extends PluginMessageNormalPlayerExecutor {

        private final WarpsRegistry registry;
        private final WarpIdsManager warpIdsManager;

        public CreateExecutor(ProxyServer proxyServer, WarpsRegistry registry, WarpIdsManager warpIdsManager) {
            super(proxyServer);
            this.registry = registry;
            this.warpIdsManager = warpIdsManager;
        }

        @Override
        public void execute(ByteArrayDataInput dataInput, Player player) {
            String name = dataInput.readUTF();
            String city = dataInput.readUTF();
            String state = dataInput.readUTF();
            double x = Double.parseDouble(dataInput.readUTF());
            double z = Double.parseDouble(dataInput.readUTF());

            double[] coordinates;
            try {
                coordinates = GeoData.BTE_GENERATOR_SETTINGS.projection().toGeo(x, z);
            } catch (OutOfProjectionBoundsException e) {
                sendMessage(player, Component.text("Error: OutOfProjectionBoundsException", NamedTextColor.RED));
                return;
            }

            String headId = dataInput.readUTF();
            float yaw = Float.parseFloat(dataInput.readUTF());
            float pitch = Float.parseFloat(dataInput.readUTF());
            double height = Double.parseDouble(dataInput.readUTF());
            String world = dataInput.readUTF();
            if (headId.equals("null")) headId = null;

            final String headIdFinal = headId;

            this.warpIdsManager.getAndClaimNextIdAsync().thenAccept(warpId -> {
                Warp warp = new Warp(warpId, name, city, state, coordinates[1], coordinates[0], headIdFinal, yaw, pitch, height, world);
                this.registry.registerAsync(warp).thenAccept(success -> {
                    if (success) {
                        sendMessage(player, Component.text("Der Warp wurde mit der Id \"%d\" erstellt!".formatted(warp.getId()), NamedTextColor.GOLD));
                        return;
                    }
                    sendMessage(player, Component.text("Ein Fehler ist aufgetreten. Der Warp konnte nicht erstellt werden.", NamedTextColor.RED));
                });
            });
        }

    }

    public static class DeleteExecutor extends PluginMessageNormalPlayerExecutor {

        private final WarpsRegistry registry;
        private final Database database;
        private final Logger logger;

        public DeleteExecutor(ProxyServer proxyServer, WarpsRegistry registry, Database database, Logger logger) {
            super(proxyServer);
            this.registry = registry;
            this.database = database;
            this.logger = logger;
        }

        @Override
        public void execute(ByteArrayDataInput dataInput, Player player) {
            int id = Integer.parseInt(dataInput.readUTF());

            try {
                PreparedStatement preparedStatement = database.getConnection().prepareStatement("DELETE FROM warps WHERE id = ?");
                preparedStatement.setInt(1, id);
                database.executeUpdateAsync(preparedStatement).thenRun(() -> {
                    this.registry.unregister(id);
                    sendMessage(player, Component.text("Der Warp mit der Id %d wurde gelöscht!".formatted(id)));
                    try {
                        preparedStatement.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (SQLException e) {
                this.logger.error("Deleting warp failed", e);
            }
        }

    }

    public static class ChangeExecutor extends PluginMessageNormalPlayerExecutor {

        private final WarpsRegistry registry;
        private final Database database;
        private final Logger logger;

        public ChangeExecutor(ProxyServer proxyServer, WarpsRegistry registry, Database database, Logger logger) {
            super(proxyServer);
            this.registry = registry;
            this.database = database;
            this.logger = logger;
        }

        @Override
        public void execute(ByteArrayDataInput dataInput, Player player) {
            int id = Integer.parseInt(dataInput.readUTF());
            String column = dataInput.readUTF();
            String valueInputRaw = dataInput.readUTF();

            Warp warp = this.registry.getWarp(id);
            if (warp == null) {
                sendMessage(player, Component.text("Es wurde kein Warp mit der Id %d gefunden.".formatted(id), NamedTextColor.RED));
                return;
            }

            String valueState = "";
            if (column.equals("state")) {
                State stateFromInput = State.getStateFromInput(valueInputRaw);
                if (stateFromInput == null) {
                    sendMessage(player, Component.text("Das Bundesland ist ungültig!", NamedTextColor.RED));
                    return;
                }
                valueState = stateFromInput.displayName;
            }
            String valueInput = valueInputRaw.equals("null") ? null : column.equals("state") ? valueState : valueInputRaw;

            if (column.equals("coordinates")) {
                if (valueInput == null) return;
                String latitudeString = valueInput.split(" ")[0].replace(",", "");
                String longitudeString = valueInput.split(" ")[1];
                try {
                    PreparedStatement preparedStatement = database.getConnection().prepareStatement("UPDATE warps SET latitude = ?, longitude = ? WHERE id = ?");
                    preparedStatement.setString(1, latitudeString);
                    preparedStatement.setString(2, longitudeString);
                    preparedStatement.setInt(3, id);
                    database.executeUpdateAsync(preparedStatement).thenRun(() -> {
                        warp.setLatitude(Double.parseDouble(latitudeString));
                        warp.setLongitude(Double.parseDouble(longitudeString));
                        sendMessage(player, Component.text("Der Warp wurde geändert!", NamedTextColor.GOLD));
                        try {
                            preparedStatement.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (SQLException e) {
                    this.logger.error("Changing warp coordinates failed", e);
                }
                return;
            }

            try {
                PreparedStatement preparedStatement = database.getConnection().prepareStatement("UPDATE warps SET " + column + " = ? WHERE id = ?");
                Object value;
                if ((column.equals("yaw") || column.equals("pitch")) && valueInput != null) {
                    preparedStatement.setFloat(1, Float.parseFloat(valueInput));
                    value = Float.parseFloat(valueInput);
                } else if (column.equals("height") && valueInput != null) {
                    preparedStatement.setDouble(1, Double.parseDouble(valueInput));
                    value = Double.parseDouble(valueInput);
                } else {
                    preparedStatement.setString(1, valueInput);
                    value = valueInput;
                }
                Field field = Warp.class.getDeclaredField(column.equals("head_id") ? "headId" : column);
                field.setAccessible(true);
                field.set(warp, value);
                preparedStatement.setInt(2, id);
                field.setAccessible(false);
                database.executeUpdateAsync(preparedStatement).thenRun(() -> {
                    sendMessage(player, Component.text("Der Warp wurde geändert!", NamedTextColor.GOLD));
                    try {
                        preparedStatement.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
                this.logger.error("Changing warp failed", e);
            }
        }

    }

}