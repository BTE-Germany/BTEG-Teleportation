package de.btegermany.teleportation.TeleportationBungee.registry;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpasRegistry implements PlayerRegistry {

    Map<UUID, UUID> tpas;

    public TpasRegistry() {
        this.tpas = new HashMap<>();
    }

    public void register(ProxiedPlayer player, ProxiedPlayer target) {
        this.register(player.getUniqueId(), target.getUniqueId());
    }

    public void register(UUID playerUUID, UUID targetUUID) {
        tpas.put(playerUUID, targetUUID);
    }

    @Override
    public boolean isRegistered(UUID playerUUID) {
        return tpas.containsKey(playerUUID);
    }

    @Override
    public void unregister(UUID playerUUID) {
        tpas.remove(playerUUID);
    }

    public UUID getTpa(ProxiedPlayer player) {
        return this.getTpa(player.getUniqueId());
    }

    public UUID getTpa(UUID playerUUID) {
        return tpas.get(playerUUID);
    }

    public Map<UUID, UUID> getTpas() {
        return tpas;
    }

}
