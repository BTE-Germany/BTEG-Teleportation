package de.btegermany.teleportation.TeleportationVelocity.registry;

import de.btegermany.teleportation.TeleportationAPI.registry.PluginMessagesWithResponseRegistry;
import de.btegermany.teleportation.TeleportationVelocity.data.Database;
import de.btegermany.teleportation.TeleportationVelocity.util.WarpIdsManager;
import lombok.Getter;
import org.slf4j.Logger;

@Getter
public class RegistriesProvider {

    private final LastLocationsRegistry lastLocationsRegistry;
    private final TpasRegistry tpasRegistry;
    private final BukkitPlayersRegistry bukkitPlayersRegistry;
    private final WarpsRegistry warpsRegistry;
    private final WarpTagsRegistry warpTagsRegistry;
    private final PluginMessagesWithResponseRegistry pluginMessagesWithResponseRegistry;

    public RegistriesProvider(Database database, Logger logger, WarpIdsManager warpIdsManager) {
        this.lastLocationsRegistry = new LastLocationsRegistry();
        this.tpasRegistry = new TpasRegistry();
        this.bukkitPlayersRegistry = new BukkitPlayersRegistry();
        this.warpTagsRegistry = new WarpTagsRegistry(database);
        this.warpsRegistry = new WarpsRegistry(logger, database, warpIdsManager, this.warpTagsRegistry);
        this.pluginMessagesWithResponseRegistry = new PluginMessagesWithResponseRegistry();
    }

}
