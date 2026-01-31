package de.btegermany.teleportation.TeleportationVelocity.listener;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationAPI.State;
import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity;
import de.btegermany.teleportation.TeleportationVelocity.data.Database;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoData;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.util.BukkitPlayer;
import de.btegermany.teleportation.TeleportationVelocity.util.Warp;
import de.btegermany.teleportation.TeleportationVelocity.util.WarpIdsManager;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class PluginMsgListener {

    private static final List<String> ALLOWED_COMMANDS_TO_EXECUTE = List.of("tpll", "tp");

    private final PluginMessenger pluginMessenger;
    private final Database database;
    private final RegistriesProvider registriesProvider;
    private final ProxyServer proxyServer;
    private final Logger logger;
    private final WarpIdsManager warpIdsManager;
    private final Map<UUID, Map<Integer, List<JSONObject>>> cachedGuiData;

    public PluginMsgListener(PluginMessenger pluginMessenger, Database database, RegistriesProvider registriesProvider, ProxyServer proxyServer, Logger logger, WarpIdsManager warpIdsManager) {
        this.pluginMessenger = pluginMessenger;
        this.database = database;
        this.registriesProvider = registriesProvider;
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.warpIdsManager = warpIdsManager;
        this.cachedGuiData = new HashMap<>();
    }

    @Subscribe
    public void onPluginMessageFromBackend(PluginMessageEvent event) {
        if (!(event.getSource() instanceof ServerConnection) || !event.getIdentifier().equals(TeleportationVelocity.PLUGIN_CHANNEL)) {
            return;
        }

        ByteArrayDataInput dataInput = ByteStreams.newDataInput(event.getData());

        PluginMessage.MessageType messageType = PluginMessage.MessageType.valueOf(dataInput.readUTF());
        int requestId = (messageType == PluginMessage.MessageType.WITH_RESPONSE || messageType == PluginMessage.MessageType.RESPONSE) ? Integer.parseInt(dataInput.readUTF()) : -1;

        switch (dataInput.readUTF()) {

            case "gui_data_request" -> {
                UUID playerUUID = UUID.fromString(dataInput.readUTF());
                String metaTitle = dataInput.readUTF();
                String[] pagesRaw = dataInput.readUTF().split(", ");
                int[] pages = new int[pagesRaw.length];
                for (int i = 0; i < pagesRaw.length; i++) {
                    pages[i] = Integer.parseInt(pagesRaw[i]);
                }

                this.proxyServer.getPlayer(playerUUID).ifPresent(player -> {
                    String group = metaTitle.split("_").length > 0 ? metaTitle.split("_")[0] : metaTitle;
                    String title = metaTitle.equals(group) ? group : metaTitle.substring(group.length() + 1);

                    switch (group) {
                        case "Alle" -> {
                            if (pages[0] == 0) {
                                Set<Warp> warps = this.registriesProvider.getWarpsRegistry().getWarps();
                                this.cachedGuiData.put(playerUUID, this.getJSONObjectsFromWarps(warps));
                            }
                            this.pluginMessenger.sendGuiData(requestId, player, metaTitle, this.getJSONArrayFromCache(playerUUID, pages));
                        }
                        case "Städte" -> {
                            final Map<Integer, List<JSONObject>> pagesMap = new HashMap<>();
                            final List<JSONObject> currentPage = new ArrayList<>();
                            Set<String> citiesAdded = new HashSet<>();

                            List<Warp> warpsOrderedByCity = this.registriesProvider.getWarpsRegistry().getWarps().stream()
                                    .sorted(Comparator.comparing(Warp::getCity)).toList();
                            for (Warp warp : warpsOrderedByCity) {
                                if (!citiesAdded.add(warp.getCity())) {
                                    continue;
                                }
                                JSONObject object = new JSONObject();
                                object.put("name", warp.getCity());
                                object.put("state", warp.getState());
                                currentPage.add(object);
                                if (currentPage.size() == 36) {
                                    pagesMap.put(pagesMap.size(), new ArrayList<>(currentPage));
                                    currentPage.clear();
                                }
                            }
                            if (!currentPage.isEmpty()) {
                                pagesMap.put(pagesMap.size(), currentPage);
                            }

                            JSONArray pagesData = new JSONArray();
                            pagesMap.forEach((page, content) -> {
                                JSONObject object = new JSONObject();
                                object.put("page", page);
                                JSONArray objectContent = new JSONArray();
                                content.forEach(objectContent::put);
                                object.put("content", objectContent);
                                pagesData.put(object);
                            });
                            this.pluginMessenger.sendGuiData(requestId, player, metaTitle, pagesData);
                        }
                        case "Tags" -> {
                            final Map<Integer, List<JSONObject>> pagesMap = new HashMap<>();
                            final List<JSONObject> currentPage = new ArrayList<>();

                            List<String> tags = this.registriesProvider.getWarpTagsRegistry().getTags().stream()
                                    .sorted(String::compareToIgnoreCase).toList();
                            for (String tag : tags) {
                                JSONObject object = new JSONObject();
                                object.put("name", tag);
                                currentPage.add(object);
                                if (currentPage.size() == 36) {
                                    pagesMap.put(pagesMap.size(), new ArrayList<>(currentPage));
                                    currentPage.clear();
                                }
                            }
                            if (!currentPage.isEmpty()) {
                                pagesMap.put(pagesMap.size(), currentPage);
                            }

                            JSONArray pagesData = new JSONArray();
                            pagesMap.forEach((page, content) -> {
                                JSONObject object = new JSONObject();
                                object.put("page", page);
                                JSONArray objectContent = new JSONArray();
                                content.forEach(objectContent::put);
                                object.put("content", objectContent);
                                pagesData.put(object);
                            });
                            this.pluginMessenger.sendGuiData(requestId, player, metaTitle, pagesData);
                        }
                        case "Events" -> {
                            if (pages[0] == 0) {
                                Set<Warp> warps = this.registriesProvider.getWarpsRegistry().getWarps().stream()
                                        .filter(warp -> warp.getName().endsWith("[Event]"))
                                        .collect(Collectors.toCollection(TreeSet::new));
                                this.cachedGuiData.put(playerUUID, this.getJSONObjectsFromWarps(warps));
                            }
                            this.pluginMessenger.sendGuiData(requestId, player, metaTitle, this.getJSONArrayFromCache(playerUUID, pages));
                        }
                        case "Plotregionen" -> {
                            if (pages[0] == 0) {
                                Set<Warp> warps = this.registriesProvider.getWarpsRegistry().getWarps().stream()
                                        .filter(warp -> warp.getName().endsWith("[Plotgebiet]"))
                                        .collect(Collectors.toCollection(TreeSet::new));
                                this.cachedGuiData.put(playerUUID, this.getJSONObjectsFromWarps(warps));
                            }
                            this.pluginMessenger.sendGuiData(requestId, player, metaTitle, this.getJSONArrayFromCache(playerUUID, pages));
                        }
                        case "city", "lobbywarp" -> {
                            if (pages[0] == 0) {
                                Set<Warp> warps = this.registriesProvider.getWarpsRegistry().getWarps().stream()
                                        .filter(warp -> warp.getCity().equalsIgnoreCase(title))
                                        .collect(Collectors.toCollection(TreeSet::new));
                                this.cachedGuiData.put(playerUUID, this.getJSONObjectsFromWarps(warps));
                            }
                            this.pluginMessenger.sendGuiData(requestId, player, metaTitle, this.getJSONArrayFromCache(playerUUID, pages));
                        }
                        case "tag" -> {
                            if (pages[0] == 0) {
                                Set<Warp> warps = this.registriesProvider.getWarpsRegistry().getWarps().stream()
                                        .filter(warp -> warp.getTags().stream().anyMatch(tag -> tag.equalsIgnoreCase(title)))
                                        .collect(Collectors.toCollection(TreeSet::new));
                                this.cachedGuiData.put(playerUUID, this.getJSONObjectsFromWarps(warps));
                            }
                            this.pluginMessenger.sendGuiData(requestId, player, metaTitle, this.getJSONArrayFromCache(playerUUID, pages));
                        }
                        case "bl" -> {
                            if (pages[0] == 0) {
                                Set<Warp> warps = this.registriesProvider.getWarpsRegistry().getWarps().stream()
                                        .filter(warp -> warp.getState().equals(title))
                                        .collect(Collectors.toCollection(TreeSet::new));
                                this.cachedGuiData.put(playerUUID, this.getJSONObjectsFromWarps(warps));
                            }
                            this.pluginMessenger.sendGuiData(requestId, player, metaTitle, this.getJSONArrayFromCache(playerUUID, pages));
                        }
                        case "search" -> {
                            List<Warp> warpsSearch1 = new ArrayList<>(this.registriesProvider.getWarpsRegistry().getWarps().stream()
                                    .filter(warp -> warp.getName().equalsIgnoreCase(title))
                                    .toList());
                            if (warpsSearch1.size() == 1) {
                                Warp warp = warpsSearch1.getFirst();
                                this.proxyServer.getCommandManager().executeAsync(player, warp.getTpllCommand());
                                return;
                            }

                            List<Warp> warpsSearch2 = this.registriesProvider.getWarpsRegistry().getWarps().stream()
                                    .filter(warp -> warp.getCity().equalsIgnoreCase(title))
                                    .toList();

                            if (warpsSearch1.isEmpty() && warpsSearch2.isEmpty()) {
                                sendMessage(player, Component.text("Leider wurden keine Warps gefunden!", NamedTextColor.GOLD));
                                return;
                            }

                            warpsSearch1.addAll(warpsSearch2);

                            if (pages[0] == 0) {
                                this.cachedGuiData.put(playerUUID, this.getJSONObjectsFromWarps(warpsSearch1));
                            }
                            this.pluginMessenger.sendGuiData(requestId, player, metaTitle, this.getJSONArrayFromCache(playerUUID, pages));
                        }
                        case "lobbywarp-around" -> {
                            String[] args = metaTitle.split("_");
                            String city = args[1];
                            double latitude = Double.parseDouble(args[2]);
                            double longitude = Double.parseDouble(args[3]);
                            int radius = Integer.parseInt(args[4]);
                            Set<Warp> warpsAround = new HashSet<>();
                            for (Warp warp : this.registriesProvider.getWarpsRegistry().getWarps()) {
                                if (warp.getCity().equals(city)) {
                                    continue;
                                }
                                double[] cA = new double[]{latitude, longitude};
                                double[] cB = new double[]{warp.getLatitude(), warp.getLongitude()};

                                double dLat = Math.toRadians(cB[0] - cA[0]);
                                double dLon = Math.toRadians(cB[1] - cA[1]);
                                double lat1 = Math.toRadians(cA[0]);
                                double lat2 = Math.toRadians(cB[0]);
                                double distance = 2 * 6371 * Math.asin(Math.sqrt(Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2)));
                                if (distance <= radius) {
                                    warpsAround.add(warp);
                                }
                            }

                            if (pages[0] == 0) {
                                this.cachedGuiData.put(playerUUID, this.getJSONObjectsFromWarps(warpsAround));
                            }
                            pluginMessenger.sendGuiData(requestId, player, String.format("lobbywarp-around_%s", city), this.getJSONArrayFromCache(playerUUID, pages));
                        }
                    }
                });
            }

            case "execute_command" -> {
                UUID playerUUID = UUID.fromString(dataInput.readUTF());
                String command = dataInput.readUTF();

                int spaceIndex = !command.contains(" ") ? 0 : command.indexOf(" ");
                String baseCommand = command.substring(0, spaceIndex);
                if (!ALLOWED_COMMANDS_TO_EXECUTE.contains(baseCommand)) {
                    this.logger.error("Not allowed to remotely execute command '{}'", baseCommand);
                    return;
                }

                this.proxyServer.getPlayer(playerUUID).ifPresent(player -> this.proxyServer.getCommandManager().executeAsync(player, command));
            }

            case "warp_delete" -> {
                UUID playerUUID = UUID.fromString(dataInput.readUTF());
                int id = Integer.parseInt(dataInput.readUTF());

                this.proxyServer.getPlayer(playerUUID).ifPresent(player -> {
                    try {
                        PreparedStatement preparedStatement = database.getConnection().prepareStatement("DELETE FROM warps WHERE id = ?");
                        preparedStatement.setInt(1, id);
                        database.executeUpdateAsync(preparedStatement).thenRun(() -> {
                            this.registriesProvider.getWarpsRegistry().unregister(id);
                            sendMessage(player, Component.text("Der Warp mit der Id %d wurde gelöscht!".formatted(id)));
                            try {
                                preparedStatement.close();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            case "warp_create" -> {
                UUID playerUUID = UUID.fromString(dataInput.readUTF());
                String name = dataInput.readUTF();
                String city = dataInput.readUTF();
                String state = dataInput.readUTF();
                double x = Double.parseDouble(dataInput.readUTF());
                double z = Double.parseDouble(dataInput.readUTF());

                this.proxyServer.getPlayer(playerUUID).ifPresent(player -> {
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
                        this.registriesProvider.getWarpsRegistry().registerAsync(warp).thenAccept(success -> {
                            if (success) {
                                sendMessage(player, Component.text("Der Warp wurde mit der Id \"%d\" erstellt!".formatted(warp.getId()), NamedTextColor.GOLD));
                                return;
                            }
                            sendMessage(player, Component.text("Ein Fehler ist aufgetreten. Der Warp konnte nicht erstellt werden.", NamedTextColor.RED));
                        });
                    });
                });
            }

            case "warp_change" -> {
                UUID playerUUID = UUID.fromString(dataInput.readUTF());
                int id = Integer.parseInt(dataInput.readUTF());
                String column = dataInput.readUTF();
                String valueInputRaw = dataInput.readUTF();

                this.proxyServer.getPlayer(playerUUID).ifPresent(player -> {
                    Warp warp = this.registriesProvider.getWarpsRegistry().getWarp(id);
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
                            e.printStackTrace();
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
                        e.printStackTrace();
                    }
                });
            }

            case "last_location", "player_world" -> {
                this.registriesProvider.getPluginMessagesWithResponseRegistry().getPluginMessageWithResponse(requestId).accept(dataInput);
            }

            case "players_online" -> {
                String serverAddress = dataInput.readUTF();
                Optional<RegisteredServer> optionalServer = this.proxyServer.getAllServers().stream().filter(server -> server.getServerInfo().getAddress().toString().contains(serverAddress)).findFirst();
                if (optionalServer.isEmpty()) {
                    return;
                }
                RegisteredServer server = optionalServer.get();
                JSONArray jsonArray = new JSONArray(dataInput.readUTF());

                if (jsonArray.isEmpty()) {
                    List<BukkitPlayer> unregister = registriesProvider.getBukkitPlayersRegistry().getBukkitPlayers().values().stream().filter(bukkitPlayer -> bukkitPlayer.getServer().equals(server)).toList();
                    for (BukkitPlayer bukkitPlayer : unregister) {
                        registriesProvider.getBukkitPlayersRegistry().unregister(bukkitPlayer.getProxiedPlayer());
                    }
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject playerObject = jsonArray.getJSONObject(i);
                    UUID playerUUID = UUID.fromString(playerObject.getString("player_uuid"));

                    this.proxyServer.getPlayer(playerUUID).ifPresent(player -> {
                        double x = playerObject.getDouble("x");
                        double y = playerObject.getDouble("y");
                        double z = playerObject.getDouble("z");
                        float yaw = playerObject.getFloat("yaw");
                        float pitch = playerObject.getFloat("pitch");
                        String gameMode = playerObject.getString("gamemode").toLowerCase();
                        BukkitPlayer bukkitPlayer = new BukkitPlayer(player, server, x, y, z, yaw, pitch, gameMode);
                        if (registriesProvider.getBukkitPlayersRegistry().isRegistered(bukkitPlayer.getProxiedPlayer())) {
                            registriesProvider.getBukkitPlayersRegistry().replace(bukkitPlayer);
                            return;
                        }
                        registriesProvider.getBukkitPlayersRegistry().register(bukkitPlayer);
                    });
                }
            }

            case "tp_random_warp" -> {
                UUID playerUUID = UUID.fromString(dataInput.readUTF());

                this.proxyServer.getPlayer(playerUUID).ifPresent(player -> {
                    Warp warp = this.getRandomWarp();

                    this.proxyServer.getCommandManager().executeAsync(player, warp.getTpllCommand());
                    TextComponent textComponent = Component.text("Dies ist ", NamedTextColor.GOLD)
                            .append(Component.text(warp.getName(), NamedTextColor.GREEN))
                            .append(Component.text(" in ", NamedTextColor.GOLD))
                            .append(Component.text(warp.getCity() + ", " + warp.getState(), NamedTextColor.GREEN))
                            .append(Component.text(".", NamedTextColor.GOLD));
                    TextComponent button = Component.text("Klicke hier, um dich zum nächsten Warp zu teleportieren.", NamedTextColor.BLUE)
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/nwarp random"));

                    sendMessage(player, textComponent);
                    sendMessage(player, button);
                });
            }

            case "get_random_warp" -> {
                Warp warp = this.getRandomWarp();

                //player.send
            }

            case "tag_add" -> {
                UUID playerUUID = UUID.fromString(dataInput.readUTF());

                this.proxyServer.getPlayer(playerUUID).ifPresent(player -> {
                    String tag = dataInput.readUTF();
                    int warpId = Integer.parseInt(dataInput.readUTF());

                    this.registriesProvider.getWarpsRegistry().addTagsToWarp(player, warpId, tag);
                });
            }

            case "tag_remove" -> {
                UUID playerUUID = UUID.fromString(dataInput.readUTF());

                this.proxyServer.getPlayer(playerUUID).ifPresent(player -> {
                    String tag = dataInput.readUTF();
                    int warpId = Integer.parseInt(dataInput.readUTF());

                    this.registriesProvider.getWarpsRegistry().removeTagsFromWarp(player, warpId, tag);
                });
            }

            case "tag_edit" -> {
                UUID playerUUID = UUID.fromString(dataInput.readUTF());

                this.proxyServer.getPlayer(playerUUID).ifPresent(player -> {
                    String tagOld = dataInput.readUTF();
                    String tagNew = dataInput.readUTF();

                    this.registriesProvider.getWarpTagsRegistry().editTag(player, tagOld, tagNew, this.registriesProvider.getWarpsRegistry());
                });
            }

        }

        if(requestId == -1) {
            return;
        }
        this.registriesProvider.getPluginMessagesWithResponseRegistry().unregister(requestId);
    }

    private Map<Integer, List<JSONObject>> getJSONObjectsFromWarps(Collection<Warp> warps) {
        final Map<Integer, List<JSONObject>> pagesMap = new HashMap<>();
        final List<JSONObject> currentPage = new ArrayList<>();
        for(Warp warp : warps) {
            JSONObject object = new JSONObject();
            object.put("name", warp.getName());
            object.put("state", warp.getState());
            object.put("id", warp.getId());
            object.put("latitude", warp.getLatitude());
            object.put("longitude", warp.getLongitude());
            object.put("city", warp.getCity());
            object.put("yaw", warp.getYaw());
            object.put("pitch", warp.getPitch());
            object.put("height", warp.getHeight());
            object.put("world", warp.getWorld());
            if(warp.getHeadId() != null) {
                object.put("head_id", warp.getHeadId());
            }
            currentPage.add(object);
            if(currentPage.size() == 36) {
                pagesMap.put(pagesMap.size(), new ArrayList<>(currentPage));
                currentPage.clear();
            }
        }
        if(!currentPage.isEmpty()) {
            pagesMap.put(pagesMap.size(), currentPage);
        }
        return pagesMap;
    }

    private JSONArray getJSONArrayFromCache(UUID playerUUID, int... pages) {
        JSONArray pagesData = new JSONArray();
        for(int pageIndex : pages) {
            if(!this.cachedGuiData.get(playerUUID).containsKey(pageIndex)) {
                continue;
            }
            JSONObject object = new JSONObject();
            object.put("page", pageIndex);
            JSONArray objectContent = new JSONArray();
            this.cachedGuiData.get(playerUUID).get(pageIndex).forEach(objectContent::put);
            object.put("content", objectContent);
            pagesData.put(object);
        }
        return pagesData;
    }

    private Warp getRandomWarp() {
        Set<Warp> warps = this.registriesProvider.getWarpsRegistry().getWarps();
        Warp warp = null;

        do {
            int i = 0;
            int searchedIndex = new Random().nextInt(warps.size());

            for (Warp randomWarp : warps) {
                if (i < searchedIndex) {
                    i++;
                    continue;
                }
                if (randomWarp.getCity().isEmpty()) {
                    break;
                }
                warp = randomWarp;
                break;
            }
        } while (warp == null);

        return warp;
    }

}
