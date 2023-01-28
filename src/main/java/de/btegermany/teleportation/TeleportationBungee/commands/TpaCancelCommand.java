package de.btegermany.teleportation.TeleportationBungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TpaCancelCommand extends Command {

    public TpaCancelCommand() {
        super("TpaCancel");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if(!p.hasPermission("teleportation.tpa")) {
                p.sendMessage(new ComponentBuilder("§b§lBTEG §7» §cDu §cbist §cnicht §cberechtigt, §cdiesen §cCommand §causzuführen!").create());
                return;
            }
            TpaCommand.cancel(p);
        }
    }

}
