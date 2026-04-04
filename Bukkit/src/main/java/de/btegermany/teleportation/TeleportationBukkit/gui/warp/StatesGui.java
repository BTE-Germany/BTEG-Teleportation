package de.btegermany.teleportation.TeleportationBukkit.gui.warp;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.gui.GuiUtils;
import de.btegermany.teleportation.TeleportationBukkit.gui.PagedGuiHandler;
import de.btegermany.teleportation.TeleportationBukkit.gui.GuiItems;
import de.btegermany.teleportation.TeleportationBukkit.gui.base.StaticCustomGui;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StatesGui extends StaticCustomGui {

    private final PluginMessenger pluginMessenger;
    private final PagedGuiHandler pagedGuiHandler;
    private final TeleportationBukkit plugin;

    public StatesGui(Player player, PagedGuiHandler pagedGuiHandler, PluginMessenger pluginMessenger, TeleportationBukkit plugin) {
        super("Bundesländer", player, plugin);
        this.pluginMessenger = pluginMessenger;
        this.pagedGuiHandler = pagedGuiHandler;
        this.plugin = plugin;

        this.gui.setItem(0, GuiItems.Warps.randomWarpItem(this));
        this.gui.setItem(1, GuiItems.Warps.searchItem(this, plugin));
        this.gui.setItem(3, GuiItems.Warps.homeItem(this, pagedGuiHandler, pluginMessenger, plugin));

        GuiUtils.fill(this.gui);

        this.gui.setItem(3, 1, this.getStateItem(Skulls.Skin.BL_BW, "Baden-Württemberg"));
        this.gui.setItem(3, 2, this.getStateItem(Skulls.Skin.BL_BY, "Bayern"));
        this.gui.setItem(3, 3, this.getStateItem(Skulls.Skin.BL_BE, "Berlin"));
        this.gui.setItem(3, 4, this.getStateItem(Skulls.Skin.BL_BB, "Brandenburg"));
        this.gui.setItem(3, 5, this.getStateItem(Skulls.Skin.BL_HB, "Bremen"));
        this.gui.setItem(3, 6, this.getStateItem(Skulls.Skin.BL_HH, "Hamburg"));
        this.gui.setItem(3, 7, this.getStateItem(Skulls.Skin.BL_HE, "Hessen"));
        this.gui.setItem(3, 8, this.getStateItem(Skulls.Skin.BL_MV, "Mecklenburg-Vorpommern"));
        this.gui.setItem(3, 9, this.getStateItem(Skulls.Skin.BL_NI, "Niedersachsen"));
        this.gui.setItem(4, 1, this.getStateItem(Skulls.Skin.BL_NW, "Nordrhein-Westfalen"));
        this.gui.setItem(4, 2, this.getStateItem(Skulls.Skin.BL_RP, "Rheinland-Pfalz"));
        this.gui.setItem(4, 3, this.getStateItem(Skulls.Skin.BL_SL, "Saarland"));
        this.gui.setItem(4, 4, this.getStateItem(Skulls.Skin.BL_SN, "Sachsen"));
        this.gui.setItem(4, 5, this.getStateItem(Skulls.Skin.BL_SA, "Sachsen-Anhalt"));
        this.gui.setItem(4, 6, this.getStateItem(Skulls.Skin.BL_SH, "Schleswig-Holstein"));
        this.gui.setItem(4, 7, this.getStateItem(Skulls.Skin.BL_TH, "Thüringen"));

        this.open();
    }

    private GuiItem getStateItem(Skulls.Skin skin, String name) {
        ItemStack item = Skulls.getSkull(skin);
        ItemMeta meta = item.getItemMeta();
        meta.customName(Component.text(name));
        item.setItemMeta(meta);
        return new GuiItem(item, event -> new StateGui(name, this.player, this.pagedGuiHandler, this.pluginMessenger, this.plugin));
    }

}
