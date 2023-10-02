package de.btegermany.teleportation.TeleportationBukkit.listener;

import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.util.LobbyCity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PlayerInteractListener implements Listener {

    private final RegistriesProvider registriesProvider;
    private final Map<Player, PressurePlateActivation> lastActivations;

    public PlayerInteractListener(RegistriesProvider registriesProvider) {
        this.registriesProvider = registriesProvider;
        this.lastActivations = new HashMap<>();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if(!event.getAction().equals(Action.PHYSICAL) || block == null|| block.getType() != Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            return;
        }

        event.setCancelled(true);
        for(LobbyCity lobbyCity : this.registriesProvider.getLobbyCitiesRegistry().getLobbyCities()) {
            if(!block.getLocation().equals(lobbyCity.getBlock().getLocation())) {
                continue;
            }
            boolean isSpam = this.lastActivations.containsKey(player) && this.lastActivations.get(player).isSpam();
            this.lastActivations.put(player, new PressurePlateActivation(lobbyCity.getBlock()));
            if(isSpam) {
                return;
            }
            player.performCommand(String.format("lobbywarp %s", lobbyCity.getCity()));
        }
    }

    private static class PressurePlateActivation {

        private final Block block;
        private final LocalDateTime localDateTimeCreated;

        public PressurePlateActivation(Block block) {
            this.block = block;
            this.localDateTimeCreated = LocalDateTime.now();
        }

        public boolean isSpam() {
            return LocalDateTime.now().isBefore(this.localDateTimeCreated.plusSeconds(1));
        }

        public Block getBlock() {
            return block;
        }
    }

}
