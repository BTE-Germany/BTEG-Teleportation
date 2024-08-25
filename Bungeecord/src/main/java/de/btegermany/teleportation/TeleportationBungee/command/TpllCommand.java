package de.btegermany.teleportation.TeleportationBungee.command;

import de.btegermany.teleportation.TeleportationBungee.geo.CoordinateFormatConverter;
import de.btegermany.teleportation.TeleportationBungee.geo.CoordinateFormats;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoData;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoServer;
import de.btegermany.teleportation.TeleportationBungee.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBungee.message.withresponse.RequestLastLocationMessage;
import de.btegermany.teleportation.TeleportationBungee.registry.RegistriesProvider;
import net.buildtheearth.terraminusminus.dataset.IScalarDataset;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorPipelines;
import net.buildtheearth.terraminusminus.generator.GeneratorDatasets;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
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
    private final RegistriesProvider registriesProvider;

    public TpllCommand(GeoData geoData, PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {
        super("tpll", null, "tpl");
        this.geoData = geoData;
        this.pluginMessenger = pluginMessenger;
        this.registriesProvider = registriesProvider;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!(sender instanceof ProxiedPlayer player)) {
            return;
        }

        // remove empty args
        args = Stream.of(args).filter(arg -> !arg.isEmpty()).toArray(String[]::new);

        // will perform tpll/tpc directly on the server and return, if tpll pass through is  true
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

        // check args length
        if (args.length < 2) {
            sender.sendMessage(getFormattedMessage("Usage: /tpll <latitude> <longitude>"));
            return;
        }

        // check permissions
        if (!sender.hasPermission("bteg.tpll")) {
            sender.sendMessage(getFormattedMessage("No permission for /tpll"));
            return;
        }

        // if copied from google earth web
        args[0] = args[0].replace("\u2066", "").replace("\u2069", "");
        args[1] = args[1].replace("\u2066", "").replace("\u2069", "");

        // check format of coordinates because of inaccuracy
        if(CoordinateFormats.isDegreesMinutes(args[0] + " " + args[1]) || CoordinateFormats.isDegreesMinutesSeconds(args[0] + " " + args[1])) {
            player.sendMessage(new TextComponent("ᾠ §cAchtung: §6Du verwendest ein ungenaues Koordinatenformat. §cFalls du gerade etwas baust, nutze bitte ausschließlich Dezimalkoordinaten §6(z.B. 12.3456(°)). Kopiere sie einfach über Rechtsklick auf Google Maps oder Google Earth (bei letzterem muss das Format unter 'Tools -> Einstellungen -> Formate und Einheiten' zu 'Dezimal' geändert werden)."));
        }

        // convert input coordinates to degrees format
        double[] coordinates = CoordinateFormatConverter.toDegrees(args[0] + " " + args[1]);
        if(coordinates == null) {
            sender.sendMessage(getFormattedMessage("Bitte überprüfe deine Koordinaten!"));
            return;
        }
        // get additional args
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

        // convert in-game coordinates to real life coordinates
        double[] mcCoordinates;
        try {
            mcCoordinates = GeoData.BTE_GENERATOR_SETTINGS.projection().fromGeo(coordinates[1], coordinates[0]);
        } catch (OutOfProjectionBoundsException e) {
            player.sendMessage(getFormattedMessage("§cError: §cOutOfProjectionBoundsException"));
            return;
        }
        final double[] mcCoordinatesFinal = mcCoordinates;

        // would set the height if the height was defined by any argument or otherwise set it to the default value
        double mcCoordinatesY = heightRaw != null ? Double.parseDouble(heightRaw) : this.getHeight((int) mcCoordinates[1], (int) mcCoordinates[0]).join();

        // get the server the location is on
        ServerInfo targetServer = stayServer != null ? ProxyServer.getInstance().getServerInfo(stayServer) : this.geoData.getServerFromLocation(coordinates[0], coordinates[1]);
        if (targetServer == null) {
            sender.sendMessage(getFormattedMessage("Location could not be found!"));
            return;
        }

        sender.sendMessage(getFormattedMessage("Teleporting to " + coordinates[0] + ", " + coordinates[1] + "."));

        // send teleport data and the player to the target server
        RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(player, this.registriesProvider, () -> {
            this.pluginMessenger.teleportToCoords(player, targetServer, mcCoordinatesFinal[0], mcCoordinatesY, mcCoordinatesFinal[1], yawFinal, pitchFinal);
        });
        this.pluginMessenger.sendMessageToServers(requestLastLocationMessage, player.getServer().getInfo());

    }

    public CompletableFuture<Double> getHeight(double adjustedLon, double adjustedLat) {
        CompletableFuture<Double> altFuture;
        try {
            GeneratorDatasets datasets = new GeneratorDatasets(GeoData.BTE_GENERATOR_SETTINGS);

            altFuture = datasets.<IScalarDataset>getCustom(EarthGeneratorPipelines.KEY_DATASET_HEIGHTS)
                    .getAsync(adjustedLon, adjustedLat)
                    .thenApply(a -> a + 1.0d);
        } catch (OutOfProjectionBoundsException e) {
            altFuture = CompletableFuture.completedFuture(0.0);
        }
        return altFuture;
    }

}
