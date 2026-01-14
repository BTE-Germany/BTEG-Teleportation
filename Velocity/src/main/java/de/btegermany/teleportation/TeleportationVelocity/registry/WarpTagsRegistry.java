package de.btegermany.teleportation.TeleportationVelocity.registry;

import static de.btegermany.teleportation.TeleportationVelocity.TeleportationVelocity.sendMessage;

import com.velocitypowered.api.proxy.Player;
import de.btegermany.teleportation.TeleportationVelocity.data.Database;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class WarpTagsRegistry {

    private final Database database;
    @Getter
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

    public void editTag(Player player, String tagOld, String tagNew, WarpsRegistry warpsRegistry) {
        try (PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("UPDATE tags_warps SET tag = ? WHERE tag = ?")) {
            preparedStatement.setString(1, tagNew);
            preparedStatement.setString(2, tagOld);
            this.database.executeUpdateAsync(preparedStatement).thenRun(() -> {
                warpsRegistry.getWarps().forEach(warp -> warp.getTags().replaceAll(tag -> tag.equalsIgnoreCase(tagOld) ? tagNew : tag));
                this.tags.remove(tagOld);
                this.tags.add(tagNew);
                sendMessage(player, Component.text(String.format("Der Tag \"%s\" wurde zu \"%s\" geändert.", tagOld, tagNew), NamedTextColor.GOLD));
            });
        } catch (SQLException e) {
            sendMessage(player, Component.text("Ein Fehler ist aufgetreten: " + e.getMessage(), NamedTextColor.RED));
            e.printStackTrace();
        }
    }

}
