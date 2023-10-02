package de.btegermany.teleportation.TeleportationBukkit.listener;

import de.btegermany.teleportation.TeleportationBukkit.gui.*;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.tp.PendingTpPlayer;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.tp.PendingTpLocation;
import de.btegermany.teleportation.TeleportationBukkit.tp.TeleportationHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.util.NumberConversions;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
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
	public void onPluginMessageReceived(String channel, @Nonnull Player player, @Nonnull byte[] message) {

		if(channel.equals(TeleportationBukkit.PLUGIN_CHANNEL)) {

			DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

			try {
				switch (in.readUTF()) {

					case "teleport_player" -> {
						UUID playerUUID = UUID.fromString(in.readUTF());
						UUID targetUUID = UUID.fromString(in.readUTF());

						teleportationHandler.handle(new PendingTpPlayer(playerUUID, targetUUID));
					}

					case "teleport_coords" -> {
						UUID playerUUID = UUID.fromString(in.readUTF());
						String[] coords = in.readUTF().split(",");
						double x = Double.parseDouble(coords[0]);
						double y = Double.parseDouble(coords[1]);
						double z = Double.parseDouble(coords[2]);
						float yaw = Float.parseFloat(in.readUTF());
						float pitch = Float.parseFloat(in.readUTF());
						World world = Bukkit.getWorld("world");
						if (world == null) {
							world = Bukkit.getWorlds().get(0);
						}
						if (Double.isNaN(y) || !NumberConversions.isFinite(y)) {
							y = world.getHighestBlockYAt((int) x, (int) z);
						}

						teleportationHandler.handle(new PendingTpLocation(playerUUID, world, x, y + 1, z, yaw, pitch));
					}

					case "gui_data" -> {
						JSONObject object = new JSONObject(in.readUTF());
						String metaTitle = object.getString("title");
						UUID playerUUID = UUID.fromString(object.getString("player_uuid"));
						JSONArray pagesData = object.getJSONArray("pagesData");

						String group = metaTitle.split("_").length > 0 ? metaTitle.split("_")[0] : metaTitle;
						String title = metaTitle.equals(group) ? group : metaTitle.substring(group.length() + 1);
						Player targetPlayer = Bukkit.getPlayer(playerUUID);
						if (targetPlayer == null || !targetPlayer.isOnline()) return;

						if (registriesProvider.getMultiplePagesGuisRegistry().isRegistered(targetPlayer)) {
							registriesProvider.getMultiplePagesGuisRegistry().getGui(targetPlayer).addPages(pagesData);
						} else {
							switch (group) {
								case "Alle" ->
										new AllGui(targetPlayer, pluginMessenger, pagesData, registriesProvider).open();
								case "Städte" ->
										new CitiesGui(targetPlayer, pluginMessenger, pagesData, registriesProvider).open();
								case "Events" ->
										new EventsGui(targetPlayer, pluginMessenger, pagesData, registriesProvider).open();
								case "Plotregionen" ->
										new PlotsGui(targetPlayer, pluginMessenger, pagesData, registriesProvider).open();
								case "Normen Hubs" ->
										new NormenHubsGui(targetPlayer, pluginMessenger, pagesData, registriesProvider).open();
								case "city" ->
										new CitiesDetailGui(targetPlayer, title, pluginMessenger, pagesData, registriesProvider).open();
								case "bl" ->
										new StatesDetailGui(targetPlayer, title, pluginMessenger, pagesData, registriesProvider).open();
								case "search" ->
										new SearchResultsGui(targetPlayer, title, pluginMessenger, pagesData, registriesProvider).open();
								case "server" ->
										new ServersDetailGui(targetPlayer, pluginMessenger, pagesData, title, registriesProvider).open();
								case "lobbywarp" ->
										new LobbyWarpGui(targetPlayer, title, pluginMessenger, pagesData, registriesProvider).open();
								case "lobbywarp-around" ->
										new LobbyWarpAroundGui(targetPlayer, title, pluginMessenger, pagesData, registriesProvider).open();
							}
						}
					}

					case "warp_info" -> {
						UUID playerUUID = UUID.fromString(in.readUTF());
						int responseNumber = Integer.parseInt(in.readUTF());
						int id = Integer.parseInt(in.readUTF());
						String name = in.readUTF();
						String city = in.readUTF();
						String state = in.readUTF();
						double latitude = Double.parseDouble(in.readUTF());
						double longitude = Double.parseDouble(in.readUTF());
						String headId = in.readUTF();
						if (headId.equals("null")) {
							headId = null;
						}
						float yaw = Float.parseFloat(in.readUTF());
						float pitch = Float.parseFloat(in.readUTF());
						double height = Double.parseDouble(in.readUTF());
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
						UUID playerUUID = UUID.fromString(in.readUTF());
						String command = in.readUTF();
						Player targetPlayer = Bukkit.getPlayer(playerUUID);
						if (targetPlayer == null || !targetPlayer.isOnline()) return;

						targetPlayer.performCommand(command);
					}
				}

			} catch (IOException ignore) {}

		}
	}

}
