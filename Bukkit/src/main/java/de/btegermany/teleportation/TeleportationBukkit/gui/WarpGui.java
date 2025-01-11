package de.btegermany.teleportation.TeleportationBukkit.gui;

import com.tchristofferson.pagedinventories.IPagedInventory;
import com.tchristofferson.pagedinventories.NavigationRow;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryClickHandler;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.*;
import de.btegermany.teleportation.TeleportationBukkit.message.withresponse.GetGuiDataMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class WarpGui extends SinglePageWarpGuiAbstract {

    public WarpGui(GuiArgs guiArgs) {
        super(guiArgs, "Warp Kategorien");

        inventory.addHandler(new PagedInventoryClickHandler() {
            @Override
            public void handle(ClickHandler clickHandler) {
                if(clickHandler.getCurrentItem() == null || clickHandler.getCurrentItem().getItemMeta() == null || clickHandler.getCurrentItem().getItemMeta().getDisplayName().length() < 3) return;
                switch (clickHandler.getCurrentItem().getItemMeta().getDisplayName().substring(2)) {
                    case "Tags", "Städte", "Events", "Plotregionen", "Normen Hubs", "Alle" ->
                            pluginMessenger.send(new GetGuiDataMessage(registriesProvider, pluginMessenger, player.getUniqueId().toString(), clickHandler.getCurrentItem().getItemMeta().getDisplayName().substring(2), 0, 1));
                    case "Bundesländer" ->
                            new StatesGui(new GuiArgs(clickHandler.getPlayer(), pluginMessenger, registriesProvider)).open();
                }
            }
        });
    }

    @Nonnull
    @Override
    public IPagedInventory createInventory() {
        return pagedInventoryAPI.createPagedInventory(new NavigationRow(NAV_NEXT, NAV_PREVIOUS, NAV_CLOSE, NAV_SEARCH, NAV_TP_RANDOM));
    }

    @Nonnull
    @Override
    public GuiBlueprint createBlueprint() {
        return new GuiBlueprint()
                .addRow(new BlueprintRange(0, 8, placeholderDefault))
                .addRow(new BlueprintRange(0, placeholderDefault),
                        new BlueprintRange(1, 2, new BlueprintItem()),
                        new BlueprintRange(3, placeholderDefault),
                        new BlueprintRange(4, 7, new BlueprintItem()),
                        new BlueprintRange(8, placeholderDefault)
                )
                .addRow(new BlueprintRange(0, 8, placeholderDefault));
    }

    @Nonnull
    @Override
    public List<ItemStack> getContent() {
        ItemStack itemCities = new ItemStack(Material.PAPER);
        ItemMeta metaCities = itemCities.getItemMeta();
        metaCities.setDisplayName("Städte");
        metaCities.setCustomModelData(7);
        itemCities.setItemMeta(metaCities);
        ItemStack itemStates = new ItemStack(Material.PAPER);
        ItemMeta metaStates = itemStates.getItemMeta();
        metaStates.setDisplayName("Bundesländer");
        metaStates.setCustomModelData(3);
        itemStates.setItemMeta(metaStates);
        ItemStack itemTags = new ItemStack(Material.NAME_TAG);
        ItemMeta metaTags = itemCities.getItemMeta();
        metaTags.setDisplayName("Tags");
        itemTags.setItemMeta(metaTags);
        ItemStack itemEvents = new ItemStack(Material.PAPER);
        ItemMeta metaEvents = itemCities.getItemMeta();
        metaEvents.setDisplayName("Events");
        metaEvents.setCustomModelData(12);
        itemEvents.setItemMeta(metaEvents);
        ItemStack itemPlots = new ItemStack(Material.PAPER);
        ItemMeta metaPlots = itemPlots.getItemMeta();
        metaPlots.setDisplayName("Plotregionen");
        metaPlots.setCustomModelData(2);
        itemPlots.setItemMeta(metaPlots);
        ItemStack itemAll = new ItemStack(Material.PAPER);
        ItemMeta metaAll = itemCities.getItemMeta();
        metaAll.setDisplayName("Alle");
        metaAll.setCustomModelData(4);
        itemAll.setItemMeta(metaAll);

        return Arrays.asList(itemCities, itemStates, itemTags, itemEvents, itemPlots, itemAll);
    }
}
