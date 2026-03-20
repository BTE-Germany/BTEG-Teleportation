package de.btegermany.teleportation.TeleportationBukkit.gui;

import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemData {

    private final String name;
    private final String headId;
    private final String city;
    private final String state;
    private final Double latitude;
    private final Double longitude;
    private final Integer id;
    private final Float yaw;
    private final Float pitch;
    private final Double height;
    private final String world;

    public ItemData(JSONObject itemJson) {
        this.name = itemJson.has("name") ? itemJson.getString("name") : null;
        this.headId = itemJson.has("head_id") ? itemJson.getString("head_id") : null;
        this.city = itemJson.has("city") ? itemJson.getString("city") : null;
        this.state = itemJson.has("state") ? itemJson.getString("state") : null;
        this.latitude = itemJson.has("latitude") ? itemJson.getDouble("latitude") : null;
        this.longitude = itemJson.has("longitude") ? itemJson.getDouble("longitude") : null;
        this.id = itemJson.has("id") ? itemJson.getInt("id") : null;
        this.yaw = itemJson.has("yaw") ? itemJson.getFloat("yaw") : null;
        this.pitch = itemJson.has("pitch") ? itemJson.getFloat("pitch") : null;
        this.height = itemJson.has("height") ? itemJson.getDouble("height") : null;
        this.world = itemJson.has("world") ? itemJson.getString("world") : null;

    }

    public String getTpllCommand() {
        return String.format(Locale.US, "tpll %f %f %f yaw=%f pitch=%f world=%s", this.latitude, this.longitude, this.height, this.yaw, this.pitch, this.world);
    }

    public ItemStack getItemStack(Skulls.Skin defaultHead) {
        ItemStack item = this.headId != null ? Skulls.getSkullFromId(this.headId) : Skulls.getSkull(defaultHead);

        List<Component> lore = new ArrayList<>();
        if (this.city != null) {
            lore.add(Component.text("Stadt: %s".formatted(this.city)));
        }
        if (this.state != null) {
            lore.add(Component.text("Bundesland: %s".formatted(this.state)));
        }
        if (this.latitude != null && this.longitude != null) {
            lore.add(Component.text(String.format(Locale.US, "Koordinaten: %f, %f", this.latitude, this.longitude)));
        }
        if (this.id != null) {
            lore.add(Component.text("Id: %d".formatted(this.id)));
        }

        ItemMeta meta = item.getItemMeta();
        meta.customName(Component.text(this.name, NamedTextColor.AQUA));
        meta.lore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public String getName() {
        return name;
    }

}
