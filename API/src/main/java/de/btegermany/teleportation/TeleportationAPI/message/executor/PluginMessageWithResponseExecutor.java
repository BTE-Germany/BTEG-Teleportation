package de.btegermany.teleportation.TeleportationAPI.message.executor;

import com.google.common.io.ByteArrayDataInput;

public interface PluginMessageWithResponseExecutor extends PluginMessageExecutor {

    @Override
    default void execute(ByteArrayDataInput dataInput, Integer requestId) {
        this.execute(dataInput, (int) requestId);
    }

    void execute(ByteArrayDataInput dataInput, int requestId);
    
}
