package de.btegermany.teleportation.TeleportationBukkit.gui.warp;


import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.gui.PagedGuiHandler;
import de.btegermany.teleportation.TeleportationBukkit.gui.GuiItems;
import de.btegermany.teleportation.TeleportationBukkit.gui.base.StaticCustomGui;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class WarpGui extends StaticCustomGui {

    private static final int FIRST_ROW_CITIES_STATES = 3;
    private static final int FIRST_COL_CITIES = 3;
    private static final int FIRST_COL_STATES = 6;
    private static final int ROW_OTHER = 5;

    public WarpGui(Player player, PagedGuiHandler pagedGuiHandler, PluginMessenger pluginMessenger, TeleportationBukkit plugin) {
        super("Warp Kategorien", "च", player, plugin);

        this.gui.setItem(0, GuiItems.Warps.randomWarpItem(this));
        this.gui.setItem(1, GuiItems.Warps.searchItem(this));

        GuiItem itemCities = GuiItems.emptyItem("Städte", NamedTextColor.AQUA, () -> new CitiesGui(player, pagedGuiHandler, pluginMessenger, plugin));
        this.gui.setItem(FIRST_ROW_CITIES_STATES, FIRST_COL_CITIES, itemCities);
        this.gui.setItem(FIRST_ROW_CITIES_STATES, FIRST_COL_CITIES + 1, itemCities);
        this.gui.setItem(FIRST_ROW_CITIES_STATES + 1, FIRST_COL_CITIES, itemCities);
        this.gui.setItem(FIRST_ROW_CITIES_STATES + 1, FIRST_COL_CITIES + 1, itemCities);

        GuiItem itemStates = GuiItems.emptyItem("Bundesländer", NamedTextColor.AQUA, () -> new StatesGui(player, pagedGuiHandler, pluginMessenger, plugin));
        this.gui.setItem(FIRST_ROW_CITIES_STATES, FIRST_COL_STATES, itemStates);
        this.gui.setItem(FIRST_ROW_CITIES_STATES, FIRST_COL_STATES + 1, itemStates);
        this.gui.setItem(FIRST_ROW_CITIES_STATES + 1, FIRST_COL_STATES, itemStates);
        this.gui.setItem(FIRST_ROW_CITIES_STATES + 1, FIRST_COL_STATES + 1, itemStates);

        this.gui.setItem(ROW_OTHER, 3, GuiItems.emptyItem("Tags", NamedTextColor.YELLOW, () -> new TagsGui(player, pagedGuiHandler, pluginMessenger, plugin)));
        this.gui.setItem(ROW_OTHER, 4, GuiItems.emptyItem("Plotgebiete", NamedTextColor.GREEN, () -> new PlotsGui(player, pagedGuiHandler, pluginMessenger, plugin)));
        this.gui.setItem(ROW_OTHER, 5, GuiItems.emptyItem("Events", NamedTextColor.RED, () -> new EventsGui(player, pagedGuiHandler, pluginMessenger, plugin)));
        this.gui.setItem(ROW_OTHER, 6, GuiItems.emptyItem("Alle Warps", NamedTextColor.LIGHT_PURPLE, () -> new AllGui(player, pagedGuiHandler, pluginMessenger, plugin)));

        this.open();
    }
}
