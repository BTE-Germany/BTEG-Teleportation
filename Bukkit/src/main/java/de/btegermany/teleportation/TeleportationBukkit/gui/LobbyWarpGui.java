package de.btegermany.teleportation.TeleportationBukkit.gui;

import com.tchristofferson.pagedinventories.IPagedInventory;
import com.tchristofferson.pagedinventories.NavigationRow;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryCustomNavigationHandler;
import com.tchristofferson.pagedinventories.navigationitems.CustomNavigationItem;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.*;
import de.btegermany.teleportation.TeleportationBukkit.message.withresponse.GetGuiDataMessage;
import de.btegermany.teleportation.TeleportationBukkit.util.LobbyCity;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;

import javax.annotation.Nonnull;
import java.util.Optional;

public class LobbyWarpGui extends MultiPageDetailWarpGuiAbstract {

    public LobbyWarpGui(MultiPageGuiArgs guiArgs, String city) {
        super(guiArgs, city, Skulls.Skin.WARP_HOUSE.getId());
    }

    @Nonnull
    @Override
    public IPagedInventory createInventory() {
        return pagedInventoryAPI.createPagedInventory(new NavigationRow(NAV_NEXT, NAV_PREVIOUS, NAV_CLOSE,
                new CustomNavigationItem(NAV_LOBBY_AROUND.getItemStack(), NAV_LOBBY_AROUND.getSlot()) {
                    @Override
                    public void handleClick(PagedInventoryCustomNavigationHandler handler) {
                        Optional<LobbyCity> lobbyCityOptional = registriesProvider.getLobbyCitiesRegistry().getLobbyCities().stream().filter(lobbyCity1 -> lobbyCity1.getCity().equalsIgnoreCase(title)).findFirst();
                        if(lobbyCityOptional.isEmpty()) {
                            return;
                        }
                        LobbyCity lobbyCity = lobbyCityOptional.get();
                        pluginMessenger.send(new GetGuiDataMessage(registriesProvider, pluginMessenger, player.getUniqueId().toString(), String.format("lobbywarp-around_" + lobbyCity.getCity() + "_" + lobbyCity.getCenterLat() + "_" + lobbyCity.getCenterLon() + "_" + lobbyCity.getRadius()), 0, 1));
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
