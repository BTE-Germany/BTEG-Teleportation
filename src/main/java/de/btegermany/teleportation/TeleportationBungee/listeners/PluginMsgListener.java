package de.btegermany.teleportation.TeleportationBungee.listeners;

import java.util.UUID;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;


import de.btegermany.teleportation.TeleportationBungee.LastLocation;
import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PluginMsgListener implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {

        //TODO: remove???
        if(event.getTag().equals(TeleportationBungee.PLUGIN_CHANNEL)) {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            if(in.readUTF().equals("coords")) {
                UUID uuid = UUID.fromString(in.readUTF());
                String server = in.readUTF();
                String world = in.readUTF();
                String coords = in.readUTF();
                TeleportationBungee.lastLocations.put(uuid, new LastLocation(server, world, coords));
            }
        }
    }

}
