package de.btegermany.teleportation.TeleportationAPI.message;

import com.google.common.io.ByteArrayDataInput;

import java.util.function.Consumer;

public class PluginMessageWithResponse extends PluginMessage {

    private final Consumer<ByteArrayDataInput> consumer;

    public PluginMessageWithResponse(String label, Consumer<ByteArrayDataInput> consumer) {
        super(label, MessageType.WITH_RESPONSE);
        this.consumer = consumer;
    }

    public void accept(ByteArrayDataInput dataInput) {
        if (this.consumer == null) {
            return;
        }
        this.consumer.accept(dataInput);
    }

}
