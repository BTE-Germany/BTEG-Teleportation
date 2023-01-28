package de.btegermany.teleportation.TeleportationBungee.commands;

import net.buildtheearth.terraminusminus.dataset.IScalarDataset;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorPipelines;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.generator.GeneratorDatasets;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.protocol.Location;

import java.util.concurrent.CompletableFuture;

public class TpllCommand extends Command implements TabExecutor {

    private static final EarthGeneratorSettings bteGeneratorSettings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
    public TpllCommand() {
        super("tpll", null, "tpl");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 2) {
            if (sender.hasPermission("bteg.tpll")) {

                double[] coordinates = new double[2];
                coordinates[1] = Double.parseDouble(args[0].replace(",", ""));
                coordinates[0] = Double.parseDouble(args[1]);

                double[] mcCoordinates = new double[0];
                try {
                    mcCoordinates = bteGeneratorSettings.projection().fromGeo(coordinates[0], coordinates[1]);
                } catch (OutOfProjectionBoundsException e) {
                    e.printStackTrace();
                }
                String location = mcCoordinates[0] + " " + getHeight((int) mcCoordinates[0], (int) mcCoordinates[1]).join() + " " + mcCoordinates[1];

//OSM fragen auf welchem server diese location liegt

                ///an bukkit plugin

                sender.sendMessage("§b§lBTEG §7» §7Teleporting to " + coordinates[1] + ", " + coordinates[0] + ".");
                return;
            } else {
                sender.sendMessage("§b§lBTEG §7» §7No permission for /tpll");
                return;
            }
        }else {
            sender.sendMessage("§b§lBTEG §7»  §7Usage: /tpll <longitudes> <latitudes>");
            return;
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
