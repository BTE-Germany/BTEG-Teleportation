package de.btegermany.teleportation.TeleportationVelocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity;
import de.btegermany.teleportation.TeleportationVelocity.geo.CoordinateFormatConverter;
import de.btegermany.teleportation.TeleportationVelocity.geo.CoordinateFormats;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoData;
import de.btegermany.teleportation.TeleportationVelocity.geo.GeoServer;
import de.btegermany.teleportation.TeleportationVelocity.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationVelocity.message.withresponse.RequestLastLocationMessage;
import de.btegermany.teleportation.TeleportationVelocity.registry.RegistriesProvider;
import net.buildtheearth.terraminusminus.dataset.IScalarDataset;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorPipelines;
import net.buildtheearth.terraminusminus.generator.GeneratorDatasets;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class TpllCommand implements SimpleCommand {

    private final TeleportationVelocity plugin;
    private final ProxyServer proxyServer;
    private final GeoData geoData;
    private final PluginMessenger pluginMessenger;
    private final RegistriesProvider registriesProvider;

    public TpllCommand(final TeleportationVelocity plugin, final ProxyServer proxyServer, final GeoData geoData, final PluginMessenger pluginMessenger, final RegistriesProvider registriesProvider) {
        this.plugin = plugin;
        this.proxyServer = proxyServer;
        this.geoData = geoData;
        this.pluginMessenger = pluginMessenger;
        this.registriesProvider = registriesProvider;
    }

    @Override
    public void execute(Invocation invocation) {
        Player player = (Player) invocation.source();
        String[] args = Stream.of(invocation.arguments()).filter(arg -> !arg.isEmpty()).toArray(String[]::new);

        // will perform tpll/tpc directly on the server and return, if tpll pass through is  true
        Optional<GeoServer> optional = geoData.getGeoServers().stream().filter(geoServer -> player.getCurrentServer().isPresent() && player.getCurrentServer().get().getServer().equals(geoServer.getServer())).findFirst();
        if(optional.isPresent() && optional.get().isTpllPassthrough()) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(" ").append(arg);
            }
            if (player.getCurrentServer().get().getServer().getServerInfo().getName().equals("Vanilla-1")) {
                this.pluginMessenger.performCommand(player, "tpc" + builder);
                return;
            }
            this.pluginMessenger.performCommand(player, "tpll" + builder);
            return;
        }

        // check args length
        if (args.length < 2) {
            player.sendMessage(Component.text(this.plugin.getPrefix() + " Usage: /tpll <latitude> <longitude>", NamedTextColor.RED));
            return;
        }

        // if copied from Google Earth web
        args[0] = args[0].replace("\u2066", "").replace("\u2069", "");
        args[1] = args[1].replace("\u2066", "").replace("\u2069", "");

        // check format of coordinates because of inaccuracy
        if(CoordinateFormats.isDegreesMinutes(args[0] + " " + args[1]) || CoordinateFormats.isDegreesMinutesSeconds(args[0] + " " + args[1])) {
            TextComponent textComponent = Component.text(this.plugin.getPrefix() + " Achtung: ", NamedTextColor.RED)
                    .append(Component.text("Du verwendest ein ungenaues Koordinatenformat. ", NamedTextColor.GOLD))
                    .append(Component.text("Falls du gerade etwas baust, nutze bitte ausschließlich Dezimalkoordinaten ", NamedTextColor.RED))
                    .append(Component.text("(z.B. 12.3456(°)). Kopiere sie einfach über Rechtsklick auf Google Maps oder Google Earth (bei letzterem muss das Format unter 'Tools -> Einstellungen -> Formate und Einheiten' zu 'Dezimal' geändert werden).", NamedTextColor.GOLD));
            player.sendMessage(textComponent);
        }

        // convert input coordinates to degrees format
        double[] coordinates = CoordinateFormatConverter.toDegrees(args[0] + " " + args[1]);
        if(coordinates == null) {
            player.sendMessage(Component.text(this.plugin.getPrefix() + " Bitte überprüfe deine Koordinaten!", NamedTextColor.RED));
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
            player.sendMessage(Component.text(this.plugin.getPrefix() + " Error: OutOfProjectionBoundsException", NamedTextColor.RED));
            return;
        }
        final double[] mcCoordinatesFinal = mcCoordinates;

        // would set the height if the height was defined by any argument or otherwise set it to the default value
        double mcCoordinatesY = heightRaw != null ? Double.parseDouble(heightRaw) : Double.NaN; //this.getHeight((int) mcCoordinates[1], (int) mcCoordinates[0]).join();

        // get the server the location is on
        Optional<RegisteredServer> targetServerOptional = stayServer != null ? this.proxyServer.getServer(stayServer) : this.geoData.getServerFromLocation(coordinates[0], coordinates[1]);
        if (targetServerOptional.isEmpty()) {
            player.sendMessage(Component.text(this.plugin.getPrefix() + " Location could not be found!", NamedTextColor.RED));
            return;
        }
        RegisteredServer targetServer = targetServerOptional.get();

        targetServer.ping().orTimeout(1, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    player.sendMessage(Component.text(this.plugin.getPrefix() + " Server " + targetServer.getServerInfo().getName() + " is offline.", NamedTextColor.RED));
                    return null;
                })
                .thenAccept(pingResult -> {
                    if (pingResult == null) {
                        return;
                    }

                    player.sendMessage(Component.text(this.plugin.getPrefix() + " Teleporting to " + coordinates[0] + ", " + coordinates[1] + ".", NamedTextColor.GOLD));

                    // send teleport data and the player to the target server
                    RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(player, this.registriesProvider, () -> {
                        this.pluginMessenger.teleportToCoords(player, targetServer, mcCoordinatesFinal[0], mcCoordinatesY, mcCoordinatesFinal[1], yawFinal, pitchFinal);
                    });
                    if (player.getCurrentServer().isEmpty()) {
                        return;
                    }
                    this.pluginMessenger.sendMessageToServers(requestLastLocationMessage, player.getCurrentServer().get().getServer());
                });
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        CommandSource source = invocation.source();
        return (source instanceof Player) && source.hasPermission("bteg.tpll");
    }

    /*public BrigadierCommand createTpllCommand(final ProxyServer proxyServer, final GeoData geoData, final PluginMessenger pluginMessenger, final RegistriesProvider registriesProvider) {
        return new BrigadierCommand(BrigadierCommand.literalArgumentBuilder("tpll")
                .requires(source -> (source instanceof Player) && source.hasPermission("bteg.tpll"))
                .executes(context -> {
                    context.getSource().sendMessage(Component.text(this.plugin.getPrefix() + " Usage: /tpll <latitude> <longitude>", NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                })
                // issue: , at the end
                .then(BrigadierCommand.requiredArgumentBuilder("latitude", StringArgumentType.word())
                        .executes(context -> {
                            context.getSource().sendMessage(Component.text(this.plugin.getPrefix() + " Usage: /tpll <latitude> <longitude>", NamedTextColor.RED));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("longitude", StringArgumentType.word())
                                .executes(context -> {
                                    teleportPlayer(context, proxyServer, pluginMessenger, geoData, registriesProvider);
                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(BrigadierCommand.requiredArgumentBuilder("height", DoubleArgumentType.doubleArg())
                                        .executes(context -> {
                                            teleportPlayer(context, proxyServer, pluginMessenger, geoData, registriesProvider);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                        .then(BrigadierCommand.requiredArgumentBuilder("yaw", FloatArgumentType.floatArg())
                                                .executes(context -> {
                                                    teleportPlayer(context, proxyServer, pluginMessenger, geoData, registriesProvider);
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                                .then(BrigadierCommand.requiredArgumentBuilder("pitch", FloatArgumentType.floatArg())
                                                        .executes(context -> {
                                                            teleportPlayer(context, proxyServer, pluginMessenger, geoData, registriesProvider);
                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                        .then(BrigadierCommand.requiredArgumentBuilder("stayServer", StringArgumentType.word())
                                                                .executes(context -> {
                                                                    teleportPlayer(context, proxyServer, pluginMessenger, geoData, registriesProvider);
                                                                    return Command.SINGLE_SUCCESS;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .build());
    }

    private void teleportPlayer(CommandContext<CommandSource> context, ProxyServer proxyServer, PluginMessenger pluginMessenger, GeoData geoData, RegistriesProvider registriesProvider){
        Player player = (Player) context.getSource();
        String latitudeRaw = context.getArgument("latitude", String.class);
        String longitudeRaw = context.getArgument("longitude", String.class);

        // if copied from Google Earth web
        latitudeRaw = latitudeRaw.replace("\u2066", "").replace("\u2069", "");
        longitudeRaw = longitudeRaw.replace("\u2066", "").replace("\u2069", "");

        // will perform tpll/tpc directly on the server and return, if tpll pass through is  true
        Optional<GeoServer> optional = geoData.getGeoServers().stream().filter(geoServer -> player.getCurrentServer().isPresent() && player.getCurrentServer().get().getServer().equals(geoServer.getServer())).findFirst();
        if(optional.isPresent() && optional.get().isTpllPassthrough()) {
            StringBuilder builder = new StringBuilder();

            for(String arg : context.getArguments().values().stream().map(entry -> entry.getResult().toString()).toList()) {
                builder.append(" ").append(arg);
            }
            if(player.getCurrentServer().get().getServer().getServerInfo().getName().equals("Vanilla-1")) {
                pluginMessenger.performCommand(player, "tpc" + builder);
                return;
            }
            pluginMessenger.performCommand(player, "tpll" + builder);
            return;
        }

        // check format of coordinates because of inaccuracy
        if(CoordinateFormats.isDegreesMinutes(latitudeRaw + " " + longitudeRaw) || CoordinateFormats.isDegreesMinutesSeconds(latitudeRaw + " " + longitudeRaw)) {
            TextComponent textComponent = Component.text(this.plugin.getPrefix() + " Achtung: ", NamedTextColor.RED)
                    .append(Component.text("Du verwendest ein ungenaues Koordinatenformat. ", NamedTextColor.GOLD))
                    .append(Component.text("Falls du gerade etwas baust, nutze bitte ausschließlich Dezimalkoordinaten ", NamedTextColor.RED))
                    .append(Component.text("(z.B. 12.3456(°)). Kopiere sie einfach über Rechtsklick auf Google Maps oder Google Earth (bei letzterem muss das Format unter 'Tools -> Einstellungen -> Formate und Einheiten' zu 'Dezimal' geändert werden).", NamedTextColor.GOLD));
            player.sendMessage(textComponent);
        }

        // convert input coordinates to degrees format
        double[] coordinates = CoordinateFormatConverter.toDegrees(latitudeRaw + " " + longitudeRaw);
        if(coordinates == null) {
            player.sendMessage(Component.text(this.plugin.getPrefix() + " Bitte überprüfe deine Koordinaten!", NamedTextColor.RED));
            return;
        }
        // convert real life coordinates to in-game coordinates
        double[] mcCoordinates;
        try {
            mcCoordinates = GeoData.BTE_GENERATOR_SETTINGS.projection().fromGeo(coordinates[1], coordinates[0]);
        } catch (OutOfProjectionBoundsException e) {
            player.sendMessage(Component.text(this.plugin.getPrefix() + " Error: OutOfProjectionBoundsException", NamedTextColor.RED));
            return;
        }
        final double[] mcCoordinatesFinal = mcCoordinates;

        // get additional args
        double height = context.getArguments().containsKey("height") ? context.getArgument("height", Double.class) : getHeight((int) mcCoordinates[1], (int) mcCoordinates[0]).join();
        float yaw = context.getArguments().containsKey("yaw") ? Float.parseFloat(context.getArgument("yaw", String.class).replace("yaw=", "")) : 12345;
        float pitch = context.getArguments().containsKey("pitch") ? Float.parseFloat(context.getArgument("pitch", String.class).replace("pitch=", "")) : 12345;
        String stayServer = context.getArguments().containsKey("stayServer") ? context.getArgument("stayServer", String.class).replace("stay=", "") : null;

        // get the server the location is on
        Optional<RegisteredServer> targetServerOptional = stayServer != null ? proxyServer.getServer(stayServer) : geoData.getServerFromLocation(coordinates[0], coordinates[1]);
        if (targetServerOptional.isEmpty()) {
            player.sendMessage(Component.text(this.plugin.getPrefix() + " Location could not be found!", NamedTextColor.RED));
            return;
        }
        RegisteredServer targetServer = targetServerOptional.get();

        player.sendMessage(Component.text(this.plugin.getPrefix() + " Teleporting to " + coordinates[0] + ", " + coordinates[1] + ".", NamedTextColor.GOLD));

        // send teleport data and the player to the target server
        RequestLastLocationMessage requestLastLocationMessage = new RequestLastLocationMessage(player, registriesProvider, () -> {
            pluginMessenger.teleportToCoords(player, targetServer, mcCoordinatesFinal[0], height, mcCoordinatesFinal[1], yaw, pitch);
        });
        if (player.getCurrentServer().isEmpty()) {
            return;
        }
        pluginMessenger.sendMessageToServers(requestLastLocationMessage, player.getCurrentServer().get().getServer());
    }*/

    public CompletableFuture<Double> getHeight(double adjustedLon, double adjustedLat) {
        GeneratorDatasets datasets = new GeneratorDatasets(GeoData.BTE_GENERATOR_SETTINGS);
        CompletableFuture<Double> altFuture;
        try {
            altFuture = datasets.<IScalarDataset>getCustom(EarthGeneratorPipelines.KEY_DATASET_HEIGHTS)
                    .getAsync(adjustedLon, adjustedLat)
                    .thenApply(a -> a + 1.0d);
        } catch (OutOfProjectionBoundsException e) {
            altFuture = CompletableFuture.completedFuture(0.0);
        }
        return altFuture;
    }

}
