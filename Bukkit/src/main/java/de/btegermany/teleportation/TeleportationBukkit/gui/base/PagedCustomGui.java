package de.btegermany.teleportation.TeleportationBukkit.gui.base;

import de.btegermany.teleportation.TeleportationAPI.PagedGuiType;
import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.gui.GuiItems;
import de.btegermany.teleportation.TeleportationBukkit.gui.GuiUtils;
import de.btegermany.teleportation.TeleportationBukkit.gui.ItemData;
import de.btegermany.teleportation.TeleportationBukkit.gui.PagedGuiHandler;
import de.btegermany.teleportation.TeleportationBukkit.util.Skulls;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public abstract class PagedCustomGui extends CustomGui {

    private static final int PAGES_TO_LOAD_DATA_BEFORE_LAST = 1;
    private static final int SLOT_PREVIOUS_ITEM = 5;
    private static final int SLOT_NEXT_ITEM = 6;

    private final String title;
    private final String customData;
    protected final PagedGuiType type;
    protected final PaginatedGui gui;
    private final Skulls.Skin defaultHead;
    protected final PagedGuiHandler pagedGuiHandler;
    private final TeleportationBukkit plugin;
    private final String[] requestArgs;

    public PagedCustomGui(String title, String customData, PagedGuiType type, Skulls.Skin defaultHead, Player player, PagedGuiHandler pagedGuiHandler, TeleportationBukkit plugin, String... requestArgs) {
        super(player);
        this.title = title;
        this.customData = customData;
        this.type = type;
        this.defaultHead = defaultHead;
        this.pagedGuiHandler = pagedGuiHandler;
        this.plugin = plugin;
        this.requestArgs = requestArgs;

        this.gui = Gui.paginated()
                .title(GuiUtils.getTitle(title, customData))
                .rows(ROWS_COUNT)
                .disableAllInteractions()
                .create();

        this.gui.setItem(List.of(0, 1, 2, 3, 4, 5, 6, 7), GuiItems.blankItem());
        this.gui.setItem(8, GuiItems.closeItem(this));
    }

    public PagedCustomGui(String title, PagedGuiType type, Skulls.Skin defaultHead, Player player, PagedGuiHandler pagedGuiHandler, TeleportationBukkit plugin, String... requestArgs) {
        this(title, "ङ", type, defaultHead, player, pagedGuiHandler, plugin, requestArgs);
    }

    private GuiItem previousPageItem() {
        return GuiItems.emptyItem("Vorherige Seite", NamedTextColor.GOLD, this::previousPage);
    }

    private GuiItem nextPageItem() {
        return GuiItems.emptyItem("Nächste Seite", NamedTextColor.GOLD, () -> {
            int pagesNum = this.gui.getPagesNum();
            if (this.gui.getCurrentPageNum() == pagesNum - PAGES_TO_LOAD_DATA_BEFORE_LAST) {
                this.pagedGuiHandler.loadData(this, this.requestArgs, pagesNum + 1);
                return;
            }

            this.nextPage();
        });
    }

    public void updateNavbar() {
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            int currentPageNum = this.gui.getCurrentPageNum();
            int pagesNum = this.gui.getPagesNum();

            if (pagesNum == 1) {
                return;
            }

            if (currentPageNum == 1) {
                this.gui.updateItem(SLOT_PREVIOUS_ITEM, GuiItems.blankItem());
                this.gui.updateItem(SLOT_NEXT_ITEM, this.nextPageItem());
            } else if (currentPageNum == pagesNum) {
                this.gui.updateItem(SLOT_PREVIOUS_ITEM, this.previousPageItem());
                this.gui.updateItem(SLOT_NEXT_ITEM, GuiItems.blankItem());
            }

            if (pagesNum == 2) {
                return;
            }

            if (currentPageNum > 1 && currentPageNum < pagesNum) {
                this.gui.updateItem(SLOT_PREVIOUS_ITEM, this.previousPageItem());
                this.gui.updateItem(SLOT_NEXT_ITEM, this.nextPageItem());
            }
        });
    }

    public void addPages(JSONArray pagesData) {
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            for (int i = 0; i < pagesData.length(); i++) {
                JSONObject pageData = pagesData.getJSONObject(i);
                int page = pageData.getInt("page");
                JSONArray pageContent = pageData.getJSONArray("content");
                if (page > 1 && page <= this.gui.getPagesNum()) {
                    continue;
                }

                for (int j = 0; j < ((this.gui.getRows() - GuiUtils.NAVBAR_ROWS) * 9); j++) {
                    if (pageContent.length() > j) {
                        ItemData itemData = new ItemData(pageContent.getJSONObject(j));
                        this.gui.addItem(new GuiItem(itemData.getItemStack(this.defaultHead), event -> this.onClick(itemData)));
                        continue;
                    }
                    this.gui.addItem(GuiItems.fillerItem());
                }
            }

            this.gui.update();
        });
    }

    public void previousPage() {
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            this.gui.previous();
            this.updateNavbar();
            this.updateTitle();
        });
    }

    public void nextPage() {
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            this.gui.next();
            this.updateNavbar();
            this.updateTitle();
        });
    }

    public void updateTitle() {
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            if (this.gui.getPagesNum() == 1) {
                return;
            }
            this.gui.updateTitle(GuiUtils.getTitle(this.title + " - Seite " + this.gui.getCurrentPageNum(), this.customData));
        });
    }

    @Override
    public void open() {
        Bukkit.getScheduler().runTask(this.plugin, () -> this.pagedGuiHandler.open(this, this.requestArgs));
    }

    @Override
    public void close() {
        Bukkit.getScheduler().runTask(this.plugin, () -> this.pagedGuiHandler.close(this));
    }

    protected abstract void onClick(ItemData itemData);

    public PagedGuiType getType() {
        return type;
    }

    public PaginatedGui getGui() {
        return gui;
    }
}
