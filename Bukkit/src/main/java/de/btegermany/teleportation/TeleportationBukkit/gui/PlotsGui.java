package de.btegermany.teleportation.TeleportationBukkit.gui;

import com.tchristofferson.pagedinventories.IPagedInventory;
import com.tchristofferson.pagedinventories.NavigationRow;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.*;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;

import javax.annotation.Nonnull;

public class PlotsGui extends MultiPageDetailWarpGuiAbstract {

    public PlotsGui(MultiPageGuiArgs guiArgs) {
        super(guiArgs, "Plotgebiete", Skulls.Skin.WARP_HOUSE.getId());
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
