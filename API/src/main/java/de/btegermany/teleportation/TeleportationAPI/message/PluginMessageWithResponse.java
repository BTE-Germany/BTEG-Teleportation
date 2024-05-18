package de.btegermany.teleportation.TeleportationAPI.message;

import java.io.DataInputStream;
import java.util.function.Consumer;

public class PluginMessageWithResponse extends PluginMessage {

    private final Consumer<DataInputStream> consumer;

    public PluginMessageWithResponse(String label, Consumer<DataInputStream> consumer) {
        super(label, MessageType.WITH_RESPONSE);
        this.consumer = consumer;
    }

    public void accept(DataInputStream dataInput) {
        if(this.consumer == null) {
            return;
        }
        this.consumer.accept(dataInput);
    }

}
