package de.btegermany.teleportation.TeleportationBukkit.listener;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.message.CreateWarpMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.GetWarpInfoMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.util.WarpGettingChanged;
import de.btegermany.teleportation.TeleportationBukkit.message.ChangeWarpMessage;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.util.WarpInCreation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener implements Listener {

    private final PluginMessenger pluginMessenger;
    private final RegistriesProvider registriesProvider;

    public AsyncPlayerChatListener(PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {
        this.pluginMessenger = pluginMessenger;
        this.registriesProvider = registriesProvider;
    }

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // id for deleting warp
        if (registriesProvider.getPlayersEnteringDeleteWarpIdRegistry().isRegistered(player)) {
            event.setCancelled(true);
            if (message.equals("cancel")) {
                registriesProvider.getPlayersEnteringDeleteWarpIdRegistry().unregister(player);
                player.sendMessage(TeleportationBukkit.getFormattedMessage("Der Vorgang wurde abgebrochen."));
                return;
            }
            if (!message.matches("\\d+")) {
                player.sendMessage(TeleportationBukkit.getFormattedErrorMessage("Die Id muss eine Zahl sein!"));
                return;
            }
            pluginMessenger.send(new GetWarpInfoMessage(player, Integer.parseInt(message), 0));
        }

        // create warp
        if (registriesProvider.getWarpsInCreationRegistry().isRegistered(player)) {
            event.setCancelled(true);
            if (message.equals("cancel")) {
                registriesProvider.getWarpsInCreationRegistry().unregister(player);
                player.sendMessage(TeleportationBukkit.getFormattedMessage("Der Vorgang wurde abgebrochen."));
                return;
            }
            WarpInCreation warp = registriesProvider.getWarpsInCreationRegistry().getWarpInCreation(player);
            warp.processInput(message);
            if(!warp.isComplete()) {
                warp.sendCurrentQuestion();
                return;
            }
            pluginMessenger.send(new CreateWarpMessage(warp));
            registriesProvider.getWarpsInCreationRegistry().unregister(player);
        }

        // id for changing warp
        if(registriesProvider.getPlayersEnteringChangeWarpIdRegistry().isRegistered(player)) {
            event.setCancelled(true);
            if (message.equals("cancel")) {
                registriesProvider.getPlayersEnteringChangeWarpIdRegistry().unregister(player);
                player.sendMessage(TeleportationBukkit.getFormattedMessage("Der Vorgang wurde abgebrochen."));
                return;
            }
            if (!message.matches("\\d+")) {
                player.sendMessage(TeleportationBukkit.getFormattedErrorMessage("Die Id muss eine Zahl sein!"));
                return;
            }
            pluginMessenger.send(new GetWarpInfoMessage(player, Integer.parseInt(message), 1));
        }

        // change warp
        if(registriesProvider.getWarpsGettingChangedRegistry().isRegistered(player)) {
            event.setCancelled(true);
            if (message.equals("cancel")) {
                registriesProvider.getWarpsGettingChangedRegistry().unregister(player);
                player.sendMessage(TeleportationBukkit.getFormattedMessage("Der Vorgang wurde abgebrochen."));
                return;
            }
            WarpGettingChanged warp = registriesProvider.getWarpsGettingChangedRegistry().getWarpGettingChanged(player);
            warp.setValue(message);
            pluginMessenger.send(new ChangeWarpMessage(player, warp));
            registriesProvider.getWarpsGettingChangedRegistry().unregister(player);
        }
    }

}
