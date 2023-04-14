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

import fr.thesmyler.bungee2forge.api.ForgePacket;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.UUID;

public class PluginHelloPacket implements ForgePacket {

    public String version = "";
    public PlayerSyncStatus syncPlayers = PlayerSyncStatus.DISABLED;
    public PlayerSyncStatus syncSpectators = PlayerSyncStatus.DISABLED;
    public boolean globalMap = true; // If true, Terramap allows users to open the map on non-terra worlds
    public boolean globalSettings = false; // Should settings and preferences be saved for the whole network (true) or per server (false)
    public boolean hasWarpSupport = false; // Do this server have warp support
    public UUID proxyUUID = new UUID(0, 0);

    public PluginHelloPacket(String version, PlayerSyncStatus syncPlayers, PlayerSyncStatus syncSpectators, boolean globalMap, boolean globalSettings, boolean hasWarpSupport, UUID proxyUUID) {
        this.version = version;
        this.syncPlayers = syncPlayers;
        this.syncSpectators = syncSpectators;
        this.globalMap = globalMap;
        this.globalSettings = globalSettings;
        this.hasWarpSupport = hasWarpSupport;
        this.proxyUUID = proxyUUID;
    }

    public PluginHelloPacket() {
        // Needed so this class can be instanced by the channel in case someone sends us such a packet
    }

    @Override
    public void encode(ByteBuf buf) {
        DefinedPacket.writeString(this.version, buf);
        buf.writeByte(this.syncPlayers.VALUE);
        buf.writeByte(this.syncSpectators.VALUE);
        buf.writeBoolean(this.globalMap);
        buf.writeBoolean(this.globalSettings);
        buf.writeBoolean(this.hasWarpSupport);
        buf.writeLong(this.proxyUUID.getLeastSignificantBits());
        buf.writeLong(this.proxyUUID.getMostSignificantBits());
    }

    @Override
    public void decode(ByteBuf buf) {
        this.version = DefinedPacket.readString(buf);
        this.syncPlayers = PlayerSyncStatus.getFromNetworkCode(buf.readByte());
        this.syncSpectators = PlayerSyncStatus.getFromNetworkCode(buf.readByte());
        this.globalMap = buf.readBoolean();
        this.globalSettings = buf.readBoolean();
        this.hasWarpSupport = buf.readBoolean();
        long leastUUID = buf.readLong();
        long mostUUID = buf.readLong();
        this.proxyUUID = new UUID(mostUUID, leastUUID);
    }

    @Override
    public boolean processFromServer(String channel, Server fromServer, ProxiedPlayer toPlayer) {
        return false; // We should never receive that, only send
    }

    @Override
    public boolean processFromClient(String channel, ProxiedPlayer fromPlayer, Server toServer) {
        return false; // We should never receive that, only send
    }

}
