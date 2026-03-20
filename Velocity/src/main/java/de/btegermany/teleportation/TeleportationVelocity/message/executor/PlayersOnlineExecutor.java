package de.btegermany.teleportation.TeleportationVelocity.message.executor;

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoData;
import de.btegermany.teleportation.TeleportationAPI.message.executor.PluginMessageNormalExecutor;
import de.btegermany.teleportation.TeleportationVelocity.util.Utils;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

public class PlayersOnlineExecutor implements PluginMessageNormalExecutor {

    private final ProxyServer proxyServer;
    private final GeoData geoData;

    public PlayersOnlineExecutor(ProxyServer proxyServer, GeoData geoData) {
        this.proxyServer = proxyServer;
        this.geoData = geoData;
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
                    double[] coords = GeoData.BTE_GENERATOR_SETTINGS.projection().toGeo(x, z);
                    Optional<RegisteredServer> expectedServerOptional = this.geoData.getServerFromLocationCheck(coords[1], coords[0], player);

                    if (expectedServerOptional.isEmpty()) {
                        sendToLobby.accept(player);
                        return;
                    }

                    if (currentServer.get().getServer().equals(expectedServerOptional.get())) {
                        return;
                    }

                    sendMessage(player, Component.text("Diese Region liegt auf einem anderen Server, du wirst daher mit dem richtigen Server verbunden.", NamedTextColor.GOLD));
                    this.proxyServer.getCommandManager().executeAsync(player, "tpll %f %f yaw=%f pitch=%f".formatted(coords[1], coords[0], yaw, pitch));
                } catch (OutOfProjectionBoundsException ignored) {
                    sendToLobby.accept(player);
                }
            });
        }
    }

}
