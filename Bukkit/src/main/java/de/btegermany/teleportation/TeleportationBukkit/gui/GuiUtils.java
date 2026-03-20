package de.btegermany.teleportation.TeleportationBukkit.gui;

import dev.triumphteam.gui.guis.BaseGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class GuiUtils {

    public static final int NAVBAR_ROWS = 1;
    private static final String CHAR_REPO_NEG8 = "\uF808";
    private static final String CHAR_REPO_NEG170 = "\uF802\uF808\uF80A\uF80C";

    public static Component getTitle(String title, String customData) {
        return Component.text(CHAR_REPO_NEG8 + customData + CHAR_REPO_NEG170, NamedTextColor.WHITE).append(Component.text(title, NamedTextColor.BLACK));
    }

    public static void fill(BaseGui gui) {
        gui.getFiller().fillBetweenPoints(1 + NAVBAR_ROWS, 1, gui.getRows(), 9, GuiItems.fillerItem());
    }

}
