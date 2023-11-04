package de.btegermany.teleportation.TeleportationBukkit.listener;

import de.btegermany.teleportation.TeleportationBukkit.message.LastLocationMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.tp.PendingTeleportationAbstract;
import de.btegermany.teleportation.TeleportationBukkit.tp.TeleportationHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class PlayerJoinListener implements Listener {

	private final TeleportationHandler teleportationHandler;
	private final PluginMessenger pluginMessenger;

	public PlayerJoinListener(TeleportationHandler teleportationHandler, PluginMessenger pluginMessenger) {
		this.teleportationHandler = teleportationHandler;
		this.pluginMessenger = pluginMessenger;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		// check if there is a teleportation for this player still pending
		if(!this.teleportationHandler.getPendingTps().containsKey(player.getUniqueId())) {
			return;
		}
		PendingTeleportationAbstract teleportation = this.teleportationHandler.getPendingTps().get(player.getUniqueId());
		// teleport if possible
		if(!teleportation.canTeleport()) {
			return;
		}
		if(teleportation.isValid()) {
			this.pluginMessenger.send(new LastLocationMessage(teleportation.getPlayerUUID()));
			teleportation.teleport();
		}
		this.teleportationHandler.getPendingTps().remove(teleportation.getPlayerUUID());
	}
	
}
