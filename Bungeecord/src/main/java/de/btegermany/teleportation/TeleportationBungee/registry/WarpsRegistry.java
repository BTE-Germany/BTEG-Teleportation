package de.btegermany.teleportation.TeleportationBungee.registry;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.data.Database;
import de.btegermany.teleportation.TeleportationBungee.util.Warp;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

                try (PreparedStatement preparedStatement1 = database.getConnection().prepareStatement("SELECT tag FROM tags_warps WHERE warp_id = ?")) {
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

    @Nullable
    public Warp getWarp(int id) {
        return this.warps.stream().filter(warp -> warp.getId() == id).findFirst().orElse(null);
    }

    public void addTagsToWarp(ProxiedPlayer player, int warpId, String... tags) {
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
            if (tagsAdded.size() > 0) {
                player.sendMessage(TeleportationBungee.getFormattedMessage("Folgende Tags wurden hinzugefügt: " + tagsAdded));
            }

            if (tagsNotAdded.size() > 0) {
                player.sendMessage(TeleportationBungee.getFormattedMessage("§cFolgende §cTags §cwurden §cnicht §chinzugefügt: " + tagsNotAdded));
            }
        });
    }

    public void removeTagsFromWarp(ProxiedPlayer player, int warpId, String... tags) {
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
            if (tagsRemoved.size() > 0) {
                player.sendMessage(TeleportationBungee.getFormattedMessage("Folgende Tags wurden entfernt: " + tagsRemoved));
            }
        });
    }

    public Set<Warp> getWarps() {
        return warps;
    }
}
