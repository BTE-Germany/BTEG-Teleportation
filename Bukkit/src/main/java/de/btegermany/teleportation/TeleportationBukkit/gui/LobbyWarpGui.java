package de.btegermany.teleportation.TeleportationBukkit.gui;

import com.tchristofferson.pagedinventories.IPagedInventory;
import com.tchristofferson.pagedinventories.NavigationRow;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryCustomNavigationHandler;
import com.tchristofferson.pagedinventories.navigationitems.CustomNavigationItem;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.BlueprintItem;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.BlueprintRange;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.GuiBlueprint;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.MultiplePagesDetailWarpGuiAbstract;
import de.btegermany.teleportation.TeleportationBukkit.message.GetGuiDataMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.util.LobbyCity;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;
import org.bukkit.entity.Player;
import org.json.JSONArray;

import javax.annotation.Nonnull;
import java.util.Optional;

public class LobbyWarpGui extends MultiplePagesDetailWarpGuiAbstract {

    public LobbyWarpGui(Player player, String city, PluginMessenger pluginMessenger, JSONArray contentJSON, RegistriesProvider registriesProvider) {
        super(player, city, pluginMessenger, contentJSON, Skulls.Skin.WARP_HOUSE.getId(), registriesProvider);
    }

    @Nonnull
    @Override
    public IPagedInventory createInventory() {
        return pagedInventoryAPI.createPagedInventory(new NavigationRow(NAV_NEXT, NAV_PREVIOUS, NAV_CLOSE,
                new CustomNavigationItem(NAV_LOBBY_AROUND.getItemStack(), NAV_LOBBY_AROUND.getSlot()) {
                    @Override
                    public void handleClick(PagedInventoryCustomNavigationHandler handler) {
                        Optional<LobbyCity> lobbyCityOptional = TeleportationBukkit.lobbyCities.stream().filter(lobbyCity1 -> lobbyCity1.getCity().equalsIgnoreCase(title)).findFirst();
                        if(!lobbyCityOptional.isPresent()) {
                            return;
                        }
                        LobbyCity lobbyCity = lobbyCityOptional.get();
                        pluginMessenger.send(new GetGuiDataMessage(player.getUniqueId().toString(), String.format("lobbywarp-around_" + lobbyCity.getCity() + "_" + lobbyCity.getCenterLat() + "_" + lobbyCity.getCenterLon() + "_" + lobbyCity.getRadius()), 0, 1));
                        player.closeInventory();
                    }
                }
        ));
    }

    @Nonnull
    @Override
    public GuiBlueprint createBlueprint() {
        return new GuiBlueprint()
                .addRow(new BlueprintRange(0, 8, new BlueprintItem()))
                .addRow(new BlueprintRange(0, 8, new BlueprintItem()))
                .addRow(new BlueprintRange(0, 8, new BlueprintItem()))
                .addRow(new BlueprintRange(0, 8, new BlueprintItem()))
                .addRow(new BlueprintRange(0, 8, new BlueprintItem()));
    }

}
