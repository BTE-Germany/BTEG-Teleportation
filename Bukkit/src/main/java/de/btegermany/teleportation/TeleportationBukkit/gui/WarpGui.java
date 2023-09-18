package de.btegermany.teleportation.TeleportationBukkit.gui;

import com.tchristofferson.pagedinventories.IPagedInventory;
import com.tchristofferson.pagedinventories.NavigationRow;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryClickHandler;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.BlueprintItem;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.BlueprintRange;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.GuiBlueprint;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.SinglePageWarpGuiAbstract;
import de.btegermany.teleportation.TeleportationBukkit.message.GetGuiDataMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class WarpGui extends SinglePageWarpGuiAbstract {

    public WarpGui(Player player, PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {
        super(player, "Warp Kategorien", pluginMessenger, registriesProvider);

        inventory.addHandler(new PagedInventoryClickHandler() {
            @Override
            public void handle(ClickHandler clickHandler) {
                if(clickHandler.getCurrentItem().getItemMeta() == null || clickHandler.getCurrentItem().getItemMeta().getDisplayName() == null || clickHandler.getCurrentItem().getItemMeta().getDisplayName().length() < 3) return;
                switch(clickHandler.getCurrentItem().getItemMeta().getDisplayName().substring(2)) {
                    case "Städte":
                    case "Events":
                    case "Plotregionen":
                    case "Normen Hubs":
                    case "Alle":
                        pluginMessenger.send(new GetGuiDataMessage(player.getUniqueId().toString(), clickHandler.getCurrentItem().getItemMeta().getDisplayName().substring(2), 0, 1));
                        break;
                    case "Bundesländer":
                        new StatesGui(clickHandler.getPlayer(), pluginMessenger, registriesProvider).open();
                        break;
                }
            }
        });
    }

    @Nonnull
    @Override
    public IPagedInventory createInventory() {
        return pagedInventoryAPI.createPagedInventory(new NavigationRow(NAV_NEXT, NAV_PREVIOUS, NAV_CLOSE, NAV_SEARCH));
    }

    @Nonnull
    @Override
    public GuiBlueprint createBlueprint() {
        return new GuiBlueprint()
                .addRow(new BlueprintRange(0, 8, placeholderDefault))
                .addRow(new BlueprintRange(0, placeholderDefault),
                        new BlueprintRange(1, new BlueprintItem()),
                        new BlueprintRange(2, new BlueprintItem()),
                        new BlueprintRange(3, placeholderDefault),
                        new BlueprintRange(4, new BlueprintItem()),
                        new BlueprintRange(5, new BlueprintItem()),
                        new BlueprintRange(6, new BlueprintItem()),
                        new BlueprintRange(7, new BlueprintItem()),
                        new BlueprintRange(8, placeholderDefault)
                )
                .addRow(new BlueprintRange(0, 8, placeholderDefault));
    }

    @Nonnull
    @Override
    public List<ItemStack> getContent() {
        ItemStack itemCities = new ItemStack(Material.STAINED_GLASS, 1, (short) 11);
        ItemMeta metaCities = itemCities.getItemMeta();
        metaCities.setDisplayName("Städte");
        itemCities.setItemMeta(metaCities);
        ItemStack itemStates = new ItemStack(Material.STAINED_GLASS, 1, (short) 4);
        ItemMeta metaStates = itemStates.getItemMeta();
        metaStates.setDisplayName("Bundesländer");
        itemStates.setItemMeta(metaStates);
        ItemStack itemEvents = new ItemStack(Material.STAINED_GLASS, 1, (short) 14);
        ItemMeta metaEvents = itemCities.getItemMeta();
        metaEvents.setDisplayName("Events");
        itemEvents.setItemMeta(metaEvents);
        ItemStack itemPlots = new ItemStack(Material.STAINED_GLASS, 1, (short) 13);
        ItemMeta metaPlots = itemPlots.getItemMeta();
        metaPlots.setDisplayName("Plotregionen");
        itemPlots.setItemMeta(metaPlots);
        ItemStack itemNormen = new ItemStack(Material.STAINED_GLASS, 1, (short) 15);
        ItemMeta metaNormen = itemNormen.getItemMeta();
        metaNormen.setDisplayName("Normen Hubs");
        itemNormen.setItemMeta(metaNormen);
        ItemStack itemAll = new ItemStack(Material.STAINED_GLASS, 1, (short) 0);
        ItemMeta metaAll = itemCities.getItemMeta();
        metaAll.setDisplayName("Alle");
        itemAll.setItemMeta(metaAll);

        return Arrays.asList(itemCities, itemStates, itemEvents, itemPlots, itemNormen, itemAll);
    }
}
