package de.btegermany.teleportation.TeleportationAPI.message;

public class PluginMessageResponse extends PluginMessage {

    public PluginMessageResponse(int id, String label) {
        super(label, MessageType.RESPONSE);
        super.setId(id);
    }

}
