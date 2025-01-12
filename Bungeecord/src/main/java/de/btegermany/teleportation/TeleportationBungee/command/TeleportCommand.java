package de.btegermany.teleportation.TeleportationBungee.command;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBungee.message.withresponse.RequestLastLocationMessage;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.util.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;

public class TeleportCommand extends Command implements TabExecutor {

    private final Utils utils;
    private final RegistriesProvider registriesProvider;
    private final PluginMessenger pluginMessenger;

    public TeleportCommand(Utils utils, RegistriesProvider registriesProvider, PluginMessenger pluginMessenger) {
        super("Tp");
        this.utils = utils;
        this.registriesProvider = registriesProvider;
        this.pluginMessenger = pluginMessenger;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer player)) {
            return;
        }

        switch (args.length) {
            case 1 -> {
                // check permissions
                if(!player.hasPermission("teleportation.tp.player")) {
                    player.sendMessage(new ComponentBuilder("ᾠ §cDu §cbist §cnicht §cberechtigt, §cdiesen §cCommand §causzuführen!").create());
                    return;
                }
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);


                // will teleport player to target player if target player exists
                if(target != null) {
                    player.sendMessage(TeleportationBungee.getFormattedMessage("Du wirst teleportiert..."));
                    RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(player, this.registriesProvider, () -> {
                        this.utils.teleport(player, target);
                    });
                    this.pluginMessenger.sendMessageToServers(requestLastLocationMessage, player.getServer().getInfo());
                } else {
                    player.sendMessage(TeleportationBungee.getFormattedMessage("Der Spieler wurde nicht gefunden!"));
                }
            }
            case 3 -> {
                // check permissions
                if(!player.hasPermission("teleportation.tp.coords")) {
                    player.sendMessage(new ComponentBuilder("ᾠ §cDu §cbist §cnicht §cberechtigt, §cdiesen §cCommand §causzuführen!").create());
                    return;
                }

                // teleport to coordinates
                try {
                    double x = Double.parseDouble(args[0]);
                    double y = Double.parseDouble(args[1]);
                    double z = Double.parseDouble(args[2]);
                    x += x % 1 == 0 ? 0.5 : 0;
                    z += z % 1 == 0 ? 0.5 : 0;
                    float yaw = 12345;
                    float pitch = 12345;
                    player.sendMessage(new ComponentBuilder(String.format("ᾠ §6Du §6wirst §6zu §2%s §2%s §2%s §6teleportiert.", x, y, z)).create());
                    this.pluginMessenger.teleportToCoords(player, player.getServer().getInfo(), x, y, z, yaw, pitch);
                } catch (NumberFormatException e) {
                    player.sendMessage(new ComponentBuilder("ᾠ §cBitte §cüberprüfe §cdie §cKoordinaten!").create());
                }
            }
            default -> player.sendMessage(new ComponentBuilder("ᾠ §cBitte §cgib §ceinen §cSpieler §coder §cKoordinaten §can!").create());
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        HashSet<String> results = new HashSet<>();
        if(args.length == 1) {
            for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                if(p.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    results.add(p.getName());
            }
        }
        return results;
    }

}
