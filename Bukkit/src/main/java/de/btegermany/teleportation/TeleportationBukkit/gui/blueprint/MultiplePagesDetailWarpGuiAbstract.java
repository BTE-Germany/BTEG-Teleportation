package de.btegermany.teleportation.TeleportationBukkit.gui.blueprint;

import com.tchristofferson.pagedinventories.handlers.PagedInventoryClickHandler;
import de.btegermany.teleportation.TeleportationBukkit.message.ExecuteCommandMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import org.bukkit.entity.Player;
import org.json.JSONArray;

import java.util.List;
import java.util.Optional;

public abstract class MultiplePagesDetailWarpGuiAbstract extends MultiplePagesWarpGuiAbstract {

    public MultiplePagesDetailWarpGuiAbstract(Player player, String title, PluginMessenger pluginMessenger, JSONArray contentJSON, String headId, RegistriesProvider registriesProvider) {
        super(player, title, pluginMessenger, contentJSON, headId, registriesProvider);
    }

    @Override
    public void onStartup() {
        inventory.addHandler(new PagedInventoryClickHandler() {
            @Override
            public void handle(ClickHandler clickHandler) {
                if(clickHandler.getCurrentItem() == null || clickHandler.getCurrentItem().getItemMeta() == null) return;
                List<String> lore = clickHandler.getCurrentItem().getItemMeta().getLore();
                if(lore == null || lore.size() == 0) return;
                Optional<String> optionalCommand = lore.stream().filter(s -> s.startsWith("/tpll")).findFirst();
                Optional<String> optionalRotation = lore.stream().filter(s -> s.startsWith("Drehung:")).findFirst();
                Optional<String> optionalHeight = lore.stream().filter(s -> s.startsWith("Höhe:")).findFirst();
                if(!optionalCommand.isPresent() || !optionalRotation.isPresent() || !optionalHeight.isPresent()) return;

                String command = optionalCommand.get();
                String[] rotation = optionalRotation.get().substring("Drehung: ".length()).replace(",", "").split(" ");
                double height = Double.parseDouble(optionalHeight.get().substring("Höhe: ".length()));
                pluginMessenger.send(new ExecuteCommandMessage(player.getUniqueId().toString(), command + " " + height + " yaw=" + rotation[0] + " pitch=" + rotation[1]));
            }
        });
    }

}
