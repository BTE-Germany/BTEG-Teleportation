package de.btegermany.teleportation.TeleportationBukkit.listener;

import de.btegermany.teleportation.TeleportationBukkit.message.LastLocationMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.tp.PendingTeleportationAbstract;
import de.btegermany.teleportation.TeleportationBukkit.tp.TeleportationHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;


public class PlayerJoinListener implements Listener {

	private final TeleportationHandler teleportationHandler;
	private final PluginMessenger pluginMessenger;

	public PlayerJoinListener(TeleportationHandler teleportationHandler, PluginMessenger pluginMessenger) {
		this.teleportationHandler = teleportationHandler;
		this.pluginMessenger = pluginMessenger;
	}

	@EventHandler
	public void onPlayerJoin(PlayerLoginEvent event) {
		
		Player player = event.getPlayer();

		if(teleportationHandler.getPendingTps().containsKey(player.getUniqueId())) {
			PendingTeleportationAbstract teleportation = teleportationHandler.getPendingTps().get(player.getUniqueId());
			if(teleportation.canTeleport() && teleportation.isValid()) {
				pluginMessenger.send(new LastLocationMessage(teleportation.getPlayerUUID()));
				teleportation.teleport();
			}
			teleportationHandler.getPendingTps().remove(teleportation.getPlayerUUID());
		}
	}
	
}
