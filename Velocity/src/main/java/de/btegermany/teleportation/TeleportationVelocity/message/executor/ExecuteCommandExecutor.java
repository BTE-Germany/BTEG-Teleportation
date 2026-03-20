package de.btegermany.teleportation.TeleportationVelocity.message.executor;

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.btegermany.teleportation.TeleportationVelocity.message.executor.base.PluginMessageNormalPlayerExecutor;
import org.slf4j.Logger;

import java.util.List;

public class ExecuteCommandExecutor extends PluginMessageNormalPlayerExecutor {

    private static final List<String> ALLOWED_COMMANDS_TO_EXECUTE = List.of("tpll", "tp");

    private final Logger logger;

    public ExecuteCommandExecutor(ProxyServer proxyServer, Logger logger) {
        super(proxyServer);
        this.logger = logger;
    }

    @Override
    public void execute(ByteArrayDataInput dataInput, Player player) {
        String command = dataInput.readUTF();

        int spaceIndex = !command.contains(" ") ? 0 : command.indexOf(" ");
        String baseCommand = command.substring(0, spaceIndex);
        if (!ALLOWED_COMMANDS_TO_EXECUTE.contains(baseCommand)) {
            this.logger.error("Not allowed to remotely execute command '{}'", baseCommand);
            return;
        }

        this.proxyServer.getCommandManager().executeAsync(player, command);
    }

}
