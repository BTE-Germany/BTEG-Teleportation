package de.btegermany.teleportation.TeleportationBukkit.gui;

import com.tchristofferson.pagedinventories.IPagedInventory;
import com.tchristofferson.pagedinventories.NavigationRow;
import com.tchristofferson.pagedinventories.handlers.PagedInventorySwitchPageHandler;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.*;
import de.btegermany.teleportation.TeleportationBukkit.message.withresponse.GetGuiDataMessage;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;

import javax.annotation.Nonnull;

public class StatesDetailGui extends MultiPageDetailWarpGuiAbstract {

    public StatesDetailGui(MultiPageGuiArgs guiArgs, String state) {
        super(guiArgs, state, Skulls.Skin.WARP_HOUSE.getId());

        inventory.addHandler(new PagedInventorySwitchPageHandler() {
            @Override
            public void handle(SwitchHandler switchHandler) {
                if(!switchHandler.getPageAction().equals(PageAction.NEXT)) return;
                pluginMessenger.send(new GetGuiDataMessage(registriesProvider, pluginMessenger, player.getUniqueId().toString(), "bl_" + state, inventory.getSize()));
            }
        });
    }

    @Nonnull
    @Override
    public IPagedInventory createInventory() {
        return pagedInventoryAPI.createPagedInventory(new NavigationRow(NAV_NEXT, NAV_PREVIOUS, NAV_CLOSE, NAV_SORT, NAV_SEARCH));
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
