package de.jaskerx.btegteleportation.bukkit.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.jaskerx.btegteleportation.bukkit.main.Main;
import de.jaskerx.btegteleportation.bukkit.main.PendingTpPlayer;

public class PluginMsgListener implements PluginMessageListener {
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		
		if(channel.equals("Teleportation")) {
			
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String tag = in.readUTF();
			if(tag.equals("teleport")) {
				UUID pUUID = UUID.fromString(in.readUTF());
				UUID tUUID = UUID.fromString(in.readUTF());
				boolean sameServer = Boolean.parseBoolean(in.readUTF());
				
				for(Player t : Bukkit.getOnlinePlayers()) {
					if(t.getUniqueId().equals(tUUID)) {
						if(sameServer) {
							for(Player pl : Bukkit.getOnlinePlayers()) {
								if(pl.getUniqueId().equals(pUUID)) {
									pl.teleport(t);
								}
							}
						} else {
							Main.pendingTps.put(pUUID, new PendingTpPlayer(Bukkit.getPlayer(tUUID)));
						}
					}
				}
			} else if(tag.equals("teleportLocation")) {
				UUID pUUID = UUID.fromString(in.readUTF());
				String world = in.readUTF();
				String[] coords = in.readUTF().split(",");
				boolean sameServer = Boolean.parseBoolean(in.readUTF());
				Location location = new Location(Bukkit.getServer().getWorld(world), Double.valueOf(coords[0]), Double.valueOf(coords[1]), Double.valueOf(coords[2]), Float.valueOf(coords[3]), Float.valueOf(coords[4]));
				
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(p.getUniqueId().equals(pUUID)) {
						if(sameServer) {
							p.teleport(location);
						} else {
							Main.pendingTpBacks.put(pUUID, location);
						}
					}
				}
			} else if(tag.equals("getLocation")) {
				UUID pUUID = UUID.fromString(in.readUTF());
				String server = in.readUTF();
				
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(p.getUniqueId().equals(pUUID)) {
						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("coords");
						out.writeUTF(pUUID.toString());
						out.writeUTF(server);
						out.writeUTF(p.getWorld().getName());
						out.writeUTF(p.getLocation().getBlockX() + "," + p.getLocation().getBlockY() + "," + p.getLocation().getBlockZ() + "," + p.getLocation().getYaw() + "," + p.getLocation().getPitch());
						
						p.getServer().sendPluginMessage(Main.getInstance(), "Teleportation", out.toByteArray());
					}
				}
			}
		}
	}

}
