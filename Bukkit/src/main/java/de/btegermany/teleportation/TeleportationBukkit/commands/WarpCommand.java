package de.btegermany.teleportation.TeleportationBukkit.commands;

import de.btegermany.teleportation.TeleportationBukkit.gui.CategoriesGui;
import de.btegermany.teleportation.TeleportationBukkit.util.WarpGettingChanged;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.message.ChangeWarpMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.DeleteWarpMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.message.WarpsSearchMessage;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit.getFormattedErrorMessage;

public class WarpCommand implements CommandExecutor {

    private final PluginMessenger pluginMessenger;
    private final RegistriesProvider registriesProvider;

    public WarpCommand(PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {
        this.pluginMessenger = pluginMessenger;
        this.registriesProvider = registriesProvider;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(getFormattedErrorMessage("Diesen Command können nur Spieler ausführen!"));
            return true;
        }

        Player player = (Player) sender;
        int id;

        if(args.length >= 1 && !args[0].equals("create") && !args[0].equals("delete") && !args[0].equals("change")) {
            findWarps(player, args);
            return true;
        }

        switch (args.length) {
            case 0:
                new CategoriesGui(player, pluginMessenger, registriesProvider).open();
                break;
            case 1:
                if(!player.hasPermission("bteg.warps.manage") || !args[0].equals("create")) {
                    return true;
                }
                registriesProvider.getWarpsInCreationRegistry().register(player);
                player.sendMessage(ChatColor.RED + "ACHTUNG! Zum aktuellen Zeitpunkt wird der erstellte Warp nur auf der temporären Proxy und somit NICHT über einen Neustart hinaus gespeichert!");
                player.sendMessage(TeleportationBukkit.getFormattedMessage("Nun kannst du Angaben zum Warp machen. Gib \"cancel\" ein, um den Vorgang abzubrechen."));
                registriesProvider.getWarpsInCreationRegistry().getWarpInCreation(player).sendCurrentQuestion();
                break;
            case 2:
                if(!player.hasPermission("bteg.warps.manage") || !args[0].equals("delete")) {
                    return true;
                }
                id = Integer.parseInt(args[1]);
                player.sendMessage(ChatColor.RED + "ACHTUNG! Zum aktuellen Zeitpunkt wird der gelöschte Warp nur auf der temporären Proxy und somit NICHT über einen Neustart hinaus gespeichert!");
                pluginMessenger.send(new DeleteWarpMessage(player, id));
                break;
            case 3:
                if(!player.hasPermission("bteg.warps.manage") || !args[0].equals("change")) {
                    return true;
                }
                id = Integer.parseInt(args[1]);
                String column = args[2];
                if(column.equals("yaw") || column.equals("pitch") || column.equals("height")) {
                    WarpGettingChanged warp = new WarpGettingChanged(id, column);
                    warp.setValue(column.equals("yaw") ? String.valueOf(player.getLocation().getYaw()) : (column.equals("pitch") ? String.valueOf(player.getLocation().getPitch()) : String.valueOf(player.getLocation().getY())));
                    pluginMessenger.send(new ChangeWarpMessage(player, warp));
                    return true;
                }
                registriesProvider.getWarpsGettingChangedRegistry().register(player, new WarpGettingChanged(id, column));
                player.sendMessage(ChatColor.RED + "ACHTUNG! Zum aktuellen Zeitpunkt wird der geänderte Warp nur auf der temporären Proxy und somit NICHT über einen Neustart hinaus gespeichert!");
                player.sendMessage(TeleportationBukkit.getFormattedMessage("Bitte gib nun den neuen Wert für \"" + column + "\" ein. Gib \"cancel\" ein, um den Vorgang abzubrechen."));
                break;
        }

        return true;
    }

    public void findWarps(Player player, String[] args) {
        StringBuilder searchBuilder = new StringBuilder();
        for(int i = 0; i < args.length; i++) {
            if(i >= 1) {
                searchBuilder.append(" ");
            }
            searchBuilder.append(args[i]);
        }
        String search = new String(searchBuilder);
        pluginMessenger.send(new WarpsSearchMessage(player, search));
    }

}
