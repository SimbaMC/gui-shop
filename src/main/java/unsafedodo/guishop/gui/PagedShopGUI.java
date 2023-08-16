package unsafedodo.guishop.gui;

import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import unsafedodo.guishop.shop.Shop;
import unsafedodo.guishop.shop.ShopItem;

public class PagedShopGUI extends ShopGUI {
    /**
     * Constructs a new simple container gui for the supplied player.
     *
     * @param player                the player to server this gui to
     * @param shop                  the shop opened
     *
     */

    protected int page = 1;
    protected int maxPage;
    public static final int MAX_PAGE_ITEMS = 36;
    public PagedShopGUI(ServerPlayerEntity player, Shop shop) {
        super(player, shop);
        maxPage = (int) Math.ceil((double) shop.getItems().size()/MAX_PAGE_ITEMS);

        this.setSlot(48, new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE)
                .setCallback(((index, type1, action) -> {
                    int oldPage = this.page;
                    this.page = getPreviousPage();
                    if(oldPage != page)
                        updateGUI();
                })));

        this.setSlot(49, new GuiElementBuilder(Items.PAPER)
                .setName(Text.literal(String.format("Current page: %d", getPage()))
                        .setStyle(Style.EMPTY.withItalic(true))
                            .formatted(Formatting.YELLOW))
                .glow()
                .setCount(getPage()));

        this.setSlot(50, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setSkullOwner(HeadTextures.GUI_NEXT_PAGE, null, null)
                .setName(Text.literal("Next page")
                        .setStyle(Style.EMPTY.withItalic(true))
                            .formatted(Formatting.YELLOW))
                .setCallback(((index, type1, action) -> {
                    int oldPage = this.page;
                    this.page = getNextPage();
                    if(oldPage != page)
                        updateGUI();
                })));
    }

    public void updateGUI(){
        int n = MAX_PAGE_ITEMS*(page);

        for (int i = MAX_PAGE_ITEMS*(page-1); i < n; i++) {
            if(i < shop.getItems().size()){
                ShopItem item = shop.getItems().get(i);
                ItemStack guiItem = new ItemStack(Registry.ITEM.get(new Identifier(item.getItemMaterial())));
                guiItem.setNbt(item.getNbt());
                Text name = TextParserUtils.formatText(item.getItemName());
                this.setSlot((i-(MAX_PAGE_ITEMS*(page-1))), GuiElementBuilder.from(guiItem)
                        .setName(name)
                        .setLore(item.getDescriptionAsText())
                        .addLoreLine(Text.literal(""))
                        .addLoreLine(Text.literal(""))
                        .addLoreLine(Text.literal(""))
                        .addLoreLine(item.getLoreBuyPrice(1))
                        .addLoreLine(item.getLoreSellPrice(1))
                        .setCallback((index, type1, action, gui) -> {
                            NewQuantityGUI qGUI = new NewQuantityGUI(player, item, gui);
                            gui.close();
                            qGUI.open();
                        }));
            } else {
                this.setSlot((i-(MAX_PAGE_ITEMS*(page-1))), new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE).setName(Text.empty()));
            }

        }

        this.setSlot(49, new GuiElementBuilder(Items.PAPER)
                .setName(Text.literal(String.format("Current page: %d", getPage()))
                        .setStyle(Style.EMPTY.withItalic(true))
                        .formatted(Formatting.YELLOW))
                .glow()
                .setCount(getPage()));

        if(page == 1){
            this.setSlot(48, new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE)
                    .setCallback(((index, type1, action) -> {
                        int oldPage = this.page;
                        this.page = getPreviousPage();
                        if(oldPage != page)
                            updateGUI();
                    })));

        } else {
            this.setSlot(48, new GuiElementBuilder(Items.PLAYER_HEAD)
                    .setSkullOwner(HeadTextures.GUI_PREVIOUS_PAGE, null, null)
                    .setName(Text.literal("Previous page")
                            .setStyle(Style.EMPTY.withItalic(true))
                            .formatted(Formatting.YELLOW))
                    .setCallback(((index, type1, action) -> {
                        int oldPage = this.page;
                        this.page = getPreviousPage();
                        if(oldPage != page)
                            updateGUI();
                    })));
        }

        if(page == maxPage){
            this.setSlot(50, new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE)
                    .setCallback(((index, type1, action) -> {
                        int oldPage = this.page;
                        this.page = getNextPage();
                        if(oldPage != page)
                            updateGUI();
                    })));
        } else {
            this.setSlot(50, new GuiElementBuilder(Items.PLAYER_HEAD)
                    .setSkullOwner(HeadTextures.GUI_NEXT_PAGE, null, null)
                    .setName(Text.literal("Next page")
                            .setStyle(Style.EMPTY.withItalic(true))
                            .formatted(Formatting.YELLOW))
                    .setCallback(((index, type1, action) -> {
                        int oldPage = this.page;
                        this.page = getNextPage();
                        if(oldPage != page)
                            updateGUI();
                    })));
        }
    }

    public int getPage(){
        return this.page;
    }

    public int getPreviousPage(){
        return Math.max(1, page-1);
    }

    public int getNextPage(){
        return Math.min(page+1, maxPage);
    }
}
