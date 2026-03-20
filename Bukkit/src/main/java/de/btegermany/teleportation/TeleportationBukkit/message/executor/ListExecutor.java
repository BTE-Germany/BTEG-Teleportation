package de.btegermany.teleportation.TeleportationBukkit.message.executor;

import com.google.common.io.ByteArrayDataInput;
import de.btegermany.teleportation.TeleportationAPI.message.executor.PluginMessageNormalExecutor;
import de.btegermany.teleportation.TeleportationBukkit.registry.CitiesRegistry;
import de.btegermany.teleportation.TeleportationBukkit.registry.WarpTagsRegistry;


public class ListExecutor {

    public static class CitiesExecutor implements PluginMessageNormalExecutor {

        private final CitiesRegistry registry;

        public CitiesExecutor(CitiesRegistry registry) {
            this.registry = registry;
        }

        @Override
        public void execute(ByteArrayDataInput dataInput) {
            this.registry.unregisterAll();
            while (true) {
                try {
                    String city = dataInput.readUTF();
                    this.registry.register(city);
                } catch (IllegalStateException e) {
                    break;
                }
            }
        }

    }

    public static class TagsExecutor implements PluginMessageNormalExecutor {

        private final WarpTagsRegistry registry;

        public TagsExecutor(WarpTagsRegistry registry) {
            this.registry = registry;
        }

        @Override
        public void execute(ByteArrayDataInput dataInput) {
            this.registry.unregisterAll();
            while (true) {
                try {
                    String warpTag = dataInput.readUTF();
                    this.registry.register(warpTag);
                } catch (IllegalStateException e) {
                    break;
                }
            }
        }

    }

}
