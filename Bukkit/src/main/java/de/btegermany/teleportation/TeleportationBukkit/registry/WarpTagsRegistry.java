package de.btegermany.teleportation.TeleportationBukkit.registry;

import java.util.HashSet;
import java.util.Set;

public class WarpTagsRegistry {

    private final Set<String> tags;

    public WarpTagsRegistry() {
        this.tags = new HashSet<>();
    }

    public void register(String tag) {
        this.tags.add(tag);
    }

    public void unregisterAll() {
        this.tags.clear();
    }

    public Set<String> getTags() {
        return tags;
    }
}
