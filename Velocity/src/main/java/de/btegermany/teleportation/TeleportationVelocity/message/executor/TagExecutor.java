package de.btegermany.teleportation.TeleportationVelocity.message.executor;

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.btegermany.teleportation.TeleportationVelocity.message.executor.base.PluginMessageNormalPlayerExecutor;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;

public class TagExecutor {

    public static class AddExecutor extends PluginMessageNormalPlayerExecutor {

        private final RegistriesProvider registriesProvider;

        public AddExecutor(ProxyServer proxyServer, RegistriesProvider registriesProvider) {
            super(proxyServer);
            this.registriesProvider = registriesProvider;
        }

        @Override
        public void execute(ByteArrayDataInput dataInput, Player player) {
            String tag = dataInput.readUTF();
            int warpId = Integer.parseInt(dataInput.readUTF());

            this.registriesProvider.getWarpsRegistry().addTagsToWarp(player, warpId, tag);
        }

    }

    public static class RemoveExecutor extends PluginMessageNormalPlayerExecutor {

        private final RegistriesProvider registriesProvider;

        public RemoveExecutor(ProxyServer proxyServer, RegistriesProvider registriesProvider) {
            super(proxyServer);
            this.registriesProvider = registriesProvider;
        }

        @Override
        public void execute(ByteArrayDataInput dataInput, Player player) {
            String tag = dataInput.readUTF();
            int warpId = Integer.parseInt(dataInput.readUTF());

            this.registriesProvider.getWarpsRegistry().removeTagsFromWarp(player, warpId, tag);
        }

    }

    public static class EditExecutor extends PluginMessageNormalPlayerExecutor {

        private final RegistriesProvider registriesProvider;

        public EditExecutor(ProxyServer proxyServer, RegistriesProvider registriesProvider) {
            super(proxyServer);
            this.registriesProvider = registriesProvider;
        }

        @Override
        public void execute(ByteArrayDataInput dataInput, Player player) {
            String tagOld = dataInput.readUTF();
            String tagNew = dataInput.readUTF();

            this.registriesProvider.getWarpTagsRegistry().editTag(player, tagOld, tagNew, this.registriesProvider.getWarpsRegistry());
        }

    }

}
