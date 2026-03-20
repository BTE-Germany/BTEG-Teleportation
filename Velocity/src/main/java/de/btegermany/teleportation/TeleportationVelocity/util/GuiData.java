package de.btegermany.teleportation.TeleportationVelocity.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.btegermany.teleportation.TeleportationAPI.PagedGuiType;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

public class GuiData {

    private static final int FIRST_PAGE = 1;
    private static final int NEXT_PAGE_DIFF = 1;
    private static final int MAX_PAGE_SIZE = 45;

    private final RegistriesProvider registriesProvider;
    private final PluginMessenger pluginMessenger;
    private final ProxyServer proxyServer;
    private final Map<UUID, Map<Integer, List<JSONObject>>> cachedGuiData;

    public GuiData(RegistriesProvider registriesProvider, PluginMessenger pluginMessenger, ProxyServer proxyServer) {
        this.registriesProvider = registriesProvider;
        this.pluginMessenger = pluginMessenger;
        this.proxyServer = proxyServer;
        this.cachedGuiData = new HashMap<>();
    }

    public void send(PagedGuiType type, Player player, int requestId, int[] pages, String[] args) {
        if (pages[0] == FIRST_PAGE) {
            this.load(type, player, args);
        }
        this.pluginMessenger.sendGuiData(requestId, player, this.getJSONArrayFromCache(player.getUniqueId(), pages));
    }

    private void load(PagedGuiType type, Player player, String[] args) {
        Map<Integer, List<JSONObject>> data = switch (type) {

            case ALL -> {
                Set<Warp> warps = this.registriesProvider.getWarpsRegistry().getWarps();
                yield this.warpsToPagedJson(warps);
            }

            case CITIES -> {
                Set<String> citiesAdded = new HashSet<>();

                List<Warp> warpsOrderedByCity = this.registriesProvider.getWarpsRegistry().getWarps().stream()
                        .sorted(Comparator.comparing(Warp::getCity)).toList();

                yield this.toPagedJson(warpsOrderedByCity, (warp, object) -> {
                    if (!citiesAdded.add(warp.getCity())) {
                        return null;
                    }
                    object.put("name", warp.getCity());
                    object.put("state", warp.getState());
                    return object;
                });
            }

            case TAGS -> {
                List<String> tags = this.registriesProvider.getWarpTagsRegistry().getTags().stream()
                        .sorted(String::compareToIgnoreCase).toList();

                yield this.toPagedJson(tags, (tag, object) -> {
                    object.put("name", tag);
                    return object;
                });
            }

            case EVENTS -> {
                Set<Warp> warps = this.registriesProvider.getWarpsRegistry().getWarps().stream()
                        .filter(warp -> warp.getName().endsWith("[Event]"))
                        .collect(Collectors.toCollection(TreeSet::new));
                yield this.warpsToPagedJson(warps);
            }

            case PLOT_REGIONS -> {
                Set<Warp> warps = this.registriesProvider.getWarpsRegistry().getWarps().stream()
                        .filter(warp -> warp.getName().endsWith("[Plotgebiet]"))
                        .collect(Collectors.toCollection(TreeSet::new));
                yield this.warpsToPagedJson(warps);
            }

            case CITY, LOBBY_WARP -> {
                String city = args[0];
                Set<Warp> warps = this.registriesProvider.getWarpsRegistry().getWarps().stream()
                        .filter(warp -> warp.getCity().equalsIgnoreCase(city))
                        .collect(Collectors.toCollection(TreeSet::new));
                yield this.warpsToPagedJson(warps);
            }

            case TAG -> {
                String tag = args[0];
                Set<Warp> warps = this.registriesProvider.getWarpsRegistry().getWarps().stream()
                        .filter(warp -> warp.getTags().stream().anyMatch(tagToCheck -> tagToCheck.equalsIgnoreCase(tag)))
                        .collect(Collectors.toCollection(TreeSet::new));
                yield this.warpsToPagedJson(warps);
            }

            case STATE -> {
                String state = args[0];
                Set<Warp> warps = this.registriesProvider.getWarpsRegistry().getWarps().stream()
                        .filter(warp -> warp.getState().equals(state))
                        .collect(Collectors.toCollection(TreeSet::new));
                yield this.warpsToPagedJson(warps);
            }

            case SEARCH_RESULTS -> {
                String searchInput = args[0];
                List<Warp> warpsSearch1 = new ArrayList<>(this.registriesProvider.getWarpsRegistry().getWarps().stream()
                        .filter(warp -> warp.getName().equalsIgnoreCase(searchInput))
                        .toList());
                if (warpsSearch1.size() == 1) {
                    Warp warp = warpsSearch1.getFirst();
                    this.proxyServer.getCommandManager().executeAsync(player, warp.getTpllCommand());
                    yield null;
                }

                List<Warp> warpsSearch2 = this.registriesProvider.getWarpsRegistry().getWarps().stream()
                        .filter(warp -> warp.getCity().equalsIgnoreCase(searchInput))
                        .toList();

                if (warpsSearch1.isEmpty() && warpsSearch2.isEmpty()) {
                    sendMessage(player, Component.text("Leider wurden keine Warps gefunden!", NamedTextColor.GOLD));
                    yield null;
                }

                warpsSearch1.addAll(warpsSearch2);

                yield this.warpsToPagedJson(warpsSearch1);
            }

            case LOBBY_WARP_AROUND -> {
                String city = args[0];
                double latitude = Double.parseDouble(args[1]);
                double longitude = Double.parseDouble(args[2]);
                int radius = Integer.parseInt(args[3]);

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

                yield this.warpsToPagedJson(warpsAround);
            }
        };

        if (data == null) {
            this.cachedGuiData.remove(player.getUniqueId());
            return;
        }

        this.cachedGuiData.put(player.getUniqueId(), data);
    }

    private Map<Integer, List<JSONObject>> warpsToPagedJson(Collection<Warp> warps) {
        return this.toPagedJson(warps, (warp, object) -> {
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
            if (warp.getHeadId() != null) {
                object.put("head_id", warp.getHeadId());
            }
            return object;
        });
    }

    private <T> Map<Integer, List<JSONObject>> toPagedJson(Collection<T> collection, BiFunction<T, JSONObject, JSONObject> mapItemFunction) {
        final Map<Integer, List<JSONObject>> pagesMap = new HashMap<>();
        final List<JSONObject> currentPage = new ArrayList<>();

        for (T item : collection) {
            JSONObject resultJson = mapItemFunction.apply(item, new JSONObject());
            if (resultJson == null) {
                continue;
            }
            currentPage.add(resultJson);

            if (currentPage.size() == MAX_PAGE_SIZE) {
                pagesMap.put(pagesMap.size() + NEXT_PAGE_DIFF, new ArrayList<>(currentPage));
                currentPage.clear();
            }
        }

        if (!currentPage.isEmpty()) {
            pagesMap.put(pagesMap.size() + NEXT_PAGE_DIFF, currentPage);
        }

        return pagesMap;
    }

    private JSONArray getJSONArrayFromCache(UUID playerUUID, int[] pages) {
        JSONArray pagesData = new JSONArray();
        for (int pageNum : pages) {
            if (!this.cachedGuiData.get(playerUUID).containsKey(pageNum)) {
                continue;
            }
            JSONObject object = new JSONObject();
            object.put("page", pageNum);
            JSONArray objectContent = new JSONArray();
            this.cachedGuiData.get(playerUUID).get(pageNum).forEach(objectContent::put);
            object.put("content", objectContent);
            pagesData.put(object);
        }
        return pagesData;
    }

}
