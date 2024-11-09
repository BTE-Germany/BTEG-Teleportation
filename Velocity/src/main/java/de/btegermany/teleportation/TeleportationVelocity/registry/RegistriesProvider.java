package de.btegermany.teleportation.TeleportationVelocity.registry;

import de.btegermany.teleportation.TeleportationAPI.registry.PluginMessagesWithResponseRegistry;
import de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity;
import de.btegermany.teleportation.TeleportationVelocity.data.Database;
import de.btegermany.teleportation.TeleportationVelocity.util.WarpIdsManager;
import org.slf4j.Logger;

public class RegistriesProvider {

    private final LastLocationsRegistry lastLocationsRegistry;
    private final TpasRegistry tpasRegistry;
    private final BukkitPlayersRegistry bukkitPlayersRegistry;
    private final WarpsRegistry warpsRegistry;
    private final WarpTagsRegistry warpTagsRegistry;
    private final PluginMessagesWithResponseRegistry pluginMessagesWithResponseRegistry;

    public RegistriesProvider(TeleportationVelocity plugin, Database database, Logger logger, WarpIdsManager warpIdsManager) {
        this.lastLocationsRegistry = new LastLocationsRegistry();
        this.tpasRegistry = new TpasRegistry();
        this.bukkitPlayersRegistry = new BukkitPlayersRegistry();
        this.warpTagsRegistry = new WarpTagsRegistry(plugin, database);
        this.warpsRegistry = new WarpsRegistry(plugin, logger, database, warpIdsManager, this.warpTagsRegistry);
        this.pluginMessagesWithResponseRegistry = new PluginMessagesWithResponseRegistry();
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

    public WarpTagsRegistry getWarpTagsRegistry() {
        return warpTagsRegistry;
    }

    public PluginMessagesWithResponseRegistry getPluginMessagesWithResponseRegistry() {
        return pluginMessagesWithResponseRegistry;
    }
}
