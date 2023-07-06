package de.btegermany.teleportation.TeleportationBungee.command;


import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
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

    public TeleportCommand(Utils utils) {
        super("Tp");
        this.utils = utils;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;
            if(!player.hasPermission("teleportation.tp")) {
                player.sendMessage(new ComponentBuilder("§b§lBTEG §7» §cDu §cbist §cnicht §cberechtigt, §cdiesen §cCommand §causzuführen!").create());
                return;
            }
            if(args.length != 1) {
                player.sendMessage(TeleportationBungee.getFormattedMessage("Bitte gib einen Spieler an!"));
                return;
            }
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);


            if(target != null) {

                utils.teleport(player, target);
            } else {
                player.sendMessage(TeleportationBungee.getFormattedMessage("Der Spieler wurde nicht gefunden!"));
            }
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
