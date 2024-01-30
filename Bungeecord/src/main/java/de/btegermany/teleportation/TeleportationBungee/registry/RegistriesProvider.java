package de.btegermany.teleportation.TeleportationBungee.registry;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.data.Database;

public class RegistriesProvider {

    private final LastLocationsRegistry lastLocationsRegistry;
    private final TpasRegistry tpasRegistry;
    private final BukkitPlayersRegistry bukkitPlayersRegistry;
    private final WarpsRegistry warpsRegistry;
    private final SentCoordinatesFormatWarningRegistry sentCoordinatesFormatWarningRegistry;
    private final WarpTagsRegistry warpTagsRegistry;

    public RegistriesProvider(Database database, TeleportationBungee plugin) {
        this.lastLocationsRegistry = new LastLocationsRegistry();
        this.tpasRegistry = new TpasRegistry();
        this.bukkitPlayersRegistry = new BukkitPlayersRegistry();
        this.warpTagsRegistry = new WarpTagsRegistry();
        this.warpsRegistry = new WarpsRegistry(database, plugin, warpTagsRegistry);
        this.sentCoordinatesFormatWarningRegistry = new SentCoordinatesFormatWarningRegistry();
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

    public SentCoordinatesFormatWarningRegistry getSentCoordinatesFormatWarningRegistry() {
        return sentCoordinatesFormatWarningRegistry;
    }

    public WarpTagsRegistry getWarpTagsRegistry() {
        return warpTagsRegistry;
    }
}
