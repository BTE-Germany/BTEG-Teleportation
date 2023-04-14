package de.btegermany.teleportation.TeleportationBukkit.registry;

public class RegistriesProvider {

    private final MultiplePagesGuisRegistry multiplePagesGuisRegistry;
    private final PlayersEnteringDeleteWarpIdRegistry playersEnteringDeleteWarpIdRegistry;
    private final WarpsInCreationRegistry warpsInCreationRegistry;
    private final PlayersEnteringChangeWarpIdRegistry playersEnteringChangeWarpIdRegistry;
    private final WarpsGettingChangedRegistry warpsGettingChangedRegistry;

    public RegistriesProvider() {
        this.multiplePagesGuisRegistry = new MultiplePagesGuisRegistry();
        this.playersEnteringDeleteWarpIdRegistry = new PlayersEnteringDeleteWarpIdRegistry();
        this.warpsInCreationRegistry = new WarpsInCreationRegistry();
        this.playersEnteringChangeWarpIdRegistry = new PlayersEnteringChangeWarpIdRegistry();
        this.warpsGettingChangedRegistry = new WarpsGettingChangedRegistry();
    }

    // Getters

    public MultiplePagesGuisRegistry getMultiplePagesGuisRegistry() {
        return multiplePagesGuisRegistry;
    }

    public PlayersEnteringDeleteWarpIdRegistry getPlayersEnteringDeleteWarpIdRegistry() {
        return playersEnteringDeleteWarpIdRegistry;
    }

    public WarpsInCreationRegistry getWarpsInCreationRegistry() {
        return warpsInCreationRegistry;
    }

    public PlayersEnteringChangeWarpIdRegistry getPlayersEnteringChangeWarpIdRegistry() {
        return playersEnteringChangeWarpIdRegistry;
    }

    public WarpsGettingChangedRegistry getWarpsGettingChangedRegistry() {
        return warpsGettingChangedRegistry;
    }

}
