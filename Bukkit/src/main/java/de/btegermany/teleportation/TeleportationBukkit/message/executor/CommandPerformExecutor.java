package de.btegermany.teleportation.TeleportationBukkit.message.executor;

import com.google.common.io.ByteArrayDataInput;
import de.btegermany.teleportation.TeleportationAPI.message.executor.PluginMessageNormalExecutor;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CommandPerformExecutor implements PluginMessageNormalExecutor {

    private static final List<String> ALLOWED_COMMANDS_TO_EXECUTE = List.of("tpll", "tpc");

    private final TeleportationBukkit plugin;

    public CommandPerformExecutor(TeleportationBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ByteArrayDataInput dataInput) {
        UUID playerUUID = UUID.fromString(dataInput.readUTF());
        String command = dataInput.readUTF();
        Player targetPlayer = Bukkit.getPlayer(playerUUID);
        if (targetPlayer == null || !targetPlayer.isOnline()) return;

        int spaceIndex = !command.contains(" ") ? 0 : command.indexOf(" ");
        String baseCommand = command.substring(0, spaceIndex);
        if (!ALLOWED_COMMANDS_TO_EXECUTE.contains(baseCommand)) {
            this.plugin.getLogger().severe("Not allowed to remotely perform command '%s'".formatted(baseCommand));
            return;
        }

        Bukkit.getScheduler().runTask(this.plugin, () -> targetPlayer.performCommand(command));
    }

}
