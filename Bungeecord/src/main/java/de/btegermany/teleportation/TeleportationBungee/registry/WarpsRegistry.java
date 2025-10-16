package de.btegermany.teleportation.TeleportationBungee.registry;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.data.Database;
import de.btegermany.teleportation.TeleportationBungee.util.Warp;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jspecify.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WarpsRegistry {

    private final Database database;
    private final TeleportationBungee plugin;
    private final Set<Warp> warps;
    private final WarpTagsRegistry warpTagsRegistry;

    public WarpsRegistry(Database database, TeleportationBungee plugin, WarpTagsRegistry warpTagsRegistry) {
        this.database = database;
        this.plugin = plugin;
        this.warpTagsRegistry = warpTagsRegistry;
        this.warps = new TreeSet<>();
    }

    public synchronized void loadWarps() {
        this.warps.clear();
        try (PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("SELECT id, name, city, state, latitude, longitude, head_id, yaw, pitch, height FROM warps ORDER BY name")) {
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

                try (PreparedStatement preparedStatement1 = this.database.getConnection().prepareStatement("SELECT tag FROM tags_warps WHERE warp_id = ?")) {
                    preparedStatement1.setInt(1, warp.getId());
                    ResultSet resultSet1 = this.database.executeQuerySync(preparedStatement1);
                    List<String> tags = new ArrayList<>();
                    while (resultSet1.next()) {
                        String tag = resultSet1.getString("tag");
                        tags.add(tag);
                        this.warpTagsRegistry.register(tag);
                    }

                    warp.getTags().clear();
                    warp.getTags().addAll(tags);
                }

                this.warps.add(warp);
            }
            this.plugin.getLogger().info("Loaded warps!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Boolean> registerAsync(Warp warp) {
        return CompletableFuture.supplyAsync(() -> this.registerSync(warp));
    }

    public synchronized boolean registerSync(Warp warp) {
        try {
            PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("INSERT INTO warps (id, name, city, state, latitude, longitude, head_id, yaw, pitch, height) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, warp.getId());
            preparedStatement.setString(2, warp.getName());
            preparedStatement.setString(3, warp.getCity());
            preparedStatement.setString(4, warp.getState());
            preparedStatement.setString(5, String.valueOf(warp.getLatitude()));
            preparedStatement.setString(6, String.valueOf(warp.getLongitude()));
            preparedStatement.setString(7, warp.getHeadId());
            preparedStatement.setFloat(8, warp.getYaw());
            preparedStatement.setFloat(9, warp.getPitch());
            preparedStatement.setDouble(10, warp.getHeight());

            this.database.executeUpdateSync(preparedStatement);
            this.warps.add(warp);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized void unregister(Warp warp) {
        this.warps.remove(warp);
        TeleportationBungee.getInstance().getWarpIdsManager().releaseIdAsync(warp.getId());
    }

    public synchronized void unregister(int id) {
        this.warps.removeIf(warp -> warp.getId() == id);
        TeleportationBungee.getInstance().getWarpIdsManager().releaseIdAsync(id);
    }

    public boolean isRegistered(Warp warp) {
        return this.warps.contains(warp);
    }

    public boolean isRegistered(int id) {
        return this.warps.stream().anyMatch(warp -> warp.getId() == id);
    }

    @Nullable
    public Warp getWarp(int id) {
        return this.warps.stream().filter(warp -> warp.getId() == id).findFirst().orElse(null);
    }

    public synchronized void addTagsToWarp(ProxiedPlayer player, int warpId, String... tags) {
        Warp warp = this.getWarp(warpId);
        if(warp == null) {
            player.sendMessage(TeleportationBungee.getFormattedMessage("§cDer §cWarp §cwurde §cnicht §cgefunden."));
            return;
        }

        List<String> tagsAdded = new ArrayList<>();
        List<String> tagsNotAdded = new ArrayList<>();

        CompletableFuture.runAsync(() -> {
            for (String tag : tags) {
                if (warp.getTags().stream().anyMatch(existingTag -> existingTag.equalsIgnoreCase(tag))) {
                    tagsNotAdded.add(tag);
                    continue;
                }
                this.warpTagsRegistry.register(tag);
                try (PreparedStatement preparedStatement = database.getConnection().prepareStatement("INSERT INTO tags_warps (tag, warp_id) VALUES (?, ?)")) {
                    preparedStatement.setString(1, tag);
                    preparedStatement.setInt(2, warp.getId());

                    this.database.executeUpdateSync(preparedStatement);
                    warp.getTags().add(tag);
                    tagsAdded.add(tag);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).thenRun(() -> {
            if (!tagsAdded.isEmpty()) {
                player.sendMessage(TeleportationBungee.getFormattedMessage("Folgende Tags wurden hinzugefügt: " + tagsAdded));
            }

            if (!tagsNotAdded.isEmpty()) {
                player.sendMessage(TeleportationBungee.getFormattedMessage("§cFolgende §cTags §cwurden §cnicht §chinzugefügt: " + tagsNotAdded));
            }
        });
    }

    public synchronized void removeTagsFromWarp(ProxiedPlayer player, int warpId, String... tags) {
        Warp warp = this.getWarp(warpId);
        if(warp == null) {
            player.sendMessage(TeleportationBungee.getFormattedMessage("§cDer §cWarp §cwurde §cnicht §cgefunden."));
            return;
        }

        List<String> tagsRemoved = new ArrayList<>();

        CompletableFuture.runAsync(() -> {
            for (String tag : tags) {
                this.warpTagsRegistry.unregister(tag);
                try (PreparedStatement preparedStatement = database.getConnection().prepareStatement("DELETE FROM tags_warps WHERE tag LIKE ? AND warp_id = ?")) {
                    preparedStatement.setString(1, tag);
                    preparedStatement.setInt(2, warp.getId());

                    this.database.executeUpdateSync(preparedStatement);
                    warp.getTags().remove(tag);
                    tagsRemoved.add(tag);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).thenRun(() -> {
            if (!tagsRemoved.isEmpty()) {
                player.sendMessage(TeleportationBungee.getFormattedMessage("Folgende Tags wurden entfernt: " + tagsRemoved));
            }
        });
    }

    public Set<Warp> getWarps() {
        return warps;
    }
}
