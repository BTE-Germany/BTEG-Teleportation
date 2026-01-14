package de.btegermany.teleportation.TeleportationVelocity.command;

import com.mojang.brigadier.Command;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import de.btegermany.teleportation.TeleportationVelocity.util.Utils;

public class TpaCancelCommand {

    public static BrigadierCommand createTpaCancelCommand(final Utils utils) {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("TpaCancel")
                .requires(source -> (source instanceof Player) && source.hasPermission("teleportation.tpa"))
                .executes(context -> {
                    utils.cancelTpa((Player) context.getSource());
                    return Command.SINGLE_SUCCESS;
                })
                .build());
    }

}