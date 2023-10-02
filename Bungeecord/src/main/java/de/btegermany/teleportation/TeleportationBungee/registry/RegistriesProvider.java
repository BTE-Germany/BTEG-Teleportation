package de.btegermany.teleportation.TeleportationBungee.registry;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.data.Database;

public class RegistriesProvider {

    private final LastLocationsRegistry lastLocationsRegistry;
    private final TpasRegistry tpasRegistry;
    private final BukkitPlayersRegistry bukkitPlayersRegistry;
    private final WarpsRegistry warpsRegistry;

    public RegistriesProvider(Database database, TeleportationBungee plugin) {
        this.lastLocationsRegistry = new LastLocationsRegistry();
        this.tpasRegistry = new TpasRegistry();
        this.bukkitPlayersRegistry = new BukkitPlayersRegistry();
        this.warpsRegistry = new WarpsRegistry(database, plugin);
    }

    public LastLocationsRegistry getLastLocationsRegistry() {
        return lastLocationsRegistry;
    }

    public TpasRegistry getTpasRegistry() {
        return tpasRegistry;
    }

    public BukkitPlayersRegistry getBukkitPlayersRegistry() {
        return bukkitPlayersRegistry;
    }

    public WarpsRegistry getWarpsRegistry() {
        return warpsRegistry;
    }
}
