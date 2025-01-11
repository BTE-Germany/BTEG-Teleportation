package de.btegermany.teleportation.TeleportationBukkit.gui.blueprint;

import com.tchristofferson.pagedinventories.handlers.PagedInventoryClickHandler;
import de.btegermany.teleportation.TeleportationBukkit.message.ExecuteCommandMessage;

import java.util.List;
import java.util.Optional;

public abstract class MultiPageDetailWarpGuiAbstract extends MultiPageWarpGuiAbstract {

    public MultiPageDetailWarpGuiAbstract(MultiPageGuiArgs guiArgs, String title, String headId) {
        super(guiArgs, title, headId);
    }

    @Override
    public void onStartup() {
        // teleport to warp on click
        inventory.addHandler(new PagedInventoryClickHandler() {
            @Override
            public void handle(ClickHandler clickHandler) {
                if(clickHandler.getCurrentItem() == null || clickHandler.getCurrentItem().getItemMeta() == null) return;
                List<String> lore = clickHandler.getCurrentItem().getItemMeta().getLore();
                if(lore == null || lore.isEmpty()) return;
                Optional<String> optionalCommand = lore.stream().filter(s -> s.startsWith("/tpll")).findFirst();
                Optional<String> optionalRotation = lore.stream().filter(s -> s.startsWith("Drehung:")).findFirst();
                Optional<String> optionalHeight = lore.stream().filter(s -> s.startsWith("Höhe:")).findFirst();
                if(optionalCommand.isEmpty() || optionalRotation.isEmpty() || optionalHeight.isEmpty()) return;

                // execute tpll command for warp
                String command = optionalCommand.get();
                String[] rotation = optionalRotation.get().substring("Drehung: ".length()).replace(",", "").split(" ");
                double height = Double.parseDouble(optionalHeight.get().substring("Höhe: ".length()));
                pluginMessenger.send(new ExecuteCommandMessage(player.getUniqueId().toString(), command + " " + height + " yaw=" + rotation[0] + " pitch=" + rotation[1]));
            }
        });
    }

}
