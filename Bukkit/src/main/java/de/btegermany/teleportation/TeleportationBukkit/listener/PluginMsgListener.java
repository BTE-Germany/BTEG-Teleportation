package de.btegermany.teleportation.TeleportationBukkit.listener;

import de.btegermany.teleportation.TeleportationAPI.message.PluginMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.response.LastLocationResponseMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.registry.CitiesRegistry;
import de.btegermany.teleportation.TeleportationBukkit.registry.WarpTagsRegistry;
import de.btegermany.teleportation.TeleportationBukkit.tp.PendingTpPlayer;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.tp.PendingTpLocation;
import de.btegermany.teleportation.TeleportationBukkit.tp.TeleportationHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.util.NumberConversions;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class PluginMsgListener implements PluginMessageListener {

	private final TeleportationHandler teleportationHandler;
	private final PluginMessenger pluginMessenger;
	private final RegistriesProvider registriesProvider;

	public PluginMsgListener(TeleportationHandler teleportationHandler, PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {
		this.teleportationHandler = teleportationHandler;
		this.pluginMessenger = pluginMessenger;
		this.registriesProvider = registriesProvider;
	}

	@Override
	public void onPluginMessageReceived(@Nonnull String channel, @Nonnull Player player, @Nonnull byte[] message) {

		if(!channel.equals(TeleportationBukkit.PLUGIN_CHANNEL)) {
			return;
		}

		DataInputStream dataInput = new DataInputStream(new ByteArrayInputStream(message));

		try {
			PluginMessage.MessageType messageType = PluginMessage.MessageType.valueOf(dataInput.readUTF());
			int requestId = -1;
			if(messageType == PluginMessage.MessageType.WITH_RESPONSE || messageType == PluginMessage.MessageType.RESPONSE) {
				requestId = Integer.parseInt(dataInput.readUTF());
			}
			String tag = dataInput.readUTF();

			switch (tag) {

				case "teleport_player" -> {
					UUID playerUUID = UUID.fromString(dataInput.readUTF());
					UUID targetUUID = UUID.fromString(dataInput.readUTF());
					String originServerName = dataInput.readUTF();

					teleportationHandler.handle(new PendingTpPlayer(playerUUID, targetUUID, originServerName));
				}

				case "teleport_coords" -> {
					UUID playerUUID = UUID.fromString(dataInput.readUTF());
					String[] coords = dataInput.readUTF().split(",");
					double x = Double.parseDouble(coords[0]);
					double y = Double.parseDouble(coords[1]);
					double z = Double.parseDouble(coords[2]);
					// default value null. Otherwise, use the set float value
					Float yaw = Optional.of(dataInput.readUTF()).filter(str -> !str.equals("null")).map(Float::parseFloat).orElse(null);
					Float pitch = Optional.of(dataInput.readUTF()).filter(str -> !str.equals("null")).map(Float::parseFloat).orElse(null);
					String originServerName = dataInput.readUTF();
					World world = Bukkit.getWorld("world");
					if (world == null) {
						world = Bukkit.getWorlds().get(0);
					}
					if (Double.isNaN(y) || !NumberConversions.isFinite(y)) {
						y = world.getHighestBlockYAt((int) x, (int) z) + 1;
					}

					teleportationHandler.handle(new PendingTpLocation(playerUUID, world, x, y, z, yaw, pitch, originServerName));
				}

				case "gui_data" -> {
					this.registriesProvider.getPluginMessagesWithResponseRegistry().getPluginMessageWithResponse(requestId).accept(dataInput);
				}

				case "warp_info" -> {
					UUID playerUUID = UUID.fromString(dataInput.readUTF());
					int responseNumber = Integer.parseInt(dataInput.readUTF());
					int id = Integer.parseInt(dataInput.readUTF());
					String name = dataInput.readUTF();
					String city = dataInput.readUTF();
					String state = dataInput.readUTF();
					double latitude = Double.parseDouble(dataInput.readUTF());
					double longitude = Double.parseDouble(dataInput.readUTF());
					String headId = dataInput.readUTF();
					if (headId.equals("null")) {
						headId = null;
					}
					float yaw = Float.parseFloat(dataInput.readUTF());
					float pitch = Float.parseFloat(dataInput.readUTF());
					double height = Double.parseDouble(dataInput.readUTF());
					Player targetPlayer = Bukkit.getPlayer(playerUUID);
					if (targetPlayer == null || !targetPlayer.isOnline()) return;

					switch (responseNumber) {
						case 0 -> {
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("Bist du sicher, dass du folgenden Warp löschen willst?"));
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("Id: " + id));
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("Name: " + name));
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("Stadt: " + city));
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("Bundesland: " + state));
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("Koordinaten: " + latitude + ", " + longitude));
							TextComponent yes = new TextComponent("Bestätigen");
							yes.setColor(ChatColor.RED);
							yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Der Warp wird entgültig gelöscht").create()));
							yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nwarp delete " + id));
							targetPlayer.spigot().sendMessage(new TextComponent(TeleportationBukkit.getFormattedMessage("")), yes);
							registriesProvider.getPlayersEnteringDeleteWarpIdRegistry().unregister(targetPlayer);
						}
						case 1 -> {
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("Um den folgenden Warp zu ändern, klicke unter dem jeweiligen Wert."));
							TextComponent changeName = new TextComponent(TeleportationBukkit.getFormattedMessage("§r§cName §cändern"));
							changeName.setColor(ChatColor.RED);
							changeName.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nwarp change " + id + " name"));
							TextComponent changeCity = new TextComponent(TeleportationBukkit.getFormattedMessage("§r§cStadt §cändern"));
							changeCity.setColor(ChatColor.RED);
							changeCity.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nwarp change " + id + " city"));
							TextComponent changeState = new TextComponent(TeleportationBukkit.getFormattedMessage("§r§cBundesland §cändern"));
							changeState.setColor(ChatColor.RED);
							changeState.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nwarp change " + id + " state"));
							TextComponent changeCoords = new TextComponent(TeleportationBukkit.getFormattedMessage("§r§cKoordinaten §cändern"));
							changeCoords.setColor(ChatColor.RED);
							changeCoords.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nwarp change " + id + " coordinates"));
							TextComponent changeHeadId = new TextComponent(TeleportationBukkit.getFormattedMessage("§r§cHeadId §cändern"));
							changeHeadId.setColor(ChatColor.RED);
							changeHeadId.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nwarp change " + id + " head_id"));
							TextComponent changeYaw = new TextComponent(TeleportationBukkit.getFormattedMessage("§r§cYaw §cändern"));
							changeYaw.setColor(ChatColor.RED);
							changeYaw.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nwarp change " + id + " yaw"));
							TextComponent changePitch = new TextComponent(TeleportationBukkit.getFormattedMessage("§r§cPitch §cändern"));
							changePitch.setColor(ChatColor.RED);
							changePitch.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nwarp change " + id + " pitch"));
							TextComponent changeHeight = new TextComponent(TeleportationBukkit.getFormattedMessage("§r§cHöhe §cändern"));
							changeHeight.setColor(ChatColor.RED);
							changeHeight.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nwarp change " + id + " height"));
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("Id: " + id));
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("Name: " + name));
							targetPlayer.spigot().sendMessage(changeName);
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("Stadt: " + city));
							targetPlayer.spigot().sendMessage(changeCity);
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("Bundesland: " + state));
							targetPlayer.spigot().sendMessage(changeState);
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("Koordinaten: " + latitude + ", " + longitude));
							targetPlayer.spigot().sendMessage(changeCoords);
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("HeadId: " + headId));
							targetPlayer.spigot().sendMessage(changeHeadId);
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("Yaw: " + yaw));
							targetPlayer.spigot().sendMessage(changeYaw);
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("Pitch: " + pitch));
							targetPlayer.spigot().sendMessage(changePitch);
							targetPlayer.sendMessage(TeleportationBukkit.getFormattedMessage("Höhe: " + height));
							targetPlayer.spigot().sendMessage(changeHeight);
							registriesProvider.getPlayersEnteringChangeWarpIdRegistry().unregister(targetPlayer);
						}
					}
				}

				case "command_perform" -> {
					UUID playerUUID = UUID.fromString(dataInput.readUTF());
					String command = dataInput.readUTF();
					Player targetPlayer = Bukkit.getPlayer(playerUUID);
					if (targetPlayer == null || !targetPlayer.isOnline()) return;

					targetPlayer.performCommand(command);
				}

				case "list_cities" -> {
					CitiesRegistry citiesRegistry = this.registriesProvider.getCitiesRegistry();
					citiesRegistry.unregisterAll();
					while (true) {
						try {
							String city = dataInput.readUTF();
							citiesRegistry.register(city);
						} catch (EOFException e) {
							break;
						}
					}
				}

				case "list_tags" -> {
					WarpTagsRegistry warpTagsRegistry = this.registriesProvider.getWarpTagsRegistry();
					warpTagsRegistry.unregisterAll();
					while (true) {
						try {
							String warpTag = dataInput.readUTF();
							warpTagsRegistry.register(warpTag);
						} catch (EOFException e) {
							break;
						}
					}
				}

				case "last_location_request" -> {
					UUID playerUUID = UUID.fromString(dataInput.readUTF());
					this.pluginMessenger.send(new LastLocationResponseMessage(requestId, playerUUID));
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			if (player.getName().equals("JaskerX")) {
				player.sendMessage("EXCEPTION");
				if(e.getMessage() != null) {
					player.sendMessage(e.getMessage());
				}
				if(e.getMessage() != null) {
					player.sendMessage(e.getLocalizedMessage());
				}
				for(StackTraceElement s : e.getStackTrace()) {
					player.sendMessage(s.toString());
				}
			}
		}

	}

}
