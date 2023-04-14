package de.btegermany.teleportation.TeleportationBungee.command;

import de.btegermany.teleportation.TeleportationBungee.geo.GeoData;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoServer;
import de.btegermany.teleportation.TeleportationBungee.util.PluginMessenger;
import net.buildtheearth.terraminusminus.dataset.IScalarDataset;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorPipelines;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.generator.GeneratorDatasets;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static de.btegermany.teleportation.TeleportationBungee.TeleportationBungee.getFormattedMessage;

public class TpllCommand extends Command {

    private final EarthGeneratorSettings bteGeneratorSettings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
    private final GeoData geoData;
    private final PluginMessenger pluginMessenger;

    public TpllCommand(GeoData geoData, PluginMessenger pluginMessenger) {
        super("tpll", null, "tpl");
        this.geoData = geoData;
        this.pluginMessenger = pluginMessenger;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        Optional<GeoServer> optional = geoData.getGeoServers().stream().filter(geoServer -> geoServer.getServerInfo().equals(player.getServer().getInfo())).findFirst();
        if(optional.isPresent() && optional.get().isTpllPassthrough()) {
            StringBuilder builder = new StringBuilder();
            for(String arg : args) {
                builder.append(" ").append(arg);
            }
            player.chat("/tpll" + builder);
            return;
        }

        if (args.length >= 2) {

            if (sender.hasPermission("bteg.tpll")) {

                double[] coordinates = new double[2];
                coordinates[0] = Double.parseDouble(args[0].replace("°", "").replace(",", ""));
                coordinates[1] = Double.parseDouble(args[1].replace("°", ""));
                String heightRaw = null;
                if(args.length >= 3 && args[2].matches("\\d+(.\\d+)?")) {
                    heightRaw = args[2];
                }
                float yaw = 12345;
                float pitch = 12345;
                for(String arg : args) {
                    if(arg.startsWith("yaw=")) {
                        yaw = Float.parseFloat(arg.substring("yaw=".length()));
                    }
                    if(arg.startsWith("pitch=")) {
                        pitch = Float.parseFloat(arg.substring("pitch=".length()));
                    }
                }
                float yawFinal = yaw;
                float pitchFinal = pitch;

                double[] mcCoordinates = new double[0];
                try {
                    mcCoordinates = bteGeneratorSettings.projection().fromGeo(coordinates[1], coordinates[0]);
                } catch (OutOfProjectionBoundsException e) {
                    e.printStackTrace();
                }
                final double[] mcCoordinatesFinal = mcCoordinates;

                double mcCoordinatesY = heightRaw != null ? Double.parseDouble(heightRaw) : getHeight((int) mcCoordinates[0], (int) mcCoordinates[1]).join();

                geoData.getServerFromLocation(coordinates[0], coordinates[1]).thenAccept(targetServer -> {
                    if (targetServer == null) {
                        sender.sendMessage(getFormattedMessage("Location could not be found!"));
                        return;
                    }

                    pluginMessenger.teleportToCoords(player, targetServer, mcCoordinatesFinal[0], mcCoordinatesY, mcCoordinatesFinal[1], yawFinal, pitchFinal);

                    sender.sendMessage(getFormattedMessage("Teleporting to " + coordinates[0] + ", " + coordinates[1] + "."));
                });
            } else {
                sender.sendMessage(getFormattedMessage("No permission for /tpll"));
            }
        } else {
            sender.sendMessage(getFormattedMessage("Usage: /tpll <latitude> <longitude>"));
        }

    }

    public CompletableFuture<Double> getHeight(double adjustedLon, double adjustedLat) {
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
