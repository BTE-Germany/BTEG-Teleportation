package de.btegermany.teleportation.TeleportationBukkit.gui;

import com.tchristofferson.pagedinventories.IPagedInventory;
import com.tchristofferson.pagedinventories.NavigationRow;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryClickHandler;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.BlueprintItem;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.GuiBlueprint;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.SinglePageWarpGuiAbstract;
import de.btegermany.teleportation.TeleportationBukkit.message.GetGuiDataMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.BlueprintRange;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class ServersGui extends SinglePageWarpGuiAbstract {

    public ServersGui(Player player, PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {
        super(player, "Server", pluginMessenger, registriesProvider);

        inventory.addHandler(new PagedInventoryClickHandler() {
            @Override
            public void handle(ClickHandler clickHandler) {
                if(clickHandler.getCurrentItem().getItemMeta() == null || clickHandler.getCurrentItem().getItemMeta().getDisplayName().length() < 3) return;
                String server = clickHandler.getCurrentItem().getItemMeta().getDisplayName().substring(2);
                pluginMessenger.send(new GetGuiDataMessage(player.getUniqueId().toString(), "server_" + server, 0, 1));
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
                .addRow(new BlueprintRange(0, 8, placeholderDefault))
                .addRow(new BlueprintRange(0, 1, placeholderDefault),
                        new BlueprintRange(2, new BlueprintItem()),
                        new BlueprintRange(3, placeholderDefault),
                        new BlueprintRange(4, new BlueprintItem()),
                        new BlueprintRange(5, placeholderDefault),
                        new BlueprintRange(6, new BlueprintItem()),
                        new BlueprintRange(7, 8, placeholderDefault)
                )
                .addRow(new BlueprintRange(0, 8, placeholderDefault))
                .addRow(new BlueprintRange(0, 1, placeholderDefault),
                        new BlueprintRange(2, new BlueprintItem()),
                        new BlueprintRange(3, placeholderDefault),
                        new BlueprintRange(4, new BlueprintItem()),
                        new BlueprintRange(5, placeholderDefault),
                        new BlueprintRange(6, new BlueprintItem()),
                        new BlueprintRange(7, 8, placeholderDefault)
                )
                .addRow(new BlueprintRange(0, 8, placeholderDefault));
    }

    @Nonnull
    @Override
    public List<ItemStack> getContent() {
        return Arrays.asList(
                getItem("Terra-1"),
                getItem("Terra-2"),
                getItem("Terra-3"),
                getItem("Terra-4"),
                getItem("Terra-5"),
                getItem("Terra-6"));
    }

    private ItemStack getItem(String server) {
        ItemStack item;
        switch (server) {
            case "Terra-1": item = Skulls.getSkull(Skulls.Skin.ONE);
                break;
            case "Terra-2": item = Skulls.getSkull(Skulls.Skin.TWO);
                break;
            case "Terra-3": item = Skulls.getSkull(Skulls.Skin.THREE);
                break;
            case "Terra-4": item = Skulls.getSkull(Skulls.Skin.FOUR);
                break;
            case "Terra-5": item = Skulls.getSkull(Skulls.Skin.FIVE);
                break;
            default: item = Skulls.getSkull(Skulls.Skin.SIX);
                break;
        }
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(server);
        item.setItemMeta(meta);
        return item;
    }

}
