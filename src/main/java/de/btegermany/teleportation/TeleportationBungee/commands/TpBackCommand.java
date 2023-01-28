package de.btegermany.teleportation.TeleportationBungee.commands;


import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.LastLocation;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TpBackCommand extends Command {

    public TpBackCommand() {
        super("TpBack");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer) {

            ProxiedPlayer p = (ProxiedPlayer) sender;
            if(!p.hasPermission("teleportation.tpback")) {
                p.sendMessage(new ComponentBuilder("§b§lBTEG §7» §cDu §cbist §cnicht §cberechtigt, §cdiesen §cCommand §causzuführen!").create());
                return;
            }

            teleport(p);
        }
    }

    private void teleport(ProxiedPlayer p) {
        if(TeleportationBungee.lastLocations.containsKey(p.getUniqueId())) {
            LastLocation lastLocation = TeleportationBungee.lastLocations.get(p.getUniqueId());
            TeleportationBungee.lastLocations.remove(p.getUniqueId());
            ServerInfo lastServer = ProxyServer.getInstance().getServerInfo(lastLocation.getServer());
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("teleportLocation");
            out.writeUTF(p.getUniqueId().toString());
            out.writeUTF(lastLocation.getWorld());
            out.writeUTF(lastLocation.getCoords());
            if(lastLocation.getServer().equals(p.getServer().getInfo().getName())) {
                out.writeUTF("true");
            } else {
                out.writeUTF("false");
                p.connect(lastServer);
            }
            lastServer.sendData(TeleportationBungee.PLUGIN_CHANNEL, out.toByteArray());
        } else {
            p.sendMessage(TeleportationBungee.getFormattedMessage("Wenn du dich zuvor teleportiert hast,  warte bitte einen Moment (einige Sekunden) und führe dann den Command erneut aus."));
        }
    }

}
