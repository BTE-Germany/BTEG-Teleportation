package de.btegermany.teleportation.TeleportationBungee.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.btegermany.teleportation.TeleportationBungee.BukkitPlayer;
import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoData;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoServer;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.util.Database;
import de.btegermany.teleportation.TeleportationBungee.util.LastLocation;
import de.btegermany.teleportation.TeleportationBungee.util.PluginMessenger;
import de.btegermany.teleportation.TeleportationBungee.util.Warp;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static de.btegermany.teleportation.TeleportationBungee.TeleportationBungee.getFormattedMessage;

public class PluginMsgListener implements Listener {

    private final PluginMessenger pluginMessenger;
    private final Database database;
    private final GeoData geoData;
    private final RegistriesProvider registriesProvider;
    private final Map<UUID, Map<Integer, List<JSONObject>>> cachedGuiData;

    public PluginMsgListener(PluginMessenger pluginMessenger, Database database, GeoData geoData, RegistriesProvider registriesProvider) {
        this.pluginMessenger = pluginMessenger;
        this.database = database;
        this.geoData = geoData;
        this.registriesProvider = registriesProvider;
        this.cachedGuiData = new HashMap<>();
    }
    
    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if(!event.getTag().equals(TeleportationBungee.PLUGIN_CHANNEL)) return;

        ByteArrayDataInput dataInput = ByteStreams.newDataInput(event.getData());
        UUID playerUUID;
        ProxiedPlayer player;
        int id;
        float yaw;
        float pitch;
        double x;
        double y;
        double z;

        switch (dataInput.readUTF()) {

            case "get_gui_data":
                playerUUID = UUID.fromString(dataInput.readUTF());
                String metaTitle = dataInput.readUTF();
                String[] pagesRaw = dataInput.readUTF().split(", ");
                int[] pages = new int[pagesRaw.length];
                for(int i = 0; i < pagesRaw.length; i++) {
                    pages[i] = Integer.parseInt(pagesRaw[i]);
                }
                player = ProxyServer.getInstance().getPlayer(playerUUID);
                if(player == null || !player.isConnected()) return;
                Server server = player.getServer();

                String group = metaTitle.split("_").length > 0 ? metaTitle.split("_")[0] : metaTitle;
                String title = metaTitle.equals(group) ? group : metaTitle.substring(group.length() + 1);

                switch (group) {
                    case "Alle":
                        sendGuiData(player, metaTitle, pages, "SELECT id, name, city, state, latitude, longitude, head_id, yaw, pitch, height FROM warps ORDER BY name");
                        break;
                    case "Städte":
                        sendGuiData(player, metaTitle, pages, "SELECT city AS name, state FROM warps GROUP BY city ORDER BY city");
                        break;
                    case "city":
                        sendGuiData(player, metaTitle, pages, "SELECT id, name, city, state, latitude, longitude, head_id, yaw, pitch, height FROM warps WHERE city = '" + title + "' ORDER BY name");
                        break;
                    case "bl":
                        sendGuiData(player, metaTitle, pages, "SELECT id, name, city, state, latitude, longitude, head_id, yaw, pitch, height FROM warps WHERE state = '" + title + "' ORDER BY name");
                        break;
                    case "server":
                        Optional<GeoServer> optional = geoData.getGeoServers().stream().filter(geoServer -> geoServer.getServerInfo().getName().equals(title)).findFirst();
                        if(!optional.isPresent()) return;
                        GeoServer geoServer = optional.get();
                        if(geoServer.getCities().size() == 0 && geoServer.getStates().size() == 0) return;

                        StringBuilder builder = new StringBuilder();
                        AtomicInteger i = new AtomicInteger(0);
                        geoServer.getStates().forEach(state -> {
                            if(i.getAndIncrement() == 0) {
                                builder.append(" (");
                            } else {
                                builder.append(" OR");
                            }
                            builder.append(" state = '").append(state).append("'");
                        });
                        geoServer.getCities().forEach(city -> {
                            if(i.getAndIncrement() == 0) {
                                builder.append(" (");
                            } else {
                                builder.append(" OR");
                            }
                            builder.append(" city = '").append(city).append("'");
                        });
                        i.set(0);
                        builder.append(")");
                        geoData.getGeoServers().forEach(gs -> {
                            if(!gs.equals(geoServer)) {
                                gs.getCities().forEach(city -> builder.append(" AND city <> '").append(city).append("'"));
                            }
                        });
                        sendGuiData(player, metaTitle, pages, "SELECT id, name, city, state, latitude, longitude, head_id, yaw, pitch, height FROM warps WHERE" + new String(builder) + " ORDER BY name");
                        break;
                }
                break;

            case "get_warp_info":
                playerUUID = UUID.fromString(dataInput.readUTF());
                id = Integer.parseInt(dataInput.readUTF());
                int responseNumber = Integer.parseInt(dataInput.readUTF());
                player = ProxyServer.getInstance().getPlayer(playerUUID);
                if(player == null || !player.isConnected()) return;

                try {
                    PreparedStatement preparedStatement = database.getConnection().prepareStatement("SELECT id, name, city, state, latitude, longitude, head_id, yaw, pitch, height FROM warps WHERE id = ?");
                    preparedStatement.setInt(1, id);
                    database.executeQueryAsync(preparedStatement).thenAccept(resultSet -> {
                        try {
                            if(resultSet.next()) {
                                String name = resultSet.getString("name");
                                String city = resultSet.getString("city");
                                String state = resultSet.getString("state");
                                double latitude = Double.parseDouble(resultSet.getString("latitude"));
                                double longitude = Double.parseDouble(resultSet.getString("longitude"));
                                String headId = null;
                                final float yawFinal = resultSet.getFloat("yaw");
                                final float pitchFinal = resultSet.getFloat("pitch");
                                double height = resultSet.getDouble("height");
                                try {
                                    headId = resultSet.getString("headId");
                                } catch (SQLException ignored) {}
                                Warp warp = new Warp(id, name, city, state, latitude, longitude, headId, yawFinal, pitchFinal, height, null);
                                pluginMessenger.sendWarpInfo(player, warp, responseNumber);
                            } else {
                                player.sendMessage(TeleportationBungee.getFormattedMessage("Es wurde kein Warp mit der Id " + id + " gefunden!"));
                            }
                            preparedStatement.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case "execute_command":
                playerUUID = UUID.fromString(dataInput.readUTF());
                String command = dataInput.readUTF();
                player = ProxyServer.getInstance().getPlayer(playerUUID);
                if(player == null || !player.isConnected()) return;

                ProxyServer.getInstance().getPluginManager().dispatchCommand(player, command.substring(1));
                break;

            case "warp_delete":
                playerUUID = UUID.fromString(dataInput.readUTF());
                id = Integer.parseInt(dataInput.readUTF());
                player = ProxyServer.getInstance().getPlayer(playerUUID);
                if(player == null || !player.isConnected()) return;

                try {
                    PreparedStatement preparedStatement = database.getConnection().prepareStatement("DELETE FROM warps WHERE id = ?");
                    preparedStatement.setInt(1, id);
                    database.executeUpdateAsync(preparedStatement).thenRun(() -> {
                        player.sendMessage(TeleportationBungee.getFormattedMessage("Der Warp mit der Id " + id + " wurde gelöscht!"));
                        try {
                            preparedStatement.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case "warp_create":
                playerUUID = UUID.fromString(dataInput.readUTF());
                String name = dataInput.readUTF();
                String city = dataInput.readUTF();
                String state = dataInput.readUTF();
                x = Double.parseDouble(dataInput.readUTF());
                z = Double.parseDouble(dataInput.readUTF());
                double[] coordinates;
                player = ProxyServer.getInstance().getPlayer(playerUUID);
                if(player == null || !player.isConnected()) return;
                try {
                    coordinates = GeoData.bteGeneratorSettings.projection().toGeo(x, z);
                } catch (OutOfProjectionBoundsException e) {
                    player.sendMessage(getFormattedMessage("§cError: §cOutOfProjectionBoundsException"));
                    return;
                }
                String headId = dataInput.readUTF();
                yaw = Float.parseFloat(dataInput.readUTF());
                pitch = Float.parseFloat(dataInput.readUTF());
                double height = Double.parseDouble(dataInput.readUTF());
                if(headId.equals("null")) headId = null;

                try {
                    PreparedStatement preparedStatement = database.getConnection().prepareStatement("INSERT INTO warps (name, city, state, latitude, longitude, head_id, yaw, pitch, height) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, city);
                    preparedStatement.setString(3, state);
                    preparedStatement.setString(4, String.valueOf(coordinates[1]));
                    preparedStatement.setString(5, String.valueOf(coordinates[0]));
                    preparedStatement.setString(6, headId);
                    preparedStatement.setFloat(7, yaw);
                    preparedStatement.setFloat(8, pitch);
                    preparedStatement.setDouble(9, height);
                    database.executeUpdateAsync(preparedStatement).thenRun(() -> {
                        player.sendMessage(TeleportationBungee.getFormattedMessage("Der Warp wurde erstellt!"));
                        try {
                            preparedStatement.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case "warp_change":
                playerUUID = UUID.fromString(dataInput.readUTF());
                id = Integer.parseInt(dataInput.readUTF());
                String column = dataInput.readUTF();
                String value = dataInput.readUTF();
                if(value.equals("null")) value = null;
                player = ProxyServer.getInstance().getPlayer(playerUUID);
                if(player == null || !player.isConnected()) return;

                if(column.equals("coordinates")) {
                    if(value == null) return;
                    try {
                        PreparedStatement preparedStatement = database.getConnection().prepareStatement("UPDATE warps SET latitude = ?, longitude = ? WHERE id = ?");
                        preparedStatement.setString(1, value.split(" ")[0].replace(",", ""));
                        preparedStatement.setString(2, value.split(" ")[1]);
                        preparedStatement.setInt(3, id);
                        database.executeUpdateAsync(preparedStatement).thenRun(() -> {
                            player.sendMessage(TeleportationBungee.getFormattedMessage("Der Warp wurde geändert!"));
                            try {
                                preparedStatement.close();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                try {
                    PreparedStatement preparedStatement = database.getConnection().prepareStatement("UPDATE warps SET " + column + " = ? WHERE id = ?");
                    if((column.equals("yaw") || column.equals("pitch")) && value != null) {
                        preparedStatement.setFloat(1, Float.parseFloat(value));
                    } else if(column.equals("height") && value != null) {
                        preparedStatement.setDouble(1, Double.parseDouble(value));
                    } else {
                        preparedStatement.setString(1, value);
                    }
                    preparedStatement.setInt(2, id);
                    database.executeUpdateAsync(preparedStatement).thenRun(() -> {
                        player.sendMessage(TeleportationBungee.getFormattedMessage("Der Warp wurde geändert!"));
                        try {
                            preparedStatement.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case "last_location":
                playerUUID = UUID.fromString(dataInput.readUTF());
                x = Double.parseDouble(dataInput.readUTF());
                y = Double.parseDouble(dataInput.readUTF());
                z = Double.parseDouble(dataInput.readUTF());
                yaw = Float.parseFloat(dataInput.readUTF());
                pitch = Float.parseFloat(dataInput.readUTF());
                player = ProxyServer.getInstance().getPlayer(playerUUID);
                if(player == null || !player.isConnected()) return;

                registriesProvider.getLastLocationsRegistry().register(playerUUID, new LastLocation(x, y, z, yaw, pitch, player.getServer().getInfo()));
                break;

            case "warps_search":
                playerUUID = UUID.fromString(dataInput.readUTF());
                String search = dataInput.readUTF();
                player = ProxyServer.getInstance().getPlayer(playerUUID);
                if(player == null || !player.isConnected()) return;

                try {
                    PreparedStatement preparedStatement = database.getConnection().prepareStatement("SELECT id, name, city, state, latitude, longitude, yaw, pitch, height FROM warps WHERE name = ? COLLATE NOCASE");
                    preparedStatement.setString(1, search);
                    database.executeQueryAsync(preparedStatement).thenAccept(resultSet -> {
                        List<Warp> warpsSearch1 = new ArrayList<>();
                        try {
                            while(resultSet.next()) {
                                final int idFinal = resultSet.getInt("id");
                                final String nameFinal = resultSet.getString("name");
                                final String cityFinal = resultSet.getString("city");
                                final String stateFinal = resultSet.getString("state");
                                final double latitudeFinal = Double.parseDouble(resultSet.getString("latitude"));
                                final double longitudeFinal = Double.parseDouble(resultSet.getString("longitude"));
                                final float yawFinal = resultSet.getFloat("yaw");
                                final float pitchFinal = resultSet.getFloat("pitch");
                                final double heightFinal = resultSet.getDouble("height");
                                String stayServer = null;
                                for(GeoServer geoServer : this.geoData.getGeoServers()) {
                                    if(nameFinal.equals(geoServer.getNormenWarp())) {
                                        stayServer = geoServer.getServerInfo().getName();
                                    }
                                }
                                Warp warp = new Warp(idFinal, nameFinal, cityFinal, stateFinal, latitudeFinal, longitudeFinal, null, yawFinal, pitchFinal, heightFinal, stayServer);
                                warpsSearch1.add(warp);
                            }
                            preparedStatement.close();
                            if(warpsSearch1.size() == 1) {
                                Warp warp = warpsSearch1.get(0);
                                ProxyServer.getInstance().getPluginManager().dispatchCommand(player, warp.getTpllCommand());
                                return;
                            }

                            PreparedStatement preparedStatement2 = database.getConnection().prepareStatement("SELECT id, name, city, state, latitude, longitude, yaw, pitch, height FROM warps WHERE city = ? COLLATE NOCASE LIMIT 10");
                            preparedStatement2.setString(1, search);
                            database.executeQueryAsync(preparedStatement2).thenAccept(resultSet2 -> {
                                List<Warp> warpsSearch2 = new ArrayList<>();
                                try {
                                    while(resultSet2.next()) {
                                        final int idFinal = resultSet2.getInt("id");
                                        final String nameFinal = resultSet2.getString("name");
                                        final String cityFinal = resultSet2.getString("city");
                                        final String stateFinal = resultSet2.getString("state");
                                        final double latitudeFinal = Double.parseDouble(resultSet2.getString("latitude"));
                                        final double longitudeFinal = Double.parseDouble(resultSet2.getString("longitude"));
                                        final float yawFinal = resultSet2.getFloat("yaw");
                                        final float pitchFinal = resultSet2.getFloat("pitch");
                                        final double heightFinal = resultSet2.getDouble("height");
                                        Warp warp = new Warp(idFinal, nameFinal, cityFinal, stateFinal, latitudeFinal, longitudeFinal, null, yawFinal, pitchFinal, heightFinal, null);
                                        warpsSearch2.add(warp);
                                    }
                                    preparedStatement2.close();

                                    if(warpsSearch1.size() == 0 && warpsSearch2.size() == 0) {
                                        player.sendMessage(TeleportationBungee.getFormattedMessage("Leider wurden keine Warps gefunden!"));
                                        return;
                                    }

                                    if(warpsSearch1.size() > 0) {
                                        player.sendMessage(TeleportationBungee.getFormattedMessage("Es wurden Warps mit diesem Namen gefunden:"));
                                    }
                                    for(Warp warp : warpsSearch1) {
                                        TextComponent text = new TextComponent(TeleportationBungee.getFormattedMessage("- " + warp.getName() + " (" + warp.getCity() + ", " + warp.getState() + ")"));
                                        TextComponent button = new TextComponent(TeleportationBungee.getFormattedMessage("   §r§9Teleportieren"));
                                        button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + warp.getTpllCommand()));
                                        player.sendMessage(text);
                                        player.sendMessage(button);
                                    }
                                    if(warpsSearch2.size() > 0) {
                                        player.sendMessage(TeleportationBungee.getFormattedMessage("Es wurden Städte mit diesem Namen gefunden. Bis zu 10 der Warps in diesen Städten werden angezeigt:"));
                                    }
                                    for(Warp warp : warpsSearch2) {
                                        TextComponent text = new TextComponent(TeleportationBungee.getFormattedMessage("- " + warp.getName() + " (" + warp.getCity() + ", " + warp.getState() + ")"));
                                        TextComponent button = new TextComponent(TeleportationBungee.getFormattedMessage("   §r§9Teleportieren"));
                                        button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + warp.getTpllCommand()));
                                        player.sendMessage(text);
                                        player.sendMessage(button);
                                    }

                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });

                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case "players_online":
                String serverAddress = dataInput.readUTF();
                Optional<ServerInfo> optionalServerInfo = ProxyServer.getInstance().getServers().values().stream().filter(si -> si.getSocketAddress().toString().contains(serverAddress)).findFirst();
                if(!optionalServerInfo.isPresent()) {
                    return;
                }
                ServerInfo serverInfo = optionalServerInfo.get();
                JSONArray jsonArray = new JSONArray(dataInput.readUTF());

                if(jsonArray.length() == 0) {
                    List<BukkitPlayer> unregister = registriesProvider.getBukkitPlayersRegistry().getBukkitPlayers().values().stream().filter(bukkitPlayer -> bukkitPlayer.getServerInfo().equals(serverInfo)).collect(Collectors.toList());
                    for(BukkitPlayer bukkitPlayer : unregister) {
                        registriesProvider.getBukkitPlayersRegistry().unregister(bukkitPlayer.getProxiedPlayer());
                    }
                }

                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject playerObject = jsonArray.getJSONObject(i);
                    playerUUID = UUID.fromString(playerObject.getString("player_uuid"));
                    ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playerUUID);
                    if(proxiedPlayer == null || !proxiedPlayer.isConnected())  {
                        continue;
                    }
                    x = playerObject.getDouble("x");
                    y = playerObject.getDouble("y");
                    z = playerObject.getDouble("z");
                    yaw = playerObject.getFloat("yaw");
                    pitch = playerObject.getFloat("pitch");
                    String gameMode = playerObject.getString("gamemode").toLowerCase();
                    BukkitPlayer bukkitPlayer = new BukkitPlayer(proxiedPlayer, serverInfo, x, y, z, yaw, pitch, gameMode);
                    if(registriesProvider.getBukkitPlayersRegistry().isRegistered(bukkitPlayer.getProxiedPlayer())) {
                        registriesProvider.getBukkitPlayersRegistry().replace(bukkitPlayer);
                        continue;
                    }
                    registriesProvider.getBukkitPlayersRegistry().register(bukkitPlayer);
                }
                break;

        }
    }

    private void loadGuiData(ProxiedPlayer player, String query, boolean loadFromDb, Runnable runnable) {
        if(!loadFromDb) {
            runnable.run();
            return;
        }
        try {
            PreparedStatement preparedStatement = database.getConnection().prepareStatement(query);
            database.executeQueryAsync(preparedStatement).thenAccept(resultSet -> {
                Map<Integer, List<JSONObject>> pages = new HashMap<>();
                try {
                    List<JSONObject> currentPage = new ArrayList<>();
                    while (resultSet.next()) {
                        JSONObject object = new JSONObject();
                        object.put("name", resultSet.getString("name"));
                        object.put("state", resultSet.getString("state"));
                        tryPutInt(object, resultSet, "id");
                        tryPutDouble(object, resultSet, "latitude");
                        tryPutDouble(object, resultSet, "longitude");
                        tryPutString(object, resultSet, "city");
                        tryPutFloat(object, resultSet, "yaw");
                        tryPutFloat(object, resultSet, "pitch");
                        tryPutDouble(object, resultSet, "height");
                        try {
                            String headId = resultSet.getString("head_id");
                            if (!resultSet.wasNull()) {
                                object.put("head_id", headId);
                            }
                        } catch (SQLException ignored) {}
                        currentPage.add(object);
                        if(currentPage.size() == 36) {
                            pages.put(pages.size(), currentPage);
                            currentPage = new ArrayList<>();
                        }
                    }
                    if(currentPage.size() > 0) {
                        pages.put(pages.size(), currentPage);
                    }
                    preparedStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                cachedGuiData.put(player.getUniqueId(), pages);
                runnable.run();
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendGuiData(ProxiedPlayer player, String title, int[] pages, String query) {
        loadGuiData(player, query, pages[0] == 0, () -> {
            JSONArray pagesData = new JSONArray();
            for (int page : pages) {
                if(page + 1 > cachedGuiData.get(player.getUniqueId()).size()) {
                    continue;
                }
                JSONObject object = new JSONObject();
                object.put("page", page);
                JSONArray objectContent = new JSONArray();
                cachedGuiData.get(player.getUniqueId()).get(page).forEach(objectContent::put);
                object.put("content", objectContent);
                pagesData.put(object);
            }
            pluginMessenger.sendGuiData(player, title, pagesData);
        });
    }

    public void tryPutString(JSONObject object, ResultSet resultSet, String column) {
        try {
            object.put(column, resultSet.getString(column));
        } catch (SQLException ignored) {}
    }

    public void tryPutInt(JSONObject object, ResultSet resultSet, String column) {
        try {
            object.put(column, resultSet.getInt(column));
        } catch (SQLException ignored) {}
    }

    public void tryPutDouble(JSONObject object, ResultSet resultSet, String column) {
        try {
            object.put(column, Double.parseDouble(resultSet.getString(column)));
        } catch (SQLException ignored) {}
    }

    public void tryPutFloat(JSONObject object, ResultSet resultSet, String column) {
        try {
            object.put(column, resultSet.getFloat(column));
        } catch (SQLException ignored) {}
    }

}
