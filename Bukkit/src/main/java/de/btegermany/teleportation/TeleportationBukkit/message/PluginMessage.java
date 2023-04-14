package de.btegermany.teleportation.TeleportationBukkit.message;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class PluginMessage {

    protected ByteArrayDataOutput byteOutput;

    public PluginMessage() {
        this.byteOutput = ByteStreams.newDataOutput();
    }

    public byte[] getBytes() {
        return this.byteOutput.toByteArray();
    }

}
