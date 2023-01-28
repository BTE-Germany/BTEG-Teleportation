package de.btegermany.teleportation.TeleportationBungee;

public class LastLocation {

    String server;
    String world;
    String coords;

    public LastLocation(String server, String world, String coords) {
        this.server = server;
        this.world = world;
        this.coords = coords;
    }

    public String getServer() {
        return server;
    }
    public String getWorld() {
        return world;
    }
    public String getCoords() {
        return coords;
    }

}
