package de.jaskerx.btegteleportation.bukkit.listeners;

import java.time.LocalTime;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import de.jaskerx.btegteleportation.bukkit.main.Main;
import de.jaskerx.btegteleportation.bukkit.main.PendingTpPlayer;

public class PluginMsgListener implements PluginMessageListener {
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		
		if(channel.equals("Teleportation")) {
			
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String pUUID = in.readUTF();
			String tUUID = in.readUTF();
			boolean sameServer = Boolean.parseBoolean(in.readUTF());
			
			for(Player t : Bukkit.getOnlinePlayers()) {
				if(t.getUniqueId().toString().equals(tUUID)) {
					if(sameServer) {
						for(Player pl : Bukkit.getOnlinePlayers()) {
							if(pl.getUniqueId().equals(UUID.fromString(pUUID))) {
								pl.teleport(t);
							}
						}
					} else {
						Main.pendingTps.put(UUID.fromString(pUUID), new PendingTpPlayer(Bukkit.getPlayer(UUID.fromString(tUUID))));
					}
				}
			}
		}
	}

}
