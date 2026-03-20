package de.btegermany.teleportation.TeleportationVelocity.message.executor;

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.proxy.ProxyServer;
import de.btegermany.teleportation.TeleportationAPI.message.executor.PluginMessageNormalExecutor;
import de.btegermany.teleportation.TeleportationVelocity.registry.WarpsRegistry;
import de.btegermany.teleportation.TeleportationVelocity.util.Warp;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

public class TpRandomWarpExecutor implements PluginMessageNormalExecutor {

    private final ProxyServer proxyServer;
    private final WarpsRegistry registry;

    public TpRandomWarpExecutor(ProxyServer proxyServer, WarpsRegistry registry) {
        this.proxyServer = proxyServer;
        this.registry = registry;
    }

    @Override
    public void execute(ByteArrayDataInput dataInput) {
        UUID playerUUID = UUID.fromString(dataInput.readUTF());

        this.proxyServer.getPlayer(playerUUID).ifPresent(player -> {
            Warp warp = this.getRandomWarp();

            this.proxyServer.getCommandManager().executeAsync(player, warp.getTpllCommand());
            TextComponent textComponent = Component.text("Dies ist ", NamedTextColor.GOLD)
                    .append(Component.text(warp.getName(), NamedTextColor.GREEN))
                    .append(Component.text(" in ", NamedTextColor.GOLD))
                    .append(Component.text(warp.getCity() + ", " + warp.getState(), NamedTextColor.GREEN))
                    .append(Component.text(".", NamedTextColor.GOLD));
            TextComponent button = Component.text("Klicke hier, um dich zum nächsten Warp zu teleportieren.", NamedTextColor.BLUE)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/nwarp random"));

            sendMessage(player, textComponent);
            sendMessage(player, button);
        });
    }

    private Warp getRandomWarp() {
        Set<Warp> warps = this.registry.getWarps();
        Warp warp = null;

        do {
            int i = 0;
            int searchedIndex = new Random().nextInt(warps.size());

            for (Warp randomWarp : warps) {
                if (i < searchedIndex) {
                    i++;
                    continue;
                }
                if (randomWarp.getCity().isEmpty()) {
                    break;
                }
                warp = randomWarp;
                break;
            }
        } while (warp == null);

        return warp;
    }

}
