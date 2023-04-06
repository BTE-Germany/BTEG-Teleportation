package de.btegermany.teleportation.TeleportationBungee.registry;

public class RegistriesProvider {

    private final LastLocationsRegistry lastLocationsRegistry;
    private final TpasRegistry tpasRegistry;

    public RegistriesProvider() {
        this.lastLocationsRegistry = new LastLocationsRegistry();
        this.tpasRegistry = new TpasRegistry();
    }

    public LastLocationsRegistry getLastLocationsRegistry() {
        return lastLocationsRegistry;
    }

    public TpasRegistry getTpasRegistry() {
        return tpasRegistry;
    }

}
