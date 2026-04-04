package de.btegermany.teleportation.TeleportationBukkit.gui.warp;

import de.btegermany.teleportation.TeleportationAPI.PagedGuiType;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.gui.PagedGuiHandler;
import de.btegermany.teleportation.TeleportationBukkit.gui.GuiItems;
import de.btegermany.teleportation.TeleportationBukkit.gui.ItemData;
import de.btegermany.teleportation.TeleportationBukkit.gui.base.PagedCustomGui;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;
import org.bukkit.entity.Player;

public class TagsGui extends PagedCustomGui {

    private final PluginMessenger pluginMessenger;
    private final TeleportationBukkit plugin;

    public TagsGui(Player player, PagedGuiHandler pagedGuiHandler, PluginMessenger pluginMessenger, TeleportationBukkit plugin) {
        super("Tags", PagedGuiType.TAGS, Skulls.Skin.EDIT, player, pagedGuiHandler, plugin);
        this.pluginMessenger = pluginMessenger;
        this.plugin = plugin;

        this.gui.setItem(0, GuiItems.Warps.randomWarpItem(this));
        this.gui.setItem(1, GuiItems.Warps.searchItem(this, plugin));
        this.gui.setItem(3, GuiItems.Warps.homeItem(this, pagedGuiHandler, pluginMessenger, plugin));

        this.open();
    }

    @Override
    protected void onClick(ItemData itemData) {
        new TagGui(itemData.getName(), this.player, this.pagedGuiHandler, this.pluginMessenger, this.plugin);
    }

}
