package de.btegermany.teleportation.TeleportationBukkit.registry;

import java.util.HashSet;
import java.util.Set;

public class CitiesRegistry {

    private final Set<String> cities;

    public CitiesRegistry() {
        this.cities = new HashSet<>();
    }

    public void register(String city) {
        this.cities.add(city);
    }

    public void unregisterAll() {
        this.cities.clear();
    }

    public Set<String> getCities() {
        return cities;
    }
}
