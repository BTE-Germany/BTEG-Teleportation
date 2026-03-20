package de.btegermany.teleportation.TeleportationAPI.message.executor;

import com.google.common.io.ByteArrayDataInput;

public interface PluginMessageNormalExecutor extends PluginMessageExecutor {

    @Override
    default void execute(ByteArrayDataInput dataInput, Integer requestId) {
        this.execute(dataInput);
    }

    void execute(ByteArrayDataInput dataInput);
    
}
