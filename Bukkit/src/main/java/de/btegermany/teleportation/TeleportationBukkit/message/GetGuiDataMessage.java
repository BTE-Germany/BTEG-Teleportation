package de.btegermany.teleportation.TeleportationBukkit.message;

public class GetGuiDataMessage extends PluginMessage {

    public GetGuiDataMessage(String playerUUID, String title, int page1, int... otherPages) {
        StringBuilder builder = new StringBuilder().append(page1);
        for(int page : otherPages) {
            builder.append(", ").append(page);
        }
        byteOutput.writeUTF("get_gui_data");
        byteOutput.writeUTF(playerUUID);
        byteOutput.writeUTF(title);
        //byteOutput.writeUTF(filter);
        byteOutput.writeUTF(new String(builder));
    }

}
