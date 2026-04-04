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

import java.util.Optional;


public class LobbyWarpGui  extends PagedCustomGui {

    private final String city;
    private final PluginMessenger pluginMessenger;
    private final LobbyCitiesRegistry registry;

    public LobbyWarpGui(String city, Player player, PagedGuiHandler pagedGuiHandler, PluginMessenger pluginMessenger, LobbyCitiesRegistry registry, TeleportationBukkit plugin) {
        super(city, PagedGuiType.LOBBY_WARP, Skulls.Skin.WARP_HOUSE, player, pagedGuiHandler, plugin, city);
        this.city = city;
        this.pluginMessenger = pluginMessenger;
        this.registry = registry;

        this.gui.setItem(4, new GuiItem(GuiItems.customModelItemStack("Warps im Umkreis", NamedTextColor.GOLD, "map"), event -> {
            Optional<LobbyCity> lobbyCityOptional = this.registry.getLobbyCities().stream().filter(lobbyCity1 -> lobbyCity1.getCity().equalsIgnoreCase(this.city)).findFirst();
            if (lobbyCityOptional.isEmpty()) {
                this.close();
                return;
            }
            LobbyCity lobbyCity = lobbyCityOptional.get();
            new LobbyWarpAroundGui(lobbyCity, player, pagedGuiHandler, pluginMessenger, registry, plugin);
        }));

        this.open();
    }

    @Override
    protected void onClick(ItemData itemData) {
        this.pluginMessenger.send(new ExecuteCommandMessage(this.player, itemData.getTpllCommand()));
    }

}
