package de.btegermany.teleportation.TeleportationBukkit.tp;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

final class TerraplusminusRegionPreparer {

    private static final String PLUGIN_NAME = "Terraplusminus";
    private static final int TELEPORT_PREP_RADIUS_CHUNKS = 16;

    private TerraplusminusRegionPreparer() {
    }

    static CompletableFuture<Boolean> prepare(TeleportationBukkit owner, Player player, World world, double x, double z) {
        Plugin terraplusminus = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        if (terraplusminus == null || !terraplusminus.isEnabled()) {
            return CompletableFuture.completedFuture(true);
        }

        try {
            Method isTeleportRegionGenerated = terraplusminus.getClass().getMethod(
                    "isTeleportRegionGenerated",
                    World.class,
                    double.class,
                    double.class,
                    int.class
            );
            boolean alreadyGenerated = (boolean) isTeleportRegionGenerated.invoke(
                    terraplusminus,
                    world,
                    x,
                    z,
                    TELEPORT_PREP_RADIUS_CHUNKS
            );
            if (alreadyGenerated) {
                return CompletableFuture.completedFuture(true);
            }

            player.sendMessage(TeleportationBukkit.getFormattedMessage("Die Zielregion wird gerade generiert. Du wirst teleportiert, sobald sie bereit ist."));
            Method primeTeleportRegionCache = terraplusminus.getClass().getMethod(
                    "primeTeleportRegionCache",
                    World.class,
                    double.class,
                    double.class,
                    int.class
            );
            Object result = primeTeleportRegionCache.invoke(
                    terraplusminus,
                    world,
                    x,
                    z,
                    TELEPORT_PREP_RADIUS_CHUNKS
            );

            if (!(result instanceof CompletionStage<?> completionStage)) {
                owner.getLogger().warning("Terraplusminus primeTeleportRegionCache did not return a CompletionStage.");
                return CompletableFuture.completedFuture(false);
            }

            return completionStage
                    .thenApply(ignored -> true)
                    .exceptionally(ex -> {
                        owner.getLogger().warning("Terraplusminus failed to prepare the target teleport region: " + ex.getMessage());
                        Bukkit.getScheduler().runTask(owner, () ->
                                player.sendMessage(TeleportationBukkit.getFormattedErrorMessage("Die Zielregion konnte nicht generiert werden. Bitte versuche es spaeter erneut.")));
                        return false;
                    })
                    .toCompletableFuture();
        } catch (NoSuchMethodException e) {
            owner.getLogger().fine("Terraplusminus is installed without teleport region preparation API; continuing without cache priming.");
            return CompletableFuture.completedFuture(true);
        } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
            owner.getLogger().warning("Could not call Terraplusminus teleport region preparation API: " + e.getMessage());
            Bukkit.getScheduler().runTask(owner, () ->
                    player.sendMessage(TeleportationBukkit.getFormattedErrorMessage("Die Zielregion konnte nicht generiert werden. Bitte versuche es spaeter erneut.")));
            return CompletableFuture.completedFuture(false);
        }
    }
}
