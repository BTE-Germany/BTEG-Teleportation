package de.btegermany.teleportation.TeleportationBukkit.gui;

import com.tchristofferson.pagedinventories.IPagedInventory;
import com.tchristofferson.pagedinventories.NavigationRow;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryClickHandler;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.*;
import de.btegermany.teleportation.TeleportationBukkit.message.withresponse.GetGuiDataMessage;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class StatesGui extends SinglePageWarpGuiAbstract {

    public StatesGui(GuiArgs guiArgs) {
        super(guiArgs, "Bundesländer");

        inventory.addHandler(new PagedInventoryClickHandler() {
            @Override
            public void handle(ClickHandler clickHandler) {
                if(clickHandler.getCurrentItem().getItemMeta() == null || clickHandler.getCurrentItem().getItemMeta().getDisplayName().length() < 3) return;
                pluginMessenger.send(new GetGuiDataMessage(registriesProvider, pluginMessenger, player.getUniqueId().toString(), "bl_" + clickHandler.getCurrentItem().getItemMeta().getDisplayName().substring(2), 0, 1));
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
                .addRow(new BlueprintRange(0, 8, placeholderDefault))
                .addRow(new BlueprintRange(0, 8, new BlueprintItem()))
                .addRow(new BlueprintRange(0, 6, new BlueprintItem()),
                        new BlueprintRange(7, 8, placeholderDefault))
                .addRow(new BlueprintRange(0, 8, placeholderDefault));
    }

    @Nonnull
    @Override
    public List<ItemStack> getContent() {
        return Arrays.asList(
                getBlItem(Skulls.Skin.BL_BW, "Baden-Württemberg"),
                getBlItem(Skulls.Skin.BL_BY, "Bayern"),
                getBlItem(Skulls.Skin.BL_BE, "Berlin"),
                getBlItem(Skulls.Skin.BL_BB, "Brandenburg"),
                getBlItem(Skulls.Skin.BL_HB, "Bremen"),
                getBlItem(Skulls.Skin.BL_HH, "Hamburg"),
                getBlItem(Skulls.Skin.BL_HE, "Hessen"),
                getBlItem(Skulls.Skin.BL_MV, "Mecklenburg-Vorpommern"),
                getBlItem(Skulls.Skin.BL_NI, "Niedersachsen"),
                getBlItem(Skulls.Skin.BL_NW, "Nordrhein-Westfalen"),
                getBlItem(Skulls.Skin.BL_RP, "Rheinland-Pfalz"),
                getBlItem(Skulls.Skin.BL_SL, "Saarland"),
                getBlItem(Skulls.Skin.BL_SN, "Sachsen"),
                getBlItem(Skulls.Skin.BL_SA, "Sachsen-Anhalt"),
                getBlItem(Skulls.Skin.BL_SH, "Schleswig-Holstein"),
                getBlItem(Skulls.Skin.BL_TH, "Thüringen"));
    }

    private ItemStack getBlItem(Skulls.Skin skin, String name) {
        ItemStack item = Skulls.getSkull(skin);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

}
