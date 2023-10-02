package de.btegermany.teleportation.TeleportationBukkit.gui;

import com.tchristofferson.pagedinventories.IPagedInventory;
import com.tchristofferson.pagedinventories.NavigationRow;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryClickHandler;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.BlueprintItem;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.BlueprintRange;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.GuiBlueprint;
import de.btegermany.teleportation.TeleportationBukkit.gui.blueprint.SinglePageWarpGuiAbstract;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class ManageWarpsGui extends SinglePageWarpGuiAbstract {

    public ManageWarpsGui(Player player, PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {
        super(player, "Warps bearbeiten", pluginMessenger, registriesProvider);

        inventory.addHandler(new PagedInventoryClickHandler() {
            @Override
            public void handle(ClickHandler clickHandler) {
                if(clickHandler.getCurrentItem().getItemMeta() == null || clickHandler.getCurrentItem().getItemMeta().getDisplayName().length() < 3) return;

                TextComponent button;
                TextComponent text = new TextComponent(TeleportationBukkit.getFormattedMessage("Nutze hierfür bitte den Command"));

                switch (clickHandler.getCurrentItem().getItemMeta().getDisplayName().substring(2)) {
                    case "Löschen" -> {
                        button = new TextComponent(" /nwarp delete ");
                        button.setColor(ChatColor.BLUE);
                        button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Command in der Chatzeile einfügen").create()));
                        button.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/nwarp delete "));
                        player.closeInventory();
                        player.spigot().sendMessage(text, button);
                    }
                    case "Bearbeiten" -> {
                        button = new TextComponent(" /nwarp change ");
                        button.setColor(ChatColor.BLUE);
                        button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Command in der Chatzeile einfügen").create()));
                        button.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/nwarp change "));
                        player.closeInventory();
                        player.spigot().sendMessage(text, button);
                    }
                    case "Erstellen" -> {
                        button = new TextComponent(" /nwarp create ");
                        button.setColor(ChatColor.BLUE);
                        button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Command in der Chatzeile einfügen").create()));
                        button.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/nwarp create "));
                        player.closeInventory();
                        player.spigot().sendMessage(text, button);
                    }
                }
            }
        });
    }

    @Nonnull
    @Override
    public IPagedInventory createInventory() {
        return pagedInventoryAPI.createPagedInventory(new NavigationRow(NAV_NEXT, NAV_PREVIOUS, NAV_CLOSE, NAV_SORT));
    }

    @Nonnull
    @Override
    public GuiBlueprint createBlueprint() {
        return new GuiBlueprint()
                .addRow(new BlueprintRange(0, 8, placeholderDefault))
                .addRow(new BlueprintRange(0, 1, placeholderDefault),
                        new BlueprintRange(2, new BlueprintItem()),
                        new BlueprintRange(3, placeholderDefault),
                        new BlueprintRange(4, new BlueprintItem()),
                        new BlueprintRange(5, placeholderDefault),
                        new BlueprintRange(6, new BlueprintItem()),
                        new BlueprintRange(7, 8, placeholderDefault))
                .addRow(new BlueprintRange(0, 8, placeholderDefault));
    }

    @Nonnull
    @Override
    public List<ItemStack> getContent() {

        ItemStack itemDelete = Skulls.getSkull(Skulls.Skin.MINUS);
        ItemMeta metaDelete = itemDelete.getItemMeta();
        metaDelete.setDisplayName("Löschen");
        itemDelete.setItemMeta(metaDelete);
        ItemStack itemEdit = Skulls.getSkull(Skulls.Skin.EDIT);
        ItemMeta metaEdit = itemEdit.getItemMeta();
        metaEdit.setDisplayName("Bearbeiten");
        itemEdit.setItemMeta(metaEdit);
        ItemStack itemAdd = Skulls.getSkull(Skulls.Skin.PLUS);
        ItemMeta metaAdd = itemAdd.getItemMeta();
        metaAdd.setDisplayName("Erstellen");
        itemAdd.setItemMeta(metaAdd);

        return Arrays.asList(itemDelete, itemEdit, itemAdd);
    }

}
