package de.btegermany.teleportation.TeleportationBungee.command;

import de.btegermany.teleportation.TeleportationBungee.geo.CoordinateFormatConverter;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoData;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoServer;
import de.btegermany.teleportation.TeleportationBungee.message.PluginMessenger;
import net.buildtheearth.terraminusminus.dataset.IScalarDataset;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorPipelines;
import net.buildtheearth.terraminusminus.generator.GeneratorDatasets;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static de.btegermany.teleportation.TeleportationBungee.TeleportationBungee.getFormattedMessage;

public class TpllCommand extends Command {

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

        args = Stream.of(args).filter(arg -> !arg.isEmpty()).toArray(String[]::new);

        ProxiedPlayer player = (ProxiedPlayer) sender;
        Optional<GeoServer> optional = geoData.getGeoServers().stream().filter(geoServer -> player.getServer().getInfo().equals(geoServer.getServerInfo())).findFirst();
        if(optional.isPresent() && optional.get().isTpllPassthrough()) {
            StringBuilder builder = new StringBuilder();
            for(String arg : args) {
                builder.append(" ").append(arg);
            }
            if(player.getServer().getInfo().getName().equals("Vanilla-1")) {
                this.pluginMessenger.performCommand(player, "tpc" + builder);
                return;
            }
            this.pluginMessenger.performCommand(player, "tpll" + builder);
            return;
        }

        if (args.length >= 2) {

            if (sender.hasPermission("bteg.tpll")) {

                double[] coordinates = CoordinateFormatConverter.toDegrees(args[0] + " " + args[1]);
                if(coordinates == null) {
                    sender.sendMessage(getFormattedMessage("Bitte überprüfe deine Koordinaten!"));
                    return;
                }
                String heightRaw = null;
                if(args.length >= 3 && args[2].matches("\\d+(.\\d+)?")) {
                    heightRaw = args[2];
                }
                float yaw = 12345;
                float pitch = 12345;
                String stayServer = null;
                for(String arg : args) {
                    if(arg.startsWith("yaw=")) {
                        yaw = Float.parseFloat(arg.substring("yaw=".length()));
                    }
                    if(arg.startsWith("pitch=")) {
                        pitch = Float.parseFloat(arg.substring("pitch=".length()));
                    }
                    if(arg.startsWith("stay=")) {
                        stayServer = arg.substring("stay=".length());
                    }
                }
                float yawFinal = yaw;
                float pitchFinal = pitch;

                double[] mcCoordinates;
                try {
                    mcCoordinates = GeoData.bteGeneratorSettings.projection().fromGeo(coordinates[1], coordinates[0]);
                } catch (OutOfProjectionBoundsException e) {
                    player.sendMessage(getFormattedMessage("§cError: §cOutOfProjectionBoundsException"));
                    return;
                }
                final double[] mcCoordinatesFinal = mcCoordinates;

                double mcCoordinatesY = heightRaw != null ? Double.parseDouble(heightRaw) : getHeight((int) mcCoordinates[1], (int) mcCoordinates[0]).join();

                ServerInfo targetServer = stayServer != null ? ProxyServer.getInstance().getServerInfo(stayServer) : geoData.getServerFromLocation(coordinates[0], coordinates[1]);
                if (targetServer == null) {
                    sender.sendMessage(getFormattedMessage("Location could not be found!"));
                    return;
                }

                pluginMessenger.teleportToCoords(player, targetServer, mcCoordinatesFinal[0], mcCoordinatesY, mcCoordinatesFinal[1], yawFinal, pitchFinal);

                sender.sendMessage(getFormattedMessage("Teleporting to " + coordinates[0] + ", " + coordinates[1] + "."));
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
            GeneratorDatasets datasets = new GeneratorDatasets(GeoData.bteGeneratorSettings);


            altFuture = datasets.<IScalarDataset>getCustom(EarthGeneratorPipelines.KEY_DATASET_HEIGHTS)
                    .getAsync(adjustedLon, adjustedLat)
                    .thenApply(a -> a + 1.0d);
        } catch (OutOfProjectionBoundsException e) {
            altFuture = CompletableFuture.completedFuture(0.0);
        }
        return altFuture;
    }

}
