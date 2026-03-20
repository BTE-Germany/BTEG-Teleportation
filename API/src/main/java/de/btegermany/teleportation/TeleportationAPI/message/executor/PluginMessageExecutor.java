package de.btegermany.teleportation.TeleportationAPI.message.executor;

import com.google.common.io.ByteArrayDataInput;

public interface PluginMessageExecutor {

    void execute(ByteArrayDataInput dataInput, Integer requestId);

}
