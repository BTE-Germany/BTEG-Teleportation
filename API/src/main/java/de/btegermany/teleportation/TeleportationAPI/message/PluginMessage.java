package de.btegermany.teleportation.TeleportationAPI.message;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.ArrayList;
import java.util.List;

public abstract class PluginMessage {

    protected final String label;
    protected final MessageType messageType;
    protected final List<String> content;
    protected int id;

    public PluginMessage(String label, MessageType messageType) {
        this.label = label;
        this.messageType = messageType;
        this.content = new ArrayList<>();
    }


    public void setId(int id) {
        this.id = id;
    }

    public byte[] getBytes() {
        ByteArrayDataOutput byteOutput = ByteStreams.newDataOutput();
        byteOutput.writeUTF(messageType.name());
        if(this.messageType == MessageType.WITH_RESPONSE || this.messageType == MessageType.RESPONSE) {
            byteOutput.writeUTF(String.valueOf(this.id));
        }
        byteOutput.writeUTF(this.label);
        this.content.forEach(byteOutput::writeUTF);
        return byteOutput.toByteArray();
    }

    public enum MessageType {
        NORMAL,
        WITH_RESPONSE,
        RESPONSE
    }

}
