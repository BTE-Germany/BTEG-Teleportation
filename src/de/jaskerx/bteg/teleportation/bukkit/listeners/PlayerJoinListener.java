package de.jaskerx.bteg.teleportation.bukkit.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.jaskerx.bteg.teleportation.bukkit.main.Main;


public class PlayerJoinListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		Player p = event.getPlayer();
		if(Main.pendingTps.containsKey(p.getUniqueId())) {
			if(Main.pendingTps.get(p.getUniqueId()).isValid()) {
				for(Player t : Bukkit.getOnlinePlayers()) {
					if(t.getUniqueId().equals(Main.pendingTps.get(p.getUniqueId()).getPlayer().getUniqueId())) {
						p.teleport(t);
						Main.pendingTps.remove(p.getUniqueId());
						return;
					}
				}
				p.sendMessage("§b§lBTEG §7» §6Der §6Spieler §6ist §6gerade §6nicht §6online.");
			} else {
				Main.pendingTps.remove(p.getUniqueId());
			}
		}
		if(Main.pendingTpBacks.containsKey(p.getUniqueId())) {
			p.teleport(Main.pendingTpBacks.get(p.getUniqueId()));
			Main.pendingTpBacks.remove(p.getUniqueId());
		}
	}
	
}
