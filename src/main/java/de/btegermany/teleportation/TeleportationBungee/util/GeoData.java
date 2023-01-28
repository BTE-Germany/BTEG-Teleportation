package de.btegermany.teleportation.TeleportationBungee.util;



import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.protocol.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class GeoData {


    public ServerInfo getServerFromLocation(double lat, double lon, boolean offline) {
        Location location = offline ? getOfflineLocation(lat, lon) : getLocation(lat, lon);
        if (location == null) {
            return null;
        }
        Map<Location.Detail, ServerInfo> serverInfoMap = Maps.newHashMap();

        for (SledgehammerServer s : ServerHandler.getInstance().getServers().values()) {
            if (!s.isEarthServer()) {
                continue;
            }
            if (s.getLocations() == null || s.getLocations().isEmpty()) {
                continue;
            }
            for (Location l : s.getLocations()) {
                switch (l.detailType) {
                    case city:
                        if (l.compare(location, Location.Detail.city)) {
                            serverInfoMap.put(l.detailType, s.getInfo());
                            continue;
                        }
                        break;
                    case county:
                        if (l.compare(location, Location.Detail.county)) {
                            serverInfoMap.put(l.detailType, s.getInfo());
                            continue;
                        }
                        break;
                    case state:
                        if (l.compare(location, Location.Detail.state)) {
                            serverInfoMap.put(l.detailType, s.getInfo());
                            continue;
                        }
                        break;
                    case country:
                        if (l.compare(location, Location.Detail.country)) {
                            serverInfoMap.put(l.detailType, s.getInfo());
                            continue;
                        }
                        break;
                }
            }
        }

        if (serverInfoMap.get(Location.Detail.city) != null) {
            return serverInfoMap.get(Location.Detail.city);
        }

        if (serverInfoMap.get(Location.Detail.county) != null) {
            return serverInfoMap.get(Location.Detail.county);
        }

        if (serverInfoMap.get(Location.Detail.state) != null) {
            return serverInfoMap.get(Location.Detail.state);
        }

        if (serverInfoMap.get(Location.Detail.country) != null) {
            return serverInfoMap.get(Location.Detail.country);
        }

        return null;
    }

    public Location getLocation(double lat, double lon, int zoom) {
        try {
            String fullRequest = Constants.nominatimAPI.replace("{zoom}", String.valueOf(zoom)) + "&lat=" + lat + "&accept-language=en&lon=" + lon;

            URL url = new URL(fullRequest);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", Constants.PLUGINID + "/" + Constants.VERSION);
            con.setRequestProperty("Accept", "application/json");

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                JsonObject geocode = JsonUtils.parseString(response.toString()).getAsJsonObject();
                JsonObject address = geocode.getAsJsonObject("address");

                String city = null;
                if (address.has("city")) {
                    city = address.get("city").getAsString();
                } else if (address.has("town")) {
                    city = address.get("town").getAsString();
                }
                String county = null;
                if (address.has("county")) {
                    county = address.get("county").getAsString();
                }
                String state = null;
                if (address.has("state")) {
                    state = address.get("state").getAsString();
                } else if (address.has("territory")) {
                    state = address.get("territory").getAsString();
                }
                String country = address.get("country").getAsString();

                return new Location(Location.Detail.none, city, county, state, country);
            }
        } catch (IOException e) {
            return null;
        }
    }
}
