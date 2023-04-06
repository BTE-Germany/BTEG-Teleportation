package de.btegermany.teleportation.TeleportationBungee.command;

import static de.btegermany.teleportation.TeleportationBungee.TeleportationBungee.getFormattedMessage;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoData;
import de.btegermany.teleportation.TeleportationBungee.util.PluginMessenger;
import net.buildtheearth.terraminusminus.dataset.IScalarDataset;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorPipelines;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.generator.GeneratorDatasets;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.concurrent.CompletableFuture;

public class TpllCommand extends Command implements TabExecutor {

    private static final EarthGeneratorSettings bteGeneratorSettings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
    private final GeoData geoData;
    private final PluginMessenger pluginMessenger;

    public TpllCommand(GeoData geoData, PluginMessenger pluginMessenger) {
        super("tpll", null, "tpl");
        this.geoData = geoData;
        this.pluginMessenger = pluginMessenger;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length >= 2) {

            if (sender.hasPermission("bteg.tpll")) {

                double[] coordinates = new double[2];
                coordinates[0] = Double.parseDouble(args[0].replace(",", ""));
                coordinates[1] = Double.parseDouble(args[1]);
                String heightRaw = null;
                if(args.length >= 3 && args[2].matches("\\d+(.\\d+)?")) {
                    heightRaw = args[2];
                }
                float yaw = 0;
                float pitch = 0;
                for(String arg : args) {
                    if(arg.startsWith("yaw=")) {
                        yaw = Float.parseFloat(arg.substring("yaw=".length()));
                    }
                    if(arg.startsWith("pitch=")) {
                        pitch = Float.parseFloat(arg.substring("pitch=".length()));
                    }
                }

                double[] mcCoordinates = new double[0];
                try {
                    mcCoordinates = bteGeneratorSettings.projection().fromGeo(coordinates[1], coordinates[0]);
                } catch (OutOfProjectionBoundsException e) {
                    e.printStackTrace();
                }

                double mcCoordinatesY = heightRaw != null ? Double.parseDouble(heightRaw) : getHeight((int) mcCoordinates[0], (int) mcCoordinates[1]).join();

                ServerInfo targetServer = geoData.getServerFromLocation(coordinates[0], coordinates[1]);
                if(targetServer == null) {
                    sender.sendMessage(getFormattedMessage("Location could not be found!"));
                    return;
                }

                pluginMessenger.teleportToCoords((ProxiedPlayer) sender, targetServer, mcCoordinates[0], mcCoordinatesY, mcCoordinates[1], yaw, pitch);

                sender.sendMessage(getFormattedMessage("Teleporting to " + coordinates[0] + ", " + coordinates[1] + "."));
            } else {
                sender.sendMessage(getFormattedMessage("No permission for /tpll"));
            }
        } else {
            sender.sendMessage(getFormattedMessage("Usage: /tpll <latitude> <longitude>"));
        }

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    public static CompletableFuture<Double> getHeight(double adjustedLon, double adjustedLat) {
        CompletableFuture<Double> altFuture;
        try {
            GeneratorDatasets datasets = new GeneratorDatasets(bteGeneratorSettings);


            altFuture = datasets.<IScalarDataset>getCustom(EarthGeneratorPipelines.KEY_DATASET_HEIGHTS)
                    .getAsync(adjustedLon, adjustedLat)
                    .thenApply(a -> a + 1.0d);
        } catch (OutOfProjectionBoundsException e) {
            altFuture = CompletableFuture.completedFuture(0.0);
        }
        return altFuture;
    }

}