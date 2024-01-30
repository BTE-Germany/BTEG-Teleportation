package de.btegermany.teleportation.TeleportationBungee.registry;

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

    public void unregister(String tag) {
        this.tags.removeIf(existingTag -> existingTag.equalsIgnoreCase(tag));
    }

    public Set<String> getTags() {
        return tags;
    }
}
