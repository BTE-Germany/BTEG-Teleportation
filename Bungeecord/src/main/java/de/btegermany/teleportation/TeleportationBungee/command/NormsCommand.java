package de.btegermany.teleportation.TeleportationBungee.command;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.data.ConfigReader;
import de.btegermany.teleportation.TeleportationBungee.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBungee.message.withresponse.RequestLastLocationMessage;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class NormsCommand extends Command {

    private final RegistriesProvider registriesProvider;
    private final PluginMessenger pluginMessenger;
    private final String normenServerName;
    private final String normenWorld;

    public NormsCommand(ConfigReader configReader, RegistriesProvider registriesProvider, PluginMessenger pluginMessenger) {
        super("norms", "bteg.norms", "normen", "norme");

        this.registriesProvider = registriesProvider;
        this.pluginMessenger = pluginMessenger;

        String[] normenServerAndWorld = configReader.readNormenServerAndWorld();
        this.normenServerName = normenServerAndWorld[0];
        this.normenWorld = normenServerAndWorld[1];
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(!(commandSender instanceof ProxiedPlayer player)) {
            commandSender.sendMessage(new TextComponent("Nur Spieler pls"));
            return;
        }

        if (this.normenServerName == null || this.normenServerName.isEmpty()) {
            player.sendMessage(TeleportationBungee.getFormattedMessage("§cAktuell §cist §ckein §cServer §cfür §cNormen §cdefiniert."));
            return;
        }

        if (this.normenWorld == null || this.normenWorld.isEmpty()) {
            player.sendMessage(TeleportationBungee.getFormattedMessage("§cAktuell §cist §ckeine §cWelt §cfür §cNormen §cdefiniert."));
            return;
        }

        player.sendMessage(TeleportationBungee.getFormattedMessage("Du wirst zu den Normen teleportiert."));
        ServerInfo normenServer = ProxyServer.getInstance().getServerInfo(this.normenServerName);

        RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(player, this.registriesProvider, () -> {
            this.pluginMessenger.teleportToNormen(player, normenServer, this.normenWorld);
        });
        this.pluginMessenger.sendMessageToServers(requestLastLocationMessage, player.getServer().getInfo());
    }

}
