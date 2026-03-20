package de.btegermany.teleportation.TeleportationBukkit.gui;

import de.btegermany.teleportation.TeleportationBukkit.gui.base.PagedCustomGui;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.message.withresponse.GetGuiDataMessage;
import de.btegermany.teleportation.TeleportationBukkit.registry.PagedGuisRegistry;


public class PagedGuiHandler {

    private final PluginMessenger pluginMessenger;
    private final PagedGuisRegistry registry;

    public PagedGuiHandler(PluginMessenger pluginMessenger) {
        this.pluginMessenger = pluginMessenger;
        this.registry = new PagedGuisRegistry();
    }

    public void open(PagedCustomGui gui, String[] args) {
        gui.getGui().open(gui.getPlayer());
        this.registry.register(gui.getPlayer(), gui);

        this.pluginMessenger.send(new GetGuiDataMessage(gui, args, true, 1, 2));
    }

    public void close(PagedCustomGui gui) {
        gui.getGui().close(gui.getPlayer());
        this.registry.unregister(gui.getPlayer());
    }

    public void loadData(PagedCustomGui gui, String[] args, int... pages) {
        this.pluginMessenger.send(new GetGuiDataMessage(gui, args, false, pages));
    }

}
