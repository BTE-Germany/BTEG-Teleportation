package de.btegermany.teleportation.TeleportationBungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class WarpCommand extends Command implements TabExecutor {
    public WarpCommand() {
        super("warp","bteg.warp","nwarp");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
