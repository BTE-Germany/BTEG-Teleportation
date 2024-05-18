package de.btegermany.teleportation.TeleportationBungee.registry;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;
import de.btegermany.teleportation.TeleportationBungee.data.Database;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class WarpTagsRegistry {

    private final Database database;
    private final Set<String> tags;

    public WarpTagsRegistry(Database database) {
        this.database = database;
        this.tags = new HashSet<>();
    }

    public void register(String tag) {
        this.tags.add(tag);
    }

    public void unregister(String tag) {
        this.tags.removeIf(existingTag -> existingTag.equalsIgnoreCase(tag));
    }

    public void editTag(ProxiedPlayer player, String tagOld, String tagNew, WarpsRegistry warpsRegistry) {
        try (PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("UPDATE tags_warps SET tag = ? WHERE tag = ?")) {
            preparedStatement.setString(1, tagNew);
            preparedStatement.setString(2, tagOld);
            this.database.executeUpdateAsync(preparedStatement).thenRun(() -> {
                warpsRegistry.getWarps().forEach(warp -> warp.getTags().replaceAll(tag -> tag.equalsIgnoreCase(tagOld) ? tagNew : tag));
                this.tags.remove(tagOld);
                this.tags.add(tagNew);
                player.sendMessage(TeleportationBungee.getFormattedMessage(String.format("Der Tag \"%s\" wurde zu \"%s\" geändert.", tagOld, tagNew)));
            });
        } catch (SQLException e) {
            player.sendMessage(TeleportationBungee.getFormattedMessage("§cEin §cFehler §cist §caufgetreten: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public Set<String> getTags() {
        return tags;
    }
}
