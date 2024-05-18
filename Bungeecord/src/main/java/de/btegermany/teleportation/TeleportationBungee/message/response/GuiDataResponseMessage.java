package de.btegermany.teleportation.TeleportationBungee.message.response;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessageResponse;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONArray;
import org.json.JSONObject;

public class GuiDataResponseMessage extends PluginMessageResponse {

    public GuiDataResponseMessage(int id, ProxiedPlayer player, String title, JSONArray pagesData) {
        super(id, "gui_data");

        JSONObject object = new JSONObject();
        object.put("title", title);
        object.put("player_uuid", player.getUniqueId().toString());
        object.put("pagesData", pagesData);
        super.content.add(object.toString());
    }

}
