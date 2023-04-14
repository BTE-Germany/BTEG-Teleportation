package de.btegermany.teleportation.TeleportationBungee.terramap;

import fr.thesmyler.bungee2forge.api.ForgePacket;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

public class RegisterForUpdatePacket implements ForgePacket {

    public boolean register;

    @Override
    public void encode(ByteBuf byteBuf) {
        byteBuf.writeBoolean(this.register);
    }

    @Override
    public void decode(ByteBuf byteBuf) {
        this.register = byteBuf.readBoolean();
    }

    @Override
    public boolean processFromServer(String channel, Server fromServer, ProxiedPlayer toPlayer) {
        return true;
    }

    @Override
    public boolean processFromClient(String channel, ProxiedPlayer fromPlayer, Server toServer) {
        if(this.register) {

        } else {

        }
        return true;
    }

}
