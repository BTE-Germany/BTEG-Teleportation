package de.btegermany.teleportation.TeleportationBukkit.commands;

import de.btegermany.teleportation.TeleportationBukkit.message.ExecuteCommandMessage;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

// necessary for default JourneyMap tp command (because it doesn't work with BungeeCord commands)
public class TpCommand implements CommandExecutor {

    private final PluginMessenger pluginMessenger;

    public TpCommand(PluginMessenger pluginMessenger) {
        this.pluginMessenger = pluginMessenger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String name, @NotNull String @NotNull [] args) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }

        String argsJoined = String.join(" ", args);

        this.pluginMessenger.send(new ExecuteCommandMessage(player.getUniqueId().toString(), "/tp %s".formatted(argsJoined)));

        return true;
    }

}
