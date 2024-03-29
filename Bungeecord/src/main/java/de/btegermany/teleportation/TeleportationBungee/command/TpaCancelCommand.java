package de.btegermany.teleportation.TeleportationBungee.command;

import de.btegermany.teleportation.TeleportationBungee.util.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TpaCancelCommand extends Command {

    private final Utils utils;

    public TpaCancelCommand(Utils utils) {
        super("TpaCancel");
        this.utils = utils;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!(sender instanceof ProxiedPlayer player)) {
            return;
        }

        // check permissions
        if(!player.hasPermission("teleportation.tpa")) {
            player.sendMessage(new ComponentBuilder("ᾠ §cDu §cbist §cnicht §cberechtigt, §cdiesen §cCommand §causzuführen!").create());
            return;
        }

        // cancel the tpa the player sent
        utils.cancelTpa(player);
    }

}
