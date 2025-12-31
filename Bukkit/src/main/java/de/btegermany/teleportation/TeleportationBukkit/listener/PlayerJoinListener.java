package de.btegermany.teleportation.TeleportationBukkit.listener;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.tp.PendingTeleportationAbstract;
import de.btegermany.teleportation.TeleportationBukkit.tp.TeleportationHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;


public class PlayerJoinListener implements Listener {

	private final TeleportationBukkit plugin;
	private final TeleportationHandler teleportationHandler;

	public PlayerJoinListener(TeleportationBukkit plugin, TeleportationHandler teleportationHandler, PluginMessenger pluginMessenger) {
		this.plugin = plugin;
		this.teleportationHandler = teleportationHandler;
	}

	@EventHandler
	public void onPlayerJoin(PlayerSpawnLocationEvent event) {
		Player player = event.getPlayer();

		//cleanup expired tps
		if (this.teleportationHandler.getPendingTps().size() > 15) {
			this.teleportationHandler.getPendingTps().forEach((playerUUID, pendingTeleportation) -> {
				if (pendingTeleportation.isValid()) {
					return;
				}
				this.teleportationHandler.getPendingTps().remove(playerUUID);
			});
		}

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
			Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
				boolean success = teleportation.teleport();
				if (!success) {
					this.plugin.getLogger().severe("Teleporting player %s from server %s failed".formatted(player.getName(), teleportation.getOriginServerName()));
				}
			}, 3);
		}
		this.teleportationHandler.getPendingTps().remove(teleportation.getPlayerUUID());
	}
	
}
