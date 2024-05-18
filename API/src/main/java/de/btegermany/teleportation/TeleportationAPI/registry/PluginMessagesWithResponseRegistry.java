package de.btegermany.teleportation.TeleportationAPI.registry;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessageWithResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class PluginMessagesWithResponseRegistry {

    private final Map<Integer, PluginMessageWithResponse> pluginMessagesWithResponse;

    public PluginMessagesWithResponseRegistry() {
        this.pluginMessagesWithResponse = new HashMap<>();
    }

    public void register(PluginMessageWithResponse pluginMessageWithResponse) {
        int id;
        while(true) {
            if (!this.pluginMessagesWithResponse.containsKey(id = ThreadLocalRandom.current().nextInt())) {
                break;
            }
        }
        pluginMessageWithResponse.setId(id);
        this.pluginMessagesWithResponse.put(id, pluginMessageWithResponse);
    }

    public void unregister(int id) {
        this.pluginMessagesWithResponse.remove(id);
    }

    public PluginMessageWithResponse getPluginMessageWithResponse(int id) {
        return this.pluginMessagesWithResponse.get(id);
    }

}
