package de.btegermany.teleportation.TeleportationBungee.command;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoData;
import de.btegermany.teleportation.TeleportationBungee.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBungee.message.withresponse.RequestLastLocationMessage;
import de.btegermany.teleportation.TeleportationBungee.message.withresponse.RequestPlayerWorldMessage;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.util.Utils;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.function.Consumer;

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

                this.teleportToCoordinates(player, args, 0);
            }
            case 4 -> {
                // check permissions
                if(!player.hasPermission("teleportation.tp.coords")) {
                    player.sendMessage(new ComponentBuilder("ᾠ §cDu §cbist §cnicht §cberechtigt, §cdiesen §cCommand §causzuführen!").create());
                    return;
                }

                // only allow players to tp themselves (for Journey Map)
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(args[0]);
                if (targetPlayer == null || !player.getName().equals(targetPlayer.getName())) {
                    player.sendMessage(new ComponentBuilder("ᾠ §cDu §ckannst §cnur §cdich §cselbst §czu §cKoordinaten §cteleportieren!").create());
                    return;
                }

                this.teleportToCoordinates(targetPlayer, args, 1);
            }
            default -> player.sendMessage(new ComponentBuilder("ᾠ §cBitte §cgib §ceinen §cSpieler §coder §cKoordinaten §can!").create());
        }
    }

    private void teleportToCoordinates(ProxiedPlayer player, String[] args, int argsOffset) {
        try {
            double x = Double.parseDouble(args[0 + argsOffset]);
            double y = Double.parseDouble(args[1 + argsOffset]);
            double z = Double.parseDouble(args[2 + argsOffset]);
            x += x % 1 == 0 ? 0.5 : 0;
            z += z % 1 == 0 ? 0.5 : 0;

            final double finalX = x;
            final double finalZ = z;
            Consumer<ServerInfo> teleport = serverInfo -> {
                player.sendMessage(new ComponentBuilder(String.format("ᾠ §6Du §6wirst §6zu §2%s §2%s §2%s §6teleportiert.", finalX, y, finalZ)).create());
                this.pluginMessenger.teleportToCoords(player, serverInfo, finalX, y, finalZ, null, null, null);
            };

            RequestPlayerWorldMessage requestPlayerWorldMessage = new RequestPlayerWorldMessage(player, world -> {
                if (player.getServer().getInfo().getName().equalsIgnoreCase("Lobby-1")) {
                    player.sendMessage(new ComponentBuilder("ᾠ §cDu §ckannst §cdich §cnicht §cinnerhalb §cder §cLobby §cteleportieren.").create());
                } else if (!world.equals(Utils.WORLD_TERRA)) {
                    teleport.accept(player.getServer().getInfo());
                } else {
                    try {
                        double[] geoCoordinates = GeoData.BTE_GENERATOR_SETTINGS.projection().toGeo(finalX, finalZ);

                        ServerInfo serverInfo = TeleportationBungee.getInstance().getGeoData().getServerFromLocation(geoCoordinates[1], geoCoordinates[0]);

                        if (serverInfo == null) {
                            player.sendMessage(new ComponentBuilder("ᾠ §cDer §cServer §cfür §cdieses §cBundesland §cist §cnicht §cerreichbar.").create());
                            return;
                        }

                        teleport.accept(serverInfo);
                    } catch (OutOfProjectionBoundsException e) {
                        player.sendMessage(new ComponentBuilder("ᾠ §cBitte §cüberprüfe §cdie §cKoordinaten!").create());
                    }
                }
            });
            this.pluginMessenger.sendMessageToServers(requestPlayerWorldMessage, player.getServer().getInfo());
        } catch (NumberFormatException e) {
            player.sendMessage(new ComponentBuilder("ᾠ §cBitte §cüberprüfe §cdie §cKoordinaten!").create());
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
