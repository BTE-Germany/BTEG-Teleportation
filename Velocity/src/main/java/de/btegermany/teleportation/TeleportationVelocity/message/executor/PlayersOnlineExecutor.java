package de.btegermany.teleportation.TeleportationVelocity.message.executor;

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoData;
import de.btegermany.teleportation.TeleportationAPI.message.executor.PluginMessageNormalExecutor;
import de.btegermany.teleportation.TeleportationVelocity.util.Utils;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

public class PlayersOnlineExecutor implements PluginMessageNormalExecutor {

    private static final double BLOCK_DIAGONAL = Math.sqrt(1 + 1);
    private final ProxyServer proxyServer;
    private final GeoData geoData;
    private final Logger logger;

    public PlayersOnlineExecutor(ProxyServer proxyServer, GeoData geoData, Logger logger) {
        this.proxyServer = proxyServer;
        this.geoData = geoData;
        this.logger = logger;
    }

    @Override
    public void execute(ByteArrayDataInput dataInput) {
        JSONArray jsonArray = new JSONArray(dataInput.readUTF());

        Consumer<Player> sendToLobby = player -> {
            Optional<RegisteredServer> lobbyServerOptional = proxyServer.getServer("Lobby-1");
            if (lobbyServerOptional.isPresent()) {
                sendMessage(player, Component.text("You are outside the countries of our build teams. You will be redirected to the lobby.", NamedTextColor.GOLD));
                Utils.connectIfOnline(player, lobbyServerOptional.get());
            }
        };

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject playerObject = jsonArray.getJSONObject(i);
            UUID playerUUID = UUID.fromString(playerObject.getString("player_uuid"));

            this.proxyServer.getPlayer(playerUUID).ifPresent(player -> {
                double x = playerObject.getDouble("x");
                double z = playerObject.getDouble("z");
                float yaw = playerObject.getFloat("yaw");
                float pitch = playerObject.getFloat("pitch");
                String world = playerObject.getString("world");

                Optional<ServerConnection> currentServer = player.getCurrentServer();
                // make sure coordinates on e.g. plot server or normen don't make player switch to the coordinates' terra server
                boolean isEarthServer = currentServer.isPresent() && this.geoData.getGeoServers().stream().anyMatch(geoServer -> currentServer.get().getServer().equals(geoServer.server()) && geoServer.isEarthServer());
                boolean isInTerraWorld = world.equals(Utils.WORLD_TERRA);
                boolean hasAutoSwitchPerm = player.hasPermission("teleportation.autoserverswitch");
                if (!isEarthServer || !isInTerraWorld || !hasAutoSwitchPerm) {
                    return;
                }

                try {
                    Optional<RegisteredServer> expectedServerOptional = this.getServerWithTolerance(x, z, player);

                    if (expectedServerOptional.isEmpty()) {
                        this.logger.info("autoserverswitch sending player {} to lobby. Reason: NoServerForLocation (x={}, z={}, coords={})", player.getUsername(), x, z, Arrays.toString(mcToCoords(x, z)));
                        sendToLobby.accept(player);
                        return;
                    }

                    if (currentServer.get().getServer().equals(expectedServerOptional.get())) {
                        return;
                    }

                    sendMessage(player, Component.text("Diese Region liegt auf einem anderen Server, du wirst daher mit dem richtigen Server verbunden.", NamedTextColor.GOLD));
                    double[] coords = mcToCoords(x, z);
                    this.proxyServer.getCommandManager().executeAsync(player, "tpll %f %f yaw=%f pitch=%f".formatted(coords[1], coords[0], yaw, pitch));
                } catch (OutOfProjectionBoundsException e) {
                    this.logger.info("autoserverswitch sending player {} to lobby. Reason: OutOfProjectionBoundsException (x={}, z={})", player.getUsername(), x, z, e);
                    sendToLobby.accept(player);
                }
            });
        }
    }

    private Optional<RegisteredServer> getServerWithTolerance(double x, double z, Player player) throws OutOfProjectionBoundsException {
        int toleranceDiff = TeleportationVelocity.getCountryBorderTolerance() - 1;
        Optional<RegisteredServer> serverPlayerPosOptional = this.getServerForMc(x, z, player);

        // player within the border
        if (serverPlayerPosOptional.isPresent()) {
            return serverPlayerPosOptional;
        }

        // player outside, x or z axis
        // x +-
        Optional<RegisteredServer> serverPlusXOptional = this.getServerForMc(x + toleranceDiff, z, player);
        if (serverPlusXOptional.isPresent()) {
            return serverPlusXOptional;
        }

        Optional<RegisteredServer> serverMinusXOptional = this.getServerForMc(x - toleranceDiff, z, player);
        if (serverMinusXOptional.isPresent()) {
            return serverMinusXOptional;
        }

        // z +-
        Optional<RegisteredServer> serverPlusZOptional = this.getServerForMc(x, z + toleranceDiff, player);
        if (serverPlusZOptional.isPresent()) {
            return serverPlusZOptional;
        }

        Optional<RegisteredServer> serverMinusZOptional = this.getServerForMc(x, z - toleranceDiff, player);
        if (serverMinusZOptional.isPresent()) {
            return serverMinusZOptional;
        }


        // 45°
        int blocksDiff = (int) Math.floor(toleranceDiff / BLOCK_DIAGONAL);
        // x +- +45°
        Optional<RegisteredServer> serverPlusX45Optional = this.getServerForMc(x + blocksDiff, z - blocksDiff, player);
        if (serverPlusX45Optional.isPresent()) {
            return serverPlusX45Optional;
        }

        Optional<RegisteredServer> serverMinusX45Optional = this.getServerForMc(x - blocksDiff, z + blocksDiff, player);
        if (serverMinusX45Optional.isPresent()) {
            return serverMinusX45Optional;
        }

        // z +- +45°
        Optional<RegisteredServer> serverPlusZ45Optional = this.getServerForMc(x + blocksDiff, z + blocksDiff, player);
        if (serverPlusZ45Optional.isPresent()) {
            return serverPlusZ45Optional;
        }

        return this.getServerForMc(x - blocksDiff, z - blocksDiff, player);
    }

    private Optional<RegisteredServer> getServerForMc(double x, double z, Player player) throws OutOfProjectionBoundsException {
        double[] coords = mcToCoords(x, z);
        return this.geoData.getServerFromLocationCheck(coords[1], coords[0], player);
    }

    private double[] mcToCoords(double x, double z) throws OutOfProjectionBoundsException {
        return GeoData.BTE_GENERATOR_SETTINGS.projection().toGeo(x, z);
    }

}
