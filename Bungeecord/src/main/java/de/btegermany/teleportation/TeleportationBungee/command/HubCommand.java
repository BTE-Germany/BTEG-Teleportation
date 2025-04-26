package de.btegermany.teleportation.TeleportationBungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HubCommand extends Command {
    private static final String LOBBY_SERVER = "LOBBY";

    public HubCommand() {
        super("hub", null, "lobby", "l");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer player && !player.getServer().getInfo().getName().startsWith(LOBBY_SERVER)) {
            player.connect(ProxyServer.getInstance().getServerInfo(LOBBY_SERVER + "-1"));
        }
    }
}
