package de.btegermany.teleportation.TeleportationBukkit.util;

import org.bukkit.World;
import org.bukkit.block.Block;

public class LobbyCity {

    private final String city;
    private final double centerLat;
    private final double centerLon;
    private final int radius;
    private final Block block;

    private LobbyCity(String city, double centerLat, double centerLon, int radius, World world, int x, int y, int z) {
        this.city = city;
        this.centerLat = centerLat;
        this.centerLon = centerLon;
        this.radius = radius;
        this.block = world.getBlockAt(x, y, z);
    }

    public static class LobbyCityBuilder {

        private String city;
        private double centerLat;
        private double centerLon;
        private int radius;
        private World world;
        private int x;
        private int y;
        private int z;

        public LobbyCity build() {
            return new LobbyCity(city, centerLat, centerLon, radius, world, x, y, z);
        }

        public LobbyCityBuilder setCity(String city) {
            this.city = city;
            return this;
        }

        public LobbyCityBuilder setCenterLat(double centerLat) {
            this.centerLat = centerLat;
            return this;
        }

        public LobbyCityBuilder setCenterLon(double centerLon) {
            this.centerLon = centerLon;
            return this;
        }

        public LobbyCityBuilder setRadius(int radius) {
            this.radius = radius;
            return this;
        }

        public LobbyCityBuilder setWorld(World world) {
            this.world = world;
            return this;
        }

        public LobbyCityBuilder setX(int x) {
            this.x = x;
            return this;
        }

        public LobbyCityBuilder setY(int y) {
            this.y = y;
            return this;
        }

        public LobbyCityBuilder setZ(int z) {
            this.z = z;
            return this;
        }
    }

    public String getCity() {
        return city;
    }

    public double getCenterLat() {
        return centerLat;
    }

    public double getCenterLon() {
        return centerLon;
    }

    public int getRadius() {
        return radius;
    }

    public Block getBlock() {
        return block;
    }

}
