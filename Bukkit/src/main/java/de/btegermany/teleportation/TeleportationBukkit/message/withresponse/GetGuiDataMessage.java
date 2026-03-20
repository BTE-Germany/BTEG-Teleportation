package de.btegermany.teleportation.TeleportationBukkit.message.withresponse;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessageWithResponse;
import de.btegermany.teleportation.TeleportationBukkit.gui.base.PagedCustomGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public class GetGuiDataMessage extends PluginMessageWithResponse {

    public GetGuiDataMessage(PagedCustomGui gui, String[] args, boolean isOnOpen, int... pages) {
        super("gui_data_request", dataInput -> {
            JSONObject object = new JSONObject(dataInput.readUTF());
            UUID playerUUIDGui = UUID.fromString(object.getString("player_uuid"));
            JSONArray pagesData = object.getJSONArray("pagesData");

            Player targetPlayer = Bukkit.getPlayer(playerUUIDGui);
            if (targetPlayer == null || !targetPlayer.isOnline()) return;

            gui.addPages(pagesData);
            if (isOnOpen) {
                gui.updateNavbar();
                gui.updateTitle();
                return;
            }
            gui.nextPage();
        });

        StringBuilder pagesBuilder = new StringBuilder().append(pages[0]);
        for (int i = 1; i < pages.length; i++) {
            pagesBuilder.append(",").append(pages[i]);
        }

        super.content.addAll(List.of(
                gui.getPlayer().getUniqueId().toString(),
                gui.getType().toString(),
                String.join(",", args),
                pagesBuilder.toString()));
    }

}
