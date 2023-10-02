package de.btegermany.teleportation.TeleportationBungee.registry;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.data.Database;
import de.btegermany.teleportation.TeleportationBungee.util.Warp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class WarpsRegistry {

    private final Database database;
    private final TeleportationBungee plugin;
    private final Set<Warp> warps;

    public WarpsRegistry(Database database, TeleportationBungee plugin) {
        this.database = database;
        this.plugin = plugin;
        this.warps = new HashSet<>();
    }

    public void loadWarps() {
        this.warps.clear();
        try (PreparedStatement preparedStatement = database.getConnection().prepareStatement("SELECT id, name, city, state, latitude, longitude, head_id, yaw, pitch, height FROM warps ORDER BY name")) {
            ResultSet resultSet = this.database.executeQuerySync(preparedStatement);
            while (resultSet.next()) {
                Warp warp = new Warp(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("city"),
                        resultSet.getString("state"),
                        Double.parseDouble(resultSet.getString("latitude")),
                        Double.parseDouble(resultSet.getString("longitude")),
                        resultSet.getString("head_id"),
                        resultSet.getFloat("yaw"),
                        resultSet.getFloat("pitch"),
                        resultSet.getInt("height")
                );
                this.warps.add(warp);
            }
            this.plugin.getLogger().info("Loaded warps!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void register(Warp warp) {
        this.warps.add(warp);
    }

    public void unregister(Warp warp) {
        this.warps.remove(warp);
    }

    public void unregister(int id) {
        this.warps.removeIf(warp -> warp.getId() == id);
    }

    public boolean isRegistered(Warp warp) {
        return this.warps.contains(warp);
    }

    public boolean isRegistered(int id) {
        return this.warps.stream().anyMatch(warp -> warp.getId() == id);
    }

    public Warp getWarp(int id) {
        return this.warps.stream().filter(warp -> warp.getId() == id).findFirst().orElse(null);
    }

    public Set<Warp> getWarps() {
        return warps;
    }
}
