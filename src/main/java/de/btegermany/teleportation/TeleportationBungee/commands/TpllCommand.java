package de.btegermany.teleportation.TeleportationBungee.commands;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoData;
import de.btegermany.teleportation.TeleportationBungee.util.PluginMessenger;
/*import net.buildtheearth.terraminusminus.dataset.IScalarDataset;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorPipelines;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.generator.GeneratorDatasets;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;*/
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.protocol.Location;

import java.util.concurrent.CompletableFuture;

public class TpllCommand extends Command implements TabExecutor {

    //private static final EarthGeneratorSettings bteGeneratorSettings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
    public TpllCommand() {
        super("tpll", null, "tpl");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 2) {
            if (sender.hasPermission("bteg.tpll")) {

                double[] coordinates = new double[2];
                coordinates[0] = Double.parseDouble(args[0].replace(",", ""));
                coordinates[1] = Double.parseDouble(args[1]);

/*                double[] mcCoordinates = new double[0];
                try {
                    mcCoordinates = bteGeneratorSettings.projection().fromGeo(coordinates[1], coordinates[0]);
                } catch (OutOfProjectionBoundsException e) {
                    e.printStackTrace();
                }

                double mcCoordinatesY = getHeight((int) mcCoordinates[0], (int) mcCoordinates[1]).join();*/

                //TODO: remove (only for testing)
                double[] mcCoordinates = new double[] {111.222, 33.44};
                double mcCoordinatesY = 666.999;

                String location = mcCoordinates[0] + " " + mcCoordinatesY + " " + mcCoordinates[1];
                ServerInfo targetServer = GeoData.getServerFromLocation(coordinates[0], coordinates[1]);
                if(targetServer == null) {
                    sender.sendMessage(TeleportationBungee.getFormattedMessage("Der Ort konnte nicht gefunden werden!"));
                    return;
                }

                PluginMessenger.teleportToCoords((ProxiedPlayer) sender, targetServer, mcCoordinates[0], mcCoordinatesY, mcCoordinates[1]);

                sender.sendMessage(new ComponentBuilder("§b§lBTEG §7» §7Teleporting to " + coordinates[1] + ", " + coordinates[0] + ".").create());
                return;
            } else {
                sender.sendMessage(new ComponentBuilder("§b§lBTEG §7» §7No permission for /tpll").create());
                return;
            }
        }else {
            sender.sendMessage(new ComponentBuilder("§b§lBTEG §7»  §7Usage: /tpll <longitudes> <latitudes>").create());
            return;
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

/*    public static CompletableFuture<Double> getHeight(double adjustedLon, double adjustedLat) {
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
    }*/

}
