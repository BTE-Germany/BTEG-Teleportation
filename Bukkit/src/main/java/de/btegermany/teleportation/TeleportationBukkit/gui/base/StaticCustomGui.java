package de.btegermany.teleportation.TeleportationBukkit.gui.base;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.gui.GuiItems;
import de.btegermany.teleportation.TeleportationBukkit.gui.GuiUtils;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class StaticCustomGui extends CustomGui {

    protected final Gui gui;
    private final TeleportationBukkit plugin;

    public StaticCustomGui(String title, String customData, Player player, TeleportationBukkit plugin) {
        super(player);
        this.plugin = plugin;

        this.gui = Gui.gui()
                .title(GuiUtils.getTitle(title, customData))
                .rows(ROWS_COUNT)
                .disableAllInteractions()
                .create();

        this.gui.setItem(List.of(0, 1, 2, 3, 4, 5, 6, 7), GuiItems.blankItem());
        this.gui.setItem(8, GuiItems.closeItem(this));
    }

    public StaticCustomGui(String title, Player player, TeleportationBukkit plugin) {
        this(title, "ङ", player, plugin);
    }

    @Override
    public void open() {
        Bukkit.getScheduler().runTask(this.plugin, () -> this.gui.open(this.player));
    }

    @Override
    public void close() {
        Bukkit.getScheduler().runTask(this.plugin, () -> this.gui.close(this.player));
    }

}
