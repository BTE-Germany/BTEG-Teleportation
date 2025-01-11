package de.btegermany.teleportation.TeleportationBukkit.gui;

import com.tchristofferson.pagedinventories.IPagedInventory;
import com.tchristofferson.pagedinventories.NavigationRow;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryClickHandler;
import com.tchristofferson.pagedinventories.handlers.PagedInventorySwitchPageHandler;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.*;
import de.btegermany.teleportation.TeleportationBukkit.message.withresponse.GetGuiDataMessage;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;

import javax.annotation.Nonnull;

public class CitiesGui extends MultiPageWarpGuiAbstract {

    public CitiesGui(MultiPageGuiArgs guiArgs) {
        super(guiArgs, "Städte", Skulls.Skin.CITY_HOUSE.getId());

        inventory.addHandler(new PagedInventoryClickHandler() {
            @Override
            public void handle(ClickHandler clickHandler) {
                if(clickHandler.getCurrentItem() == null || clickHandler.getCurrentItem().getItemMeta() == null || clickHandler.getCurrentItem().getItemMeta().getDisplayName().length() < 3) return;
                pluginMessenger.send(new GetGuiDataMessage(registriesProvider, pluginMessenger, player.getUniqueId().toString(), "city_" + clickHandler.getCurrentItem().getItemMeta().getDisplayName().substring(2), 0, 1));
                registriesProvider.getMultiplePagesGuisRegistry().unregister(player);
            }
        });
        inventory.addHandler(new PagedInventorySwitchPageHandler() {
            @Override
            public void handle(SwitchHandler switchHandler) {
                if(!switchHandler.getPageAction().equals(PageAction.NEXT)) return;
                pluginMessenger.send(new GetGuiDataMessage(registriesProvider, pluginMessenger, player.getUniqueId().toString(), "Städte", inventory.getSize()));
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
