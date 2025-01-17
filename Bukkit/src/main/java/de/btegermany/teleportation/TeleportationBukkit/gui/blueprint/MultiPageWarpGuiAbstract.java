package de.btegermany.teleportation.TeleportationBukkit.gui.blueprint;

import com.tchristofferson.pagedinventories.handlers.PagedInventoryCloseHandler;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiPageWarpGuiAbstract extends WarpGuiAbstract {

    protected final PluginMessenger pluginMessenger;
    protected final JSONArray pagesData;
    protected final String headId;

    public MultiPageWarpGuiAbstract(MultiPageGuiArgs guiArgs, String title, String headId) {
        super(guiArgs.guiArgs(), title, guiArgs.contentJSON().length() > 1);
        this.pluginMessenger = guiArgs.pluginMessenger();
        this.pagesData = guiArgs.contentJSON();
        this.headId = headId;
        this.addPages(pagesData);
        inventory.addHandler(new PagedInventoryCloseHandler() {
            @Override
            public void handle(CloseHandler closeHandler) {
                registriesProvider.getMultiplePagesGuisRegistry().unregister(player);
            }
        });
    }

    // adds the pages specified by the JSONArray to the guis data
    public void addPages(JSONArray pagesData) {
        for(int i = 0; i < pagesData.length(); i++) {
            JSONObject pageData = pagesData.getJSONObject(i);
            int page = pageData.getInt("page");
            JSONArray pageContent = pageData.getJSONArray("content");
            if(page < inventory.getSize()) {
                continue;
            }

            currentInventory = Bukkit.createInventory(player, blueprint.getRowsCount() * 9, title + " - Seite " + (page + 1));
            for(int j = 0; j < pageContent.length(); j++) {
                JSONObject itemObject = pageContent.getJSONObject(j);
                currentInventory.setItem(j, getItem(itemObject));
            }
            for(int j = 0; j < currentInventory.getSize(); j++) {
                if(currentInventory.getItem(j) != null) {
                    continue;
                }
                currentInventory.setItem(j, placeholderDefault.getItem());
            }
            inventory.addPage(currentInventory);
        }
    }

    @Override
    public void open() {
        if(inventory.getSize() == 0) {
            this.player.closeInventory();
            this.player.sendMessage(TeleportationBukkit.getFormattedMessage("Leider wurden keine passenden Warps gefunden."));
            return;
        }
        super.open();
        registriesProvider.getMultiplePagesGuisRegistry().register(player, this);
    }

    // creates an item from a JSONObject with item data
    private ItemStack getItem(JSONObject object) {
        String name = object.getString("name");
        String headId = object.has("head_id") ? object.getString("head_id") : this.headId;
        ItemStack item = Skulls.getSkullFromId(headId);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + name);
        List<String> lore = new ArrayList<>();
        if(object.has("city")) {
            lore.add("Stadt: " + object.getString("city"));
        }
        if(object.has("state")) {
            lore.add("Bundesland: " + object.getString("state"));
        }
        if(object.has("latitude") && object.has("longitude")) {
            lore.add("/tpll " + object.getDouble("latitude") + ", " + object.getDouble("longitude"));
        }
        if(object.has("id")) {
            lore.add("Id: " + object.getInt("id"));
        }
        if(object.has("yaw") && object.has("pitch")) {
            lore.add("Drehung: " + object.getDouble("yaw") + ", " + object.getDouble("pitch"));
        }
        if(object.has("height")) {
            lore.add("Höhe: " + object.getDouble("height"));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

}
