package de.btegermany.teleportation.TeleportationBukkit.gui.blueprint;

import com.tchristofferson.pagedinventories.IPagedInventory;
import com.tchristofferson.pagedinventories.PagedInventoryAPI;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryCustomNavigationHandler;
import com.tchristofferson.pagedinventories.navigationitems.CloseNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.CustomNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.NextNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.PreviousNavigationItem;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.gui.ManageWarpsGui;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;
import de.btegermany.teleportation.TeleportationBukkit.gui.WarpGui;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;

public abstract class WarpGuiAbstract {

    protected final Player player;
    protected final PagedInventoryAPI pagedInventoryAPI = TeleportationBukkit.getPagedInventoryAPI();
    protected final IPagedInventory inventory;
    protected final PluginMessenger pluginMessenger;
    protected final RegistriesProvider registriesProvider;
    protected Inventory currentInventory;
    protected final GuiBlueprint blueprint;
    protected final String title;
    protected final BlueprintItem placeholderDefault;
    private final boolean showNextPrevious;
    public final NextNavigationItem NAV_NEXT = new NextNavigationItem(Skulls.getSkull(Skulls.Skin.ARROW_RIGHT), 8);
    public final PreviousNavigationItem NAV_PREVIOUS = new PreviousNavigationItem(Skulls.getSkull(Skulls.Skin.ARROW_LEFT), 7);
    public final CloseNavigationItem NAV_CLOSE = new CloseNavigationItem(new ItemStack(Material.BARRIER), 4);
    public final CustomNavigationItem NAV_SORT = new CustomNavigationItem(new ItemStack(Material.HOPPER), 0) {
        @Override
        public void handleClick(PagedInventoryCustomNavigationHandler pagedInventoryCustomNavigationHandler) {
            openSortGui();
        }
    };
    public final CustomNavigationItem NAV_MANAGE = new CustomNavigationItem(new ItemStack(Material.ANVIL), 2) {
        @Override
        public void handleClick(PagedInventoryCustomNavigationHandler pagedInventoryCustomNavigationHandler) {
            openManageGui();
        }
    };
    public final CustomNavigationItem NAV_SEARCH = new CustomNavigationItem(new ItemStack(Material.COMPASS), 1) {
        @Override
        public void handleClick(PagedInventoryCustomNavigationHandler pagedInventoryCustomNavigationHandler) {
            TextComponent button = new TextComponent(" /nwarp ");
            button.setColor(ChatColor.BLUE);
            button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Command in der Chatzeile einfügen").create()));
            button.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/nwarp "));
            TextComponent text1 = new TextComponent(TeleportationBukkit.getFormattedMessage("Nutze den Command"));
            TextComponent text2 = new TextComponent(TeleportationBukkit.getFormattedMessage("und gib dahinter den Namen eines Warps oder der Stadt, die du suchst, ein!").substring(13));
            pagedInventoryCustomNavigationHandler.getPlayer().closeInventory();
            pagedInventoryCustomNavigationHandler.getPlayer().spigot().sendMessage(text1, button, text2);
        }
    };
    public final CustomNavigationItem NAV_LOBBY_SORT = new CustomNavigationItem(new ItemStack(Material.HOPPER), 0) {
        @Override
        public void handleClick(PagedInventoryCustomNavigationHandler pagedInventoryCustomNavigationHandler) {
            openLobbySortGui();
        }
    };
    public CustomNavigationItem NAV_LOBBY_AROUND = new CustomNavigationItem(new ItemStack(Material.SMOOTH_BRICK, 1, (short) 3), 1) {
        @Override
        public void handleClick(PagedInventoryCustomNavigationHandler handler) {}
    };
    public final CustomNavigationItem NAV_PLACEHOLDER_DEFAULT;

    public WarpGuiAbstract(Player player, String title, boolean showNextPrevious, PluginMessenger pluginMessenger, RegistriesProvider registriesProvider) {
        this.player = player;
        this.title = title;
        this.showNextPrevious = showNextPrevious;
        this.pluginMessenger = pluginMessenger;
        this.registriesProvider = registriesProvider;

        ItemStack placeholderDefaultItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        ItemMeta placeholderDefaultMeta = placeholderDefaultItem.getItemMeta();
        placeholderDefaultMeta.setDisplayName(" ");
        placeholderDefaultItem.setItemMeta(placeholderDefaultMeta);
        placeholderDefault = new BlueprintItem(placeholderDefaultItem);
        NAV_PLACEHOLDER_DEFAULT = new CustomNavigationItem(placeholderDefaultItem, 6) {@Override public void handleClick(PagedInventoryCustomNavigationHandler pagedInventoryCustomNavigationHandler) {}};

        ItemMeta metaNext = NAV_NEXT.getItemStack().getItemMeta();
        metaNext.setDisplayName(ChatColor.GOLD + "Nächste Seite");
        ItemMeta metaPrevious = NAV_PREVIOUS.getItemStack().getItemMeta();
        metaPrevious.setDisplayName(ChatColor.GOLD + "Vorherige Seite");
        ItemMeta metaClose = NAV_CLOSE.getItemStack().getItemMeta();
        metaClose.setDisplayName(ChatColor.DARK_RED + "Schließen");
        ItemMeta metaSort = NAV_SORT.getItemStack().getItemMeta();
        metaSort.setDisplayName(ChatColor.GOLD + "ZUR ÜBERSICHT");
        ItemMeta metaManage = NAV_MANAGE.getItemStack().getItemMeta();
        metaManage.setDisplayName(ChatColor.RED + "Warps bearbeiten");
        ItemMeta metaSearch = NAV_SEARCH.getItemStack().getItemMeta();
        metaSearch.setDisplayName(ChatColor.GOLD + "Suchen");
        ItemMeta metaLobbySort = NAV_LOBBY_SORT.getItemStack().getItemMeta();
        metaLobbySort.setDisplayName(ChatColor.GOLD + "Zurück");
        ItemMeta metaLobbyAround = NAV_LOBBY_AROUND.getItemStack().getItemMeta();
        metaLobbyAround.setDisplayName(ChatColor.GOLD + "Warps im Umkreis");
        NAV_NEXT.getItemStack().setItemMeta(metaNext);
        NAV_PREVIOUS.getItemStack().setItemMeta(metaPrevious);
        NAV_CLOSE.getItemStack().setItemMeta(metaClose);
        NAV_SORT.getItemStack().setItemMeta(metaSort);
        NAV_MANAGE.getItemStack().setItemMeta(metaManage);
        NAV_SEARCH.getItemStack().setItemMeta(metaSearch);
        NAV_LOBBY_SORT.getItemStack().setItemMeta(metaLobbySort);
        NAV_LOBBY_AROUND.getItemStack().setItemMeta(metaLobbyAround);

        inventory = createInventory();
        if(player.hasPermission("bteg.warps.manage")) {
            inventory.getNavigationRow().set(2, NAV_MANAGE);
        }
        for(int i = 0; i < 9; i++) {
            if(inventory.getNavigationItem(i) != null) {
                continue;
            }
            inventory.getNavigationRow().set(i, NAV_PLACEHOLDER_DEFAULT);
        }
        blueprint = createBlueprint();
        onStartup();
    }

    public abstract @Nonnull IPagedInventory createInventory();

    public abstract @Nonnull GuiBlueprint createBlueprint();

    public void onStartup() {}

    public void open() {
        inventory.open(player);

        Inventory topInv = player.getOpenInventory().getTopInventory();
        for(int i = topInv.getSize() - 9; i < topInv.getSize(); i++) {
            if(topInv.getItem(i) == null) {
                topInv.setItem(i, placeholderDefault.getItem());
            }
        }
    }

    public void openSortGui() {
        new WarpGui(player, pluginMessenger, registriesProvider).open();
    }

    public void openManageGui() {
        new ManageWarpsGui(player, pluginMessenger, registriesProvider).open();
    }

    public void openLobbySortGui() {
        player.closeInventory();
        player.performCommand("lobbywarp " + title);
    }

}
