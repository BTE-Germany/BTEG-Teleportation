package de.btegermany.teleportation.TeleportationBungee.registry;

public class RegistriesProvider {

    private final LastLocationsRegistry lastLocationsRegistry;
    private final TpasRegistry tpasRegistry;
    private final BukkitPlayersRegistry bukkitPlayersRegistry;

    public RegistriesProvider() {
        this.lastLocationsRegistry = new LastLocationsRegistry();
        this.tpasRegistry = new TpasRegistry();
        this.bukkitPlayersRegistry = new BukkitPlayersRegistry();
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
}
