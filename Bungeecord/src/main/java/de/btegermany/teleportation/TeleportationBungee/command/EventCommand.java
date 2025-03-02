package de.btegermany.teleportation.TeleportationBungee.command;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBungee.message.withresponse.RequestLastLocationMessage;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.util.Warp;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;

public class EventCommand extends Command implements TabExecutor {

    private final RegistriesProvider registriesProvider;
    private final PluginMessenger pluginMessenger;

    public EventCommand(RegistriesProvider registriesProvider, PluginMessenger pluginMessenger) {
        super("event");
        this.registriesProvider = registriesProvider;
        this.pluginMessenger = pluginMessenger;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer player)) {
            sender.sendMessage(TeleportationBungee.getFormattedMessage("Dieser Command kann nur von Spielern ausgeführt werden."));
            return;
        }

        switch (args.length) {
            case 0 -> {
                Warp warp = TeleportationBungee.getInstance().getEventWarp();
                if (warp == null) {
                    player.sendMessage(TeleportationBungee.getFormattedMessage("Gerade findet kein Event statt."));
                    return;
                }

                RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(player, this.registriesProvider, () -> {
                    TeleportationBungee.getInstance().getProxy().getPluginManager().dispatchCommand(player, warp.getTpllCommand());
                });
                this.pluginMessenger.sendMessageToServers(requestLastLocationMessage, player.getServer().getInfo());
            }

            case 1 -> {
                if (!player.hasPermission("bteg.warps.manage")) {
                    player.sendMessage(TeleportationBungee.getFormattedMessage("Du hast keine Berechtigung dazu."));
                    return;
                }

                if (!args[0].equalsIgnoreCase("cancel")) {
                    player.sendMessage(TeleportationBungee.getFormattedMessage("Usage: /event, /event cancel or /event set [id]"));
                    return;
                }

                TeleportationBungee.getInstance().setEventWarp(null);
                player.sendMessage(TeleportationBungee.getFormattedMessage("Das Event ist nicht mehr aktiv."));
            }

            case 2 -> {
                if (!player.hasPermission("bteg.warps.manage")) {
                    player.sendMessage(TeleportationBungee.getFormattedMessage("Du hast keine Berechtigung dazu."));
                    return;
                }

                if (!args[0].equalsIgnoreCase("set")) {
                    player.sendMessage(TeleportationBungee.getFormattedMessage("Usage: /event, /event cancel or /event set [id]"));
                    return;
                }

                try {
                    int id = Integer.parseInt(args[1]);
                    Warp warp = this.registriesProvider.getWarpsRegistry().getWarp(id);
                    if (warp == null) {
                        player.sendMessage(TeleportationBungee.getFormattedMessage("Es wurde kein Warp mit dieser Id gefunden."));
                        return;
                    }
                    TeleportationBungee.getInstance().setEventWarp(warp);
                    player.sendMessage(TeleportationBungee.getFormattedMessage("Der Event Warp wurde geändert."));
                } catch (NumberFormatException e) {
                    player.sendMessage(TeleportationBungee.getFormattedMessage("Bitte gib eine gültige Id an."));
                }
            }

            default -> player.sendMessage(TeleportationBungee.getFormattedMessage("Usage: /event, /event cancel or /event set [id]"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("bteg.warps.manage")) {
            return null;
        }

        HashSet<String> results = new HashSet<>();
        if (args.length == 1) {
            if ("cancel".startsWith(args[0].toLowerCase())) {
                results.add("cancel");
            }
            if ("set".startsWith(args[0].toLowerCase())) {
                results.add("set");
            }
        }
        return results;
    }

}
