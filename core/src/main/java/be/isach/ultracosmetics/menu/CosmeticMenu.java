package be.isach.ultracosmetics.menu;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.Cosmetic;
import be.isach.ultracosmetics.cosmetics.type.CosmeticMatType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.UCMaterial;
import com.udojava.evalex.Expression;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


/**
 * A cosmetic menu.
 *
 * @author iSach
 * @since 08-09-2016
 */
public abstract class CosmeticMenu<T extends CosmeticMatType> extends Menu {
    
    /*
    public final static int[] COSMETICS_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };
    */
    
    public static List<Integer> COSMETICS_SLOTS = UltraCosmeticsData.get().getPlugin().getConfig().getIntegerList("Category-Menu-Slots-Pattern");



    private Category category;

    public CosmeticMenu(UltraCosmetics ultraCosmetics, Category category) {
        super(ultraCosmetics);
        this.category = category;
    }

    @Override
    public void open(UltraPlayer player) {
        open(player, 1);
    }

    public void open(UltraPlayer player, int page) {
        if (page > getMaxPages()) {
            page = getMaxPages();
        }
        if (page < 1) {
            page = 1;
        }

        Inventory inventory = Bukkit.createInventory(null, getSize(), getMaxPages() == 1 ? getName() : getName(page));

        // Cosmetic items.
        int i = 0;
        int from = 21 * (page - 1) + 1;
        int to = 21 * page;
        for (int h = from; h <= to; h++) {
            if (h > enabled().size()) {
                break;
            }

            T cosmeticMatType = enabled().get(h - 1);

            if (!cosmeticMatType.isEnabled()) {
                continue;
            }

            if (SettingsManager.getConfig().getBoolean("No-Permission.Dont-Show-Item")
                    && !player.hasPermission(cosmeticMatType.getPermission())) {
                continue;
            }

            // Update
            COSMETICS_SLOTS = UltraCosmeticsData.get().getPlugin().getConfig().getIntegerList("Category-Menu-Slots-Pattern");

            if (SettingsManager.getConfig().getBoolean("No-Permission.Custom-Item.enabled")
                    && !player.hasPermission(cosmeticMatType.getPermission())) {
                UCMaterial material = UCMaterial.matchUCMaterial(SettingsManager.getConfig().getString("No-Permission.Custom-Item.Type"));
                // Byte data = Byte.valueOf(SettingsManager.getConfig().getString("No-Permission.Custom-Item.Data"));
                String name = ChatColor.translateAlternateColorCodes('&', SettingsManager.getConfig().getString("No-Permission.Custom-Item.Name")).replace("{cosmetic-name}", cosmeticMatType.getName());
                List<String> npLore = SettingsManager.getConfig().getStringList("No-Permission.Custom-Item.Lore");
                String[] array = new String[npLore.size()];
                npLore.toArray(array);
                putItem(inventory, COSMETICS_SLOTS.get(i), ItemFactory.create(material, name, array), clickData -> {
                    Player clicker = clickData.getClicker().getBukkitPlayer();
                    clicker.sendMessage(MessageManager.getMessage("No-Permission"));
                    clicker.closeInventory();
                });
                i++;
                continue;
            }

            String toggle = category.getActivateMenu();

            if (getCosmetic(player) != null && getCosmetic(player).getType() == cosmeticMatType) {
                toggle = category.getDeactivateMenu();
            }

            String typeName = getTypeName(cosmeticMatType, player);

            ItemStack is = ItemFactory.create(cosmeticMatType.getMaterial(), toggle + " " + typeName);
            if (getCosmetic(player) != null && getCosmetic(player).getType() == cosmeticMatType) {
                is = ItemFactory.addGlow(is);
            }

            ItemMeta itemMeta = is.getItemMeta();
            List<String> loreList = new ArrayList<>();

            if (cosmeticMatType.showsDescription()) {
                loreList.add("");
                loreList.addAll(cosmeticMatType.getDescription());
                loreList.add("");
            }

            if (SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore")) {
                String yesOrNo = player.hasPermission(cosmeticMatType.getPermission()) ? "Yes" : "No";
                String s = SettingsManager.getConfig().getString("No-Permission.Lore-Message-" + yesOrNo);
                loreList.add(ChatColor.translateAlternateColorCodes('&', s));
            }

            itemMeta.setLore(loreList);

            is.setItemMeta(itemMeta);
            is = filterItem(is, cosmeticMatType, player);

            putItem(inventory, COSMETICS_SLOTS.get(i), is, (data) -> {
                UltraPlayer ultraPlayer = data.getClicker();
                ItemStack clicked = data.getClicked();
                int currentPage = getCurrentPage(ultraPlayer);
                if (UltraCosmeticsData.get().shouldCloseAfterSelect()) {
                    ultraPlayer.getBukkitPlayer().closeInventory();
                }
                if (UltraCosmeticsData.get().isAmmoEnabled() && data.getAction() == InventoryAction.PICKUP_HALF) {
                    StringBuilder sb = new StringBuilder();
                    for (int k = 1; k < clicked.getItemMeta().getDisplayName().split(" ").length; k++) {
                        sb.append(clicked.getItemMeta().getDisplayName().split(" ")[k]);
                        try {
                            if (clicked.getItemMeta().getDisplayName().split(" ")[k + 1] != null)
                                sb.append(" ");
                        } catch (Exception ignored) {
                        }
                    }
                    if (getCosmetic(ultraPlayer) == null) {
                        toggleOff(ultraPlayer);
                    }
                    toggleOn(ultraPlayer, cosmeticMatType, getUltraCosmetics());

                    if (getCategory() == Category.GADGETS) {
                        if (ultraPlayer.getCurrentGadget().getType().requiresAmmo()) {
                            ultraPlayer.getCurrentGadget().lastPage = currentPage;
                            ultraPlayer.getCurrentGadget().openAmmoPurchaseMenu();
                            ultraPlayer.getCurrentGadget().openGadgetsInvAfterAmmo = true;
                        }
                    }
                    return;
                }

                if (clicked.getItemMeta().getDisplayName().startsWith(category.getDeactivateMenu())) {
                    toggleOff(ultraPlayer);
                    if (!UltraCosmeticsData.get().shouldCloseAfterSelect()) {
                        open(ultraPlayer, currentPage);
                    }
                } else if (clicked.getItemMeta().getDisplayName().startsWith(category.getActivateMenu())) {
                    toggleOff(ultraPlayer);
                    StringBuilder sb = new StringBuilder();
                    String name = clicked.getItemMeta().getDisplayName().replaceFirst(category.getActivateMenu(), "");
                    int j = name.split(" ").length;
                    if (name.contains("(")) {
                        j--;
                    }
                    for (int k = 1; k < j; k++) {
                        sb.append(name.split(" ")[k]);
                        try {
                            if (clicked.getItemMeta().getDisplayName().split(" ")[k + 1] != null)
                                sb.append(" ");
                        } catch (Exception ignored) {
                        }
                    }
                    toggleOn(ultraPlayer, cosmeticMatType, getUltraCosmetics());
                    if (category == Category.GADGETS &&
                            ultraPlayer.getCurrentGadget() != null &&
                            UltraCosmeticsData.get().isAmmoEnabled() && ultraPlayer.getAmmo(ultraPlayer.getCurrentGadget().getType().toString().toLowerCase()) < 1 && ultraPlayer.getCurrentGadget().getType().requiresAmmo()) {
                        ultraPlayer.getCurrentGadget().lastPage = currentPage;
                        ultraPlayer.getCurrentGadget().openAmmoPurchaseMenu();
                    } else {
                        if (!UltraCosmeticsData.get().shouldCloseAfterSelect()) {
                            open(ultraPlayer, currentPage);
                        }
                    }
                }
            });
            i++;
        }

        // Previous page item.
        if (page > 1) {
            int finalPage = page;

            String slotFromConfig = UltraCosmeticsData.get().getPlugin().getConfig().getString("Categories.Previous-Page-Slot");
            int slot;
            if (slotFromConfig.contains("%size%")) {
                String slotToEval = slotFromConfig.replaceAll("%size%", getSize() + "");
                Expression expression = new Expression(slotToEval);
                slot = expression.eval().intValue();
            } else {
                slot = Integer.parseInt(slotFromConfig);
            }

            putItem(inventory, slot, ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Previous-Page-Item"),
                    MessageManager.getMessage("Menu.Previous-Page")), (data) -> open(player, finalPage - 1));
        }

        // Next page item.
        if (page < getMaxPages()) {
            int finalPage = page;

            String slotFromConfig = UltraCosmeticsData.get().getPlugin().getConfig().getString("Categories.Next-Page-Slot");
            int slot;
            if (slotFromConfig.contains("%size%")) {
                String slotToEval = slotFromConfig.replaceAll("%size%", getSize() + "");
                Expression expression = new Expression(slotToEval);
                slot = expression.eval().intValue();
            } else {
                slot = Integer.parseInt(slotFromConfig);
            }

            putItem(inventory, slot, ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Next-Page-Item"),
                    MessageManager.getMessage("Menu.Next-Page")), (data) -> open(player, finalPage + 1));
        }

        // Clear cosmetic item.
        String message = MessageManager.getMessage(category.getClearConfigPath());
        ItemStack itemStack = ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Clear-Cosmetic-Item"), message);

        String slotFromConfig = UltraCosmeticsData.get().getPlugin().getConfig().getString("Categories.Clear-Cosmetics-Slot");
        int clearSlot;
        if (slotFromConfig.contains("%size%")) {
            String slotToEval = slotFromConfig.replaceAll("%size%", getSize() + "");
            Expression expression = new Expression(slotToEval);
            clearSlot = expression.eval().intValue();
        } else {
            clearSlot = Integer.parseInt(slotFromConfig);
        }

        putItem(inventory, clearSlot, itemStack, data -> {
            toggleOff(player);
            open(player, getCurrentPage(player));
        });

        // Go Back to Main Menu Arrow.
        if (getCategory().hasGoBackArrow()) {
            ItemStack item = ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Back-Main-Menu-Item"), MessageManager.getMessage("Menu.Main-Menu"));

            String backSlotFromConfig = UltraCosmeticsData.get().getPlugin().getConfig().getString("Categories.Back-Main-Menu-Item-Slot");
            int slot;
            if (backSlotFromConfig.contains("%size%")) {
                String slotToEval = backSlotFromConfig.replaceAll("%size%", getSize() + "");
                Expression expression = new Expression(slotToEval);
                slot = expression.eval().intValue();
            } else {
                slot = Integer.parseInt(backSlotFromConfig);
            }

            putItem(inventory, slot, item, (data) -> getUltraCosmetics().openMainMenu(player));
        }

        putItems(inventory, player, page);
        ItemFactory.fillInventory(inventory);
        player.getBukkitPlayer().openInventory(inventory);
    }

    public T getCosmeticType(String name) {
        for (T effectType : enabled()) {
            if (effectType.getConfigName().replace(" ", "").equals(name.replace(" ", ""))) {
                return effectType;
            }
        }
        return null;
    }

    /**
     * @param ultraPlayer The menu owner.
     * @return The current page of the menu opened by ultraPlayer.
     */
    protected int getCurrentPage(UltraPlayer ultraPlayer) {
        Player player = ultraPlayer.getBukkitPlayer();
        String title = player.getOpenInventory().getTitle();
        if (player.getOpenInventory() != null
                && title.startsWith(getName())
                && !title.equals(getName())) {
            String s = player.getOpenInventory().getTitle()
                    .replace(getName() + " " + ChatColor.GRAY + "" + ChatColor.ITALIC + "(", "")
                    .replace("/" + getMaxPages() + ")", "");
            return Integer.parseInt(s);
        }
        return 0;
    }

    /**
     * Gets the max amount of pages.
     *
     * @return the maximum amount of pages.
     */
    protected int getMaxPages() {
        int max = 21;
        int i = enabled().size();
        if (i % max == 0) return i / max;
        double j = i / 21;
        int h = (int) Math.floor(j * 100) / 100;
        return h + 1;
    }

    protected int getItemsPerPage() {
        return 12;
    }

    /**
     * This method can be overridden
     * to modify an itemstack of a
     * category being placed in the
     * inventory.
     *
     * @param itemStack    Item Stack being placed.
     * @param cosmeticType The Cosmetic Type.
     * @param player       The Inventory Opener.
     * @return The new item stack filtered.
     */
    protected ItemStack filterItem(ItemStack itemStack, T cosmeticType, UltraPlayer player) {
        return itemStack;
    }

    protected String getTypeName(T cosmeticType, UltraPlayer ultraPlayer) {
        return cosmeticType.getName();
    }

    /**
     * @param page The page to open.
     * @return The name of the menu with page detailed.
     */
    protected String getName(int page) {
        return MessageManager.getMessage("Menus." + category.getConfigPath()) + " " + ChatColor.GRAY + "" + ChatColor.ITALIC + "(" + page + "/" + getMaxPages() + ")";
    }

    @Override
    protected int getSize() {
        int listSize = enabled().size();
        int slotAmount = 54;
        if (listSize < 22) {
            slotAmount = 54;
        }
        if (listSize < 15) {
            slotAmount = 45;
        }
        if (listSize < 8) {
            slotAmount = 36;
        }
        return slotAmount;
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer ultraPlayer) {
        //--
    }

    /**
     * @return The name of the menu.
     */
    @Override
    protected String getName() {
        return MessageManager.getMessage("Menus." + category.getConfigPath());
    }

    public Category getCategory() {
        return category;
    }

    /**
     * Puts items in the inventory.
     *
     * @param inventory   Inventory.
     * @param ultraPlayer Inventory Owner.
     * @param page        Page to open.
     */
    abstract protected void putItems(Inventory inventory, UltraPlayer ultraPlayer, int page);

    abstract public List<T> enabled();

    abstract protected void toggleOn(UltraPlayer ultraPlayer, T type, UltraCosmetics ultraCosmetics);

    abstract protected void toggleOff(UltraPlayer ultraPlayer);

    abstract protected Cosmetic getCosmetic(UltraPlayer ultraPlayer);
}
