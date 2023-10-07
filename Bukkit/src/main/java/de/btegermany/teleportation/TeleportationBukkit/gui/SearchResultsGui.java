package de.btegermany.teleportation.TeleportationBukkit.gui;

import com.tchristofferson.pagedinventories.IPagedInventory;
import com.tchristofferson.pagedinventories.NavigationRow;
import com.tchristofferson.pagedinventories.handlers.PagedInventorySwitchPageHandler;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.BlueprintItem;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.BlueprintRange;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.GuiBlueprint;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.MultiplePagesDetailWarpGuiAbstract;
import de.btegermany.teleportation.TeleportationBukkit.message.GetGuiDataMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;
import org.bukkit.entity.Player;
import org.json.JSONArray;

import javax.annotation.Nonnull;

public class SearchResultsGui extends MultiplePagesDetailWarpGuiAbstract {

    public SearchResultsGui(Player player, String city, PluginMessenger pluginMessenger, JSONArray contentJSON, RegistriesProvider registriesProvider) {
        super(player, String.format("Suche: %s", city), pluginMessenger, contentJSON, Skulls.Skin.WARP_HOUSE.getId(), registriesProvider);

        inventory.addHandler(new PagedInventorySwitchPageHandler() {
            @Override
            public void handle(SwitchHandler switchHandler) {
                if(!switchHandler.getPageAction().equals(PageAction.NEXT)) return;
                pluginMessenger.send(new GetGuiDataMessage(player.getUniqueId().toString(), String.format("search_%s", city), inventory.getSize()));
            }
        });
    }

    @Nonnull
    @Override
    public IPagedInventory createInventory() {
        return pagedInventoryAPI.createPagedInventory(new NavigationRow(NAV_NEXT, NAV_PREVIOUS, NAV_CLOSE));
    }

    @Nonnull
    @Override
    public GuiBlueprint createBlueprint() {
        return new GuiBlueprint()
                .addRow(new BlueprintRange(0, 8, new BlueprintItem()))
                .addRow(new BlueprintRange(0, 8, new BlueprintItem()))
                .addRow(new BlueprintRange(0, 8, new BlueprintItem()))
                .addRow(new BlueprintRange(0, 8, new BlueprintItem()))
                .addRow(new BlueprintRange(0, 8, new BlueprintItem()));
    }

}
