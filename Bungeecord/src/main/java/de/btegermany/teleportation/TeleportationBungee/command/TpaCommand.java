package de.btegermany.teleportation.TeleportationBungee.command;


import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBungee.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;

import static de.btegermany.teleportation.TeleportationBungee.TeleportationBungee.getFormattedMessage;

public class TpaCommand extends Command implements TabExecutor {

    private final Utils utils;
    private final RegistriesProvider registriesProvider;

    public TpaCommand(Utils utils, RegistriesProvider registriesProvider) {
        super("Tpa");
        this.utils = utils;
        this.registriesProvider = registriesProvider;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;
            if(!player.hasPermission("teleportation.tpa")) {
                player.sendMessage(new ComponentBuilder("§b§lBTEG §7> §cDu §cbist §cnicht §cberechtigt, §cdiesen §cCommand §causzuführen!").create());
                return;
            }

            if(args.length != 1) {
                player.sendMessage(getFormattedMessage("Bitte gib einen Spieler an!"));
                return;
            }
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

            if(target != null) {
                if(registriesProvider.getTpasRegistry().isRegistered(player)) {
                    utils.cancelTpa(player);
                }
                TextComponent compAccept = new TextComponent("/tpaccept " + player.getName());
                compAccept.setColor(ChatColor.GREEN);
                compAccept.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/tpaccept " + player.getName()));
                TextComponent compDeny = new TextComponent("/tpadeny " + player.getName());
                compDeny.setColor(ChatColor.RED);
                compDeny.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/tpadeny " + player.getName()));

                registriesProvider.getTpasRegistry().register(player, target);
                target.sendMessage(new ComponentBuilder("§b§lBTEG §7> §6Du §6hast §6eine §6Teleport-Anfrage §6von §6" + player.getDisplayName() + " §6erhalten. §6Nutze ").append(compAccept).append(" §6zum §6Akzeptieren §6und ").append(compDeny).append(" §6zum §6Ablehnen §6der §6Anfrage.").create());
                player.sendMessage(getFormattedMessage("Die Anfrage wurde gesendet! Um sie abzubrechen, nutze /tpacancel."));
            } else {
                player.sendMessage(getFormattedMessage("Der Spieler wurde nicht gefunden!"));
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
