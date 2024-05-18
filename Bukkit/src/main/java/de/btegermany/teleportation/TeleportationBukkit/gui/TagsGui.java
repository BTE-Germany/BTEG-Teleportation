package de.btegermany.teleportation.TeleportationBukkit.gui;

import com.tchristofferson.pagedinventories.IPagedInventory;
import com.tchristofferson.pagedinventories.NavigationRow;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryClickHandler;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.BlueprintItem;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.BlueprintRange;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.GuiBlueprint;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.MultiplePagesWarpGuiAbstract;
import de.btegermany.teleportation.TeleportationBukkit.message.withresponse.GetGuiDataMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;
import org.bukkit.entity.Player;
import org.json.JSONArray;

import javax.annotation.Nonnull;

public class TagsGui extends MultiplePagesWarpGuiAbstract {

    public TagsGui(Player player, PluginMessenger pluginMessenger, JSONArray pagesData, RegistriesProvider registriesProvider) {
        super(player, "Tags", pluginMessenger, pagesData, Skulls.Skin.EDIT.getId(), registriesProvider);

        inventory.addHandler(new PagedInventoryClickHandler() {
            @Override
            public void handle(ClickHandler clickHandler) {
                if(clickHandler.getCurrentItem().getItemMeta() == null || clickHandler.getCurrentItem().getItemMeta().getDisplayName().length() < 3) return;
                pluginMessenger.send(new GetGuiDataMessage(registriesProvider, pluginMessenger, player.getUniqueId().toString(), "tag_" + clickHandler.getCurrentItem().getItemMeta().getDisplayName().substring(2), 0, 1));
                registriesProvider.getMultiplePagesGuisRegistry().unregister(player);
            }
        });
    }

    @Nonnull
    @Override
    public IPagedInventory createInventory() {
        return pagedInventoryAPI.createPagedInventory(new NavigationRow(NAV_NEXT, NAV_PREVIOUS, NAV_CLOSE, NAV_SORT, NAV_SEARCH, NAV_TP_RANDOM));
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
