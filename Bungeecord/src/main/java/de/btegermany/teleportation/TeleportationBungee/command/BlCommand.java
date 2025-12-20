package de.btegermany.teleportation.TeleportationBungee.command;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

// copied from old plugin BTE-Worldchanger until we switch to Velocity
public class BlCommand extends Command implements TabExecutor {

    private static final String SERVER_1 = "Terra-1";
    private static final String SERVER_2 = "Terra-2";
    private static final String SERVER_3 = "Terra-3";

    private final HashMap<String, String> federalStates = new HashMap<>();

    public BlCommand() {
        super("bundesland", null, "bl");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer player)) {
            TextComponent message = new TextComponent("Kann nur von einem Spieler benutzt werden.");
            message.setColor(ChatColor.YELLOW);
            sender.sendMessage(TeleportationBungee.getFormattedMessage(message.toPlainText()));
            return;
        }

        if (args.length == 0) {
            TextComponent message = new TextComponent("Du musst ein Bundesland angeben!");
            message.setColor(ChatColor.YELLOW);
            player.sendMessage(TeleportationBungee.getFormattedMessage(message.toPlainText()));
            return;
        }

        String newState = args[0].toLowerCase();
        String newServer = getServerForState(newState);
        String currentServer = player.getServer().getInfo().getName();

        if (newServer == null) {
            TextComponent message = new TextComponent("Unbekanntes Bundesland!");
            message.setColor(ChatColor.RED);
            player.sendMessage(TeleportationBungee.getFormattedMessage(message.toPlainText()));
            return;
        }

        if (newServer.equalsIgnoreCase(currentServer)) {
            TextComponent message = new TextComponent("Du bist bereits auf dem richtigen Server.");
            player.sendMessage(TeleportationBungee.getFormattedMessage(message.toPlainText()));
            return;
        }

        //TextComponent message = new TextComponent("Verbinde nach " + newServer);
        //player.sendMessage(BTEWorldchanger.getPREFIX(), message);

        ServerInfo target = ProxyServer.getInstance().getServerInfo(newServer);
        player.connect(target);
    }

    private String getServerForState(String state) {
        if (federalStates.isEmpty()) {
            generateFederalStatesMap();
        }

        return federalStates.getOrDefault(state, null);
    }

    private void generateFederalStatesMap() {
        federalStates.put("bw", SERVER_3);
        federalStates.put("by", SERVER_3);
        federalStates.put("be", SERVER_1);
        federalStates.put("bb", SERVER_1);
        federalStates.put("hb", SERVER_2);
        federalStates.put("hh", SERVER_1);
        federalStates.put("he", SERVER_2);
        federalStates.put("mv", SERVER_1);
        federalStates.put("ni", SERVER_2);
        federalStates.put("nw", SERVER_2);
        federalStates.put("rp", SERVER_2);
        federalStates.put("sl", SERVER_2);
        federalStates.put("sn", SERVER_3);
        federalStates.put("st", SERVER_3);
        federalStates.put("sh", SERVER_1);
        federalStates.put("th", SERVER_2);

        federalStates.put("baden-württemberg", SERVER_3);
        federalStates.put("bayern", SERVER_3);
        federalStates.put("berlin", SERVER_1);
        federalStates.put("brandenburg", SERVER_1);
        federalStates.put("bremen", SERVER_2);
        federalStates.put("hamburg", SERVER_1);
        federalStates.put("hessen", SERVER_2);
        federalStates.put("mecklenburg-vorpommern", SERVER_1);
        federalStates.put("niedersachsen", SERVER_2);
        federalStates.put("nordrhein-westfalen", SERVER_2);
        federalStates.put("rheinland-pfalz", SERVER_2);
        federalStates.put("saarland", SERVER_2);
        federalStates.put("sachsen", SERVER_3);
        federalStates.put("sachsen-anhalt", SERVER_3);
        federalStates.put("schleswig-holstein", SERVER_1);
        federalStates.put("thüringen", SERVER_2);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (federalStates.isEmpty()) {
            generateFederalStatesMap();
        }

        Set<String> keys = federalStates.keySet();

        if (args.length > 0) {
            return keys.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }

        return keys;
    }

}
