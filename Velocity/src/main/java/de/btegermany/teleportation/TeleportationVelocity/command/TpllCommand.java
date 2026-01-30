package de.btegermany.teleportation.TeleportationVelocity.command;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationVelocity.geo.CoordinateFormatConverter;
import de.btegermany.teleportation.TeleportationVelocity.geo.CoordinateFormats;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoData;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoServer;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.message.withresponse.RequestLastLocationMessage;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationVelocity.util.Utils;
import net.buildtheearth.terraminusminus.dataset.IScalarDataset;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorPipelines;
import net.buildtheearth.terraminusminus.generator.GeneratorDatasets;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class TpllCommand implements SimpleCommand {

    private final GeoData geoData;
    private final PluginMessenger pluginMessenger;
    private final RegistriesProvider registriesProvider;

    public TpllCommand(final GeoData geoData, final PluginMessenger pluginMessenger, final RegistriesProvider registriesProvider) {
        this.geoData = geoData;
        this.pluginMessenger = pluginMessenger;
        this.registriesProvider = registriesProvider;
    }

    @Override
    public void execute(Invocation invocation) {
        Player player = (Player) invocation.source();
        String[] args = Stream.of(invocation.arguments()).filter(arg -> !arg.isEmpty()).toArray(String[]::new);

        // will perform tpll/tpc directly on the server and return, if tpll pass through is  true
        Optional<GeoServer> optional = geoData.getGeoServers().stream().filter(geoServer -> player.getCurrentServer().isPresent() && player.getCurrentServer().get().getServer().equals(geoServer.server())).findFirst();
        if (optional.isPresent() && optional.get().tpllPassthrough()) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(" ").append(arg);
            }
            if (player.getCurrentServer().get().getServer().getServerInfo().getName().equalsIgnoreCase("Vanilla-1")) {
                this.pluginMessenger.performCommand(player, "tpc" + builder);
                return;
            }
            this.pluginMessenger.performCommand(player, "tpll" + builder);
            return;
        }

        String latArg;
        String lonArg;

        // check args length (only one required when lat is directly followed by comma and lon)
        if (args.length < 2) {
            if (!(args[0].contains(",") && !args[0].substring(args[0].indexOf(",") + 1).isEmpty())) {
                sendMessage(player, Component.text("Usage: /tpll <latitude> <longitude>", NamedTextColor.RED));
                return;
            }
            latArg = args[0].substring(0, args[0].indexOf(","));
            lonArg = args[0].substring(args[0].indexOf(",") + 1);
        } else {
            latArg = args[0];
            lonArg = args[1];
        }

        // if copied from Google Earth web
        latArg = latArg.replace("\u2066", "").replace("\u2069", "");
        lonArg = lonArg.replace("\u2066", "").replace("\u2069", "");

        // check format of coordinates because of inaccuracy
        if (CoordinateFormats.isDegreesMinutes(latArg + " " + lonArg) || CoordinateFormats.isDegreesMinutesSeconds(latArg + " " + lonArg)) {
            sendMessage(player, Component.text("Achtung: ", NamedTextColor.RED),
                                Component.text("Du verwendest ein ungenaues Koordinatenformat. ", NamedTextColor.GOLD),
                                Component.text("Falls du gerade etwas baust, nutze bitte ausschließlich Dezimalkoordinaten ", NamedTextColor.RED),
                                Component.text("(z.B. 12.3456(°)). Kopiere sie einfach per Rechtsklick auf Google Maps oder Google Earth (bei letzterem muss das Format unter 'Tools -> Einstellungen -> Formate und Einheiten' zu 'Dezimal' geändert werden).", NamedTextColor.GOLD));
        }

        // convert input coordinates to degrees format
        double[] coordinates = CoordinateFormatConverter.toDegrees(latArg + " " + lonArg);
        if (coordinates == null) {
            sendMessage(player, Component.text("Bitte überprüfe deine Koordinaten.", NamedTextColor.RED));
            return;
        }

        // get additional args
        String heightRaw = null;
        if (args.length >= 3 && args[2].matches("\\d+(.\\d+)?")) {
            heightRaw = args[2];
        }
        Float yaw = null;
        Float pitch = null;
        String world = Utils.WORLD_TERRA;
        for (String arg : args) {
            if (arg.startsWith("yaw=")) {
                yaw = Float.parseFloat(arg.substring("yaw=".length()));
            }
            if (arg.startsWith("pitch=")) {
                pitch = Float.parseFloat(arg.substring("pitch=".length()));
            }
            if (arg.startsWith("world=")) {
                world = arg.substring("world=".length());
            }
        }
        final Float yawFinal = yaw;
        final Float pitchFinal = pitch;
        final String worldFinal = world;

        // convert in-game coordinates to real life coordinates
        double[] mcCoordinates;
        try {
            mcCoordinates = GeoData.BTE_GENERATOR_SETTINGS.projection().fromGeo(coordinates[1], coordinates[0]);
        } catch (OutOfProjectionBoundsException e) {
            sendMessage(player, Component.text("Error: OutOfProjectionBoundsException", NamedTextColor.RED));
            return;
        }
        final double[] mcCoordinatesFinal = mcCoordinates;

        // would set the height if the height was defined by any argument or otherwise set it to the default value
        double mcCoordinatesY = heightRaw != null ? Double.parseDouble(heightRaw) : Double.NaN; //this.getHeight((int) mcCoordinates[1], (int) mcCoordinates[0]).join();

        // get the server the location is on
        Optional<RegisteredServer> targetServerOptional = this.geoData.getServerFromLocation(coordinates[0], coordinates[1]);
        if (targetServerOptional.isEmpty()) {
            sendMessage(player, Component.text("Location could not be found!", NamedTextColor.RED));
            return;
        }
        RegisteredServer targetServer = targetServerOptional.get();

        // send teleport data and the player to the target server
        RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(player, this.registriesProvider, () -> {
            sendMessage(player, Component.text("Teleporting to " + coordinates[0] + ", " + coordinates[1] + ".", NamedTextColor.GOLD));
            this.pluginMessenger.teleportToCoords(player, targetServer, mcCoordinatesFinal[0], mcCoordinatesY, mcCoordinatesFinal[1], yawFinal, pitchFinal, worldFinal);
        });
        if (player.getCurrentServer().isEmpty()) {
            return;
        }
        this.pluginMessenger.sendMessageToServers(requestLastLocationMessage, player.getCurrentServer().get().getServer());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        CommandSource source = invocation.source();
        return (source instanceof Player) && source.hasPermission("bteg.tpll");
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
