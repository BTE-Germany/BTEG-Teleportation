package de.btegermany.teleportation.TeleportationBukkit.gui.base;

import org.bukkit.entity.Player;

public abstract class CustomGui {

    protected static final int ROWS_COUNT = 6;

    protected final Player player;

    public CustomGui(Player player) {
        this.player = player;
    }

    public abstract void open();

    public abstract void close();

    public Player getPlayer() {
        return player;
    }
}
