package de.btegermany.teleportation.TeleportationBukkit.gui.lobbywarp;

import de.btegermany.teleportation.TeleportationAPI.PagedGuiType;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.gui.PagedGuiHandler;
import de.btegermany.teleportation.TeleportationBukkit.gui.GuiItems;
import de.btegermany.teleportation.TeleportationBukkit.gui.ItemData;
import de.btegermany.teleportation.TeleportationBukkit.gui.base.PagedCustomGui;
import de.btegermany.teleportation.TeleportationBukkit.message.ExecuteCommandMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.registry.LobbyCitiesRegistry;
import de.btegermany.teleportation.TeleportationBukkit.util.LobbyCity;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class LobbyWarpAroundGui extends PagedCustomGui {

    private final PluginMessenger pluginMessenger;

    public LobbyWarpAroundGui(LobbyCity lobbyCity, Player player, PagedGuiHandler pagedGuiHandler, PluginMessenger pluginMessenger, LobbyCitiesRegistry registry, TeleportationBukkit plugin) {
        super("Umkreis: %s".formatted(lobbyCity.getCity()), PagedGuiType.LOBBY_WARP_AROUND, Skulls.Skin.WARP_HOUSE, player, pagedGuiHandler, plugin, getArgs(lobbyCity));
        this.pluginMessenger = pluginMessenger;

        this.gui.setItem(4, new GuiItem(GuiItems.customModelItemStack("Zurück", NamedTextColor.GOLD, "back"), event -> new LobbyWarpGui(lobbyCity.getCity(), player, pagedGuiHandler, pluginMessenger, registry, plugin)));

        this.open();
    }

    private static String[] getArgs(LobbyCity lobbyCity) {
        return new String[] {
                lobbyCity.getCity(),
                String.valueOf(lobbyCity.getCenterLat()),
                String.valueOf(lobbyCity.getCenterLon()),
                String.valueOf(lobbyCity.getRadius())
        };
    }

    @Override
    protected void onClick(ItemData itemData) {
        this.pluginMessenger.send(new ExecuteCommandMessage(this.player, itemData.getTpllCommand()));
    }

}
