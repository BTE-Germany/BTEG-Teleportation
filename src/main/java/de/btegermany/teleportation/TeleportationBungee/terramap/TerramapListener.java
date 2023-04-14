package de.btegermany.teleportation.TeleportationBungee.terramap;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class TerramapListener implements Listener {

    @EventHandler
    public void onPostLoginEvent(PostLoginEvent event) {
        PluginHelloPacket helloPacket = new PluginHelloPacket("1.0", PlayerSyncStatus.ENABLED, PlayerSyncStatus.ENABLED, true, true, false, new UUID(0, 0));
        TeleportationBungee.terramapPluginChannel.send(helloPacket, event.getPlayer());
    }

}
