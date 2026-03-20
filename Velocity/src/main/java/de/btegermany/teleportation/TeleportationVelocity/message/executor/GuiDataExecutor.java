package de.btegermany.teleportation.TeleportationVelocity.message.executor;

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.btegermany.teleportation.TeleportationAPI.PagedGuiType;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.message.executor.base.PluginMessageWithResponsePlayerExecutor;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.util.GuiData;

public class GuiDataExecutor extends PluginMessageWithResponsePlayerExecutor {

    private final GuiData guiData;

    public GuiDataExecutor(RegistriesProvider registriesProvider, PluginMessenger pluginMessenger, ProxyServer proxyServer) {
        super(proxyServer);
        this.guiData = new GuiData(registriesProvider, pluginMessenger, proxyServer);
    }

    @Override
    public void execute(ByteArrayDataInput dataInput, int requestId, Player player) {
        PagedGuiType type = PagedGuiType.valueOf(dataInput.readUTF());
        String[] args = dataInput.readUTF().split(",");
        String[] pagesRaw = dataInput.readUTF().split(",");
        int[] pages = new int[pagesRaw.length];
        for (int i = 0; i < pagesRaw.length; i++) {
            pages[i] = Integer.parseInt(pagesRaw[i]);
        }

        this.guiData.send(type, player, requestId, pages, args);
    }

}
