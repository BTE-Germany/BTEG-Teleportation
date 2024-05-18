package de.btegermany.teleportation.TeleportationBukkit.message.withresponse;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessageWithResponse;
import de.btegermany.teleportation.TeleportationBukkit.gui.*;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class GetGuiDataMessage extends PluginMessageWithResponse {

    public GetGuiDataMessage(RegistriesProvider registriesProvider, PluginMessenger pluginMessenger, String playerUUID, String title, int page1, int... otherPages) {
        super("gui_data_request", dataInput -> {
            try {
                JSONObject object = new JSONObject(dataInput.readUTF());
                String metaTitle = object.getString("title");
                UUID playerUUIDGui = UUID.fromString(object.getString("player_uuid"));
                JSONArray pagesData = object.getJSONArray("pagesData");

                String group = metaTitle.split("_").length > 0 ? metaTitle.split("_")[0] : metaTitle;
                String titleGui = metaTitle.equals(group) ? group : metaTitle.substring(group.length() + 1);
                Player targetPlayer = Bukkit.getPlayer(playerUUIDGui);
                if (targetPlayer == null || !targetPlayer.isOnline()) return;


                if (registriesProvider.getMultiplePagesGuisRegistry().isRegistered(targetPlayer)) {
                    registriesProvider.getMultiplePagesGuisRegistry().getGui(targetPlayer).addPages(pagesData);
                } else {
                    switch (group) {
                        case "Alle" -> new AllGui(targetPlayer, pluginMessenger, pagesData, registriesProvider).open();
                        case "Städte" ->
                                new CitiesGui(targetPlayer, pluginMessenger, pagesData, registriesProvider).open();
                        case "Tags" -> new TagsGui(targetPlayer, pluginMessenger, pagesData, registriesProvider).open();
                        case "Events" ->
                                new EventsGui(targetPlayer, pluginMessenger, pagesData, registriesProvider).open();
                        case "Plotregionen" ->
                                new PlotsGui(targetPlayer, pluginMessenger, pagesData, registriesProvider).open();
                        case "Normen Hubs" ->
                                new NormenHubsGui(targetPlayer, pluginMessenger, pagesData, registriesProvider).open();
                        case "city" ->
                                new CitiesDetailGui(targetPlayer, titleGui, pluginMessenger, pagesData, registriesProvider).open();
                        case "tag" ->
                                new TagsDetailGui(targetPlayer, titleGui, pluginMessenger, pagesData, registriesProvider).open();
                        case "bl" ->
                                new StatesDetailGui(targetPlayer, titleGui, pluginMessenger, pagesData, registriesProvider).open();
                        case "search" ->
                                new SearchResultsGui(targetPlayer, titleGui, pluginMessenger, pagesData, registriesProvider).open();
                        case "server" ->
                                new ServersDetailGui(targetPlayer, pluginMessenger, pagesData, titleGui, registriesProvider).open();
                        case "lobbywarp" ->
                                new LobbyWarpGui(targetPlayer, titleGui, pluginMessenger, pagesData, registriesProvider).open();
                        case "lobbywarp-around" ->
                                new LobbyWarpAroundGui(targetPlayer, titleGui, pluginMessenger, pagesData, registriesProvider).open();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        StringBuilder builder = new StringBuilder().append(page1);
        for(int page : otherPages) {
            builder.append(", ").append(page);
        }
        super.content.addAll(List.of(
                playerUUID,
                title,
                new String(builder)));
    }

}
