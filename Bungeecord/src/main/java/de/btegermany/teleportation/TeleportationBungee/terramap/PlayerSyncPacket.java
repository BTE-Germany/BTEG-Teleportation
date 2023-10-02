/*
 * MIT License
 *
 * Copyright 2020-2022 noahhusby
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package de.btegermany.teleportation.TeleportationBungee.terramap;

import de.btegermany.teleportation.TeleportationBungee.util.BukkitPlayer;
import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.geo.GeoData;
import fr.thesmyler.bungee2forge.api.ForgePacket;
import io.netty.buffer.ByteBuf;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.DefinedPacket;

public class PlayerSyncPacket implements ForgePacket {

    private final EarthGeneratorSettings bteGeneratorSettings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
    private final BukkitPlayer[] players;
    private final GeoData geoData;

    public PlayerSyncPacket(BukkitPlayer[] players, GeoData geoData) {
        this.players = players;
        this.geoData = geoData;
    }

    public PlayerSyncPacket() {
        // Needed so this class can be instanced by the channel in case someone sends us such a packet
        this.players = new BukkitPlayer[0];
        this.geoData = new GeoData(TeleportationBungee.getInstance());
    }

    @Override
    public void encode(ByteBuf byteBuf) {
        byteBuf.writeInt(this.players.length);
        try {
            for (BukkitPlayer player : this.players) {
                if(geoData.getGeoServers().stream().anyMatch(geoServer -> player.getServerInfo().equals(geoServer.getServerInfo()) && !geoServer.shouldShowPlayersOnTerramap())) {
                    continue;
                }

                double playerX = player.getX();
                double playerZ = player.getZ();
                double[] coordinates = bteGeneratorSettings.projection().toGeo(playerX, playerZ);
                if(coordinates == null) {
                    continue;
                }

                // This only works because the BTE projection is conformal
                double[] northVec = bteGeneratorSettings.projection().vector(playerX, playerZ, 0.0001, 0);
                float north = (float) Math.toDegrees(Math.atan2(northVec[0], northVec[1]));
                float yaw = player.getYaw();
                float azimuth = north + yaw;
                if (azimuth < 0) {
                    azimuth += 360;
                }

                byteBuf.writeLong(player.getProxiedPlayer().getUniqueId().getLeastSignificantBits());
                byteBuf.writeLong(player.getProxiedPlayer().getUniqueId().getMostSignificantBits());
                String playerDisplayName = ComponentSerializer.toString(TextComponent.fromLegacyText(player.getProxiedPlayer().getDisplayName()));
                DefinedPacket.writeString(playerDisplayName, byteBuf);
                byteBuf.writeDouble(coordinates[0]);
                byteBuf.writeDouble(coordinates[1]);
                byteBuf.writeFloat(azimuth);
                DefinedPacket.writeString(player.getGameMode(), byteBuf);
            }

        } catch (OutOfProjectionBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(ByteBuf byteBuf) {}

    @Override
    public boolean processFromServer(String channel, Server fromServer, ProxiedPlayer toPlayer) {
        return true;
    }

    @Override
    public boolean processFromClient(String channel, ProxiedPlayer fromPlayer, Server toServer) {
        return true;
    }

}
