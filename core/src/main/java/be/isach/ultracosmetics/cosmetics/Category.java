package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.*;
import be.isach.ultracosmetics.menu.CosmeticMenu;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Cosmetic category enum.
 *
 * @author iSach
 * @since 06-20-2016
 */
public enum Category {

    PETS("Pets", "Spawn", "Despawn", "Clear-Pet", "%petname%", "Spawn", "Despawn") {
        @Override
        public CosmeticMenu getMenu(UltraCosmetics ultraCosmetics) {
            return ultraCosmetics.getMenus().getPetsMenu();
        }

        @Override
        public List<? extends CosmeticType> getEnabled() {
            return PetType.enabled();
        }
    },
    GADGETS("Gadgets", "Activate", "Deactivate", "Clear-Gadget", "%gadgetname%", "Equip", "Unequip") {
        @Override
        public CosmeticMenu getMenu(UltraCosmetics ultraCosmetics) {
            return ultraCosmetics.getMenus().getGadgetsMenu();
        }

        @Override
        public List<? extends CosmeticType> getEnabled() {
            return GadgetType.enabled();
        }
    },
    EFFECTS("Particle-Effects", "Summon", "Unsummon", "Clear-Effect", "%effectname%", "Summon", "Unsummon") {
        @Override
        public CosmeticMenu getMenu(UltraCosmetics ultraCosmetics) {
            return ultraCosmetics.getMenus().getEffectsMenu();
        }

        @Override
        public List<? extends CosmeticType> getEnabled() {
            return ParticleEffectType.enabled();
        }
    },
    MOUNTS("Mounts", "Spawn", "Despawn", "Clear-Mount", "%mountname%", "Spawn", "Despawn") {
        @Override
        public CosmeticMenu getMenu(UltraCosmetics ultraCosmetics) {
            return ultraCosmetics.getMenus().getMountsMenu();
        }

        @Override
        public List<? extends CosmeticType> getEnabled() {
            return MountType.enabled();
        }
    },
    MORPHS("Morphs", "Morph", "Unmorph", "Clear-Morph", "%morphname%", "Morph", "Unmorph") {
        @Override
        public CosmeticMenu getMenu(UltraCosmetics ultraCosmetics) {
            return ultraCosmetics.getMenus().getMorphsMenu();
        }

        @Override
        public List<? extends CosmeticType> getEnabled() {
            return MorphType.enabled();
        }
    },
    HATS("Hats", "Equip", "Unequip", "Clear-Hat", "%hatname%", "Equip", "Unequip") {
        @Override
        public CosmeticMenu getMenu(UltraCosmetics ultraCosmetics) {
            return ultraCosmetics.getMenus().getHatsMenu();
        }

        @Override
        public List<? extends CosmeticType> getEnabled() {
            return HatType.enabled();
        }
    },
    SUITS("Suits", "Equip", "Unequip", "Clear-Suit", "%suitname%", "Equip", "Unequip") {
        @Override
        public CosmeticMenu getMenu(UltraCosmetics ultraCosmetics) {
            return ultraCosmetics.getMenus().getSuitsMenu();
        }

        @Override
        public List<? extends CosmeticType> getEnabled() {
            return SuitType.enabled();
        }
    },
    EMOTES("Emotes", "Equip", "Unequip", "Clear-Emote", "%emotename%", "Equip", "Unequip") {
        @Override
        public CosmeticMenu getMenu(UltraCosmetics ultraCosmetics) {
            return ultraCosmetics.getMenus().getEmotesMenu();
        }

        @Override
        public List<? extends CosmeticType> getEnabled() {
            return EmoteType.enabled();
        }
    };

    public static int enabledSize() {
        return enabled().size();
    }

    public static List<Category> enabled() {
        return Arrays.stream(values()).filter(Category::isEnabled).collect(Collectors.toList());
    }

    /**
     * The config path name.
     */
    private String configPath;

    /**
     * The ItemStack in Main Menu.
     */
    private ItemStack is;

    /**
     * The Slot of the category in Main Menu.
     */
    private Integer slot; // Integer allows checking if slot == null

    /**
     * Message on menu to activate a cosmetic of this category.
     */
    private String activateMenu;

    /**
     * Message on menu to deactivate a cosmetic of this category.
     */
    private String deactivateMenu;

    /**
     * Path of the clear message.
     */
    private String clearConfigPath;

    private String chatPlaceholder;

    private String activateConfig;

    private String deactivateConfig;

    /**
     * Category of Cosmetic.
     *
     * @param configPath       The config path name.
     * @param activateMenu     Message on menu to activate a cosmetic of this category.
     * @param deactivateMenu   Message on menu to deactivate a cosmetic of this category.
     * @param clearConfigPath
     * @param chatPlaceholder
     * @param activateConfig
     * @param deactivateConfig
     */
    Category(String configPath, String activateMenu, String deactivateMenu, String clearConfigPath, String chatPlaceholder, String activateConfig, String deactivateConfig) {
        this.configPath = configPath;
        this.activateMenu = activateMenu;
        this.deactivateMenu = deactivateMenu;
        this.clearConfigPath = clearConfigPath;
        this.chatPlaceholder = chatPlaceholder;
        this.activateConfig = activateConfig;
        this.deactivateConfig = deactivateConfig;
        this.slot = UltraCosmeticsData.get().getPlugin().getConfig().getInt("Categories." + configPath + ".Main-Menu-Item-Slot");

        if (SettingsManager.getConfig().contains("Categories." + configPath + ".Main-Menu-Item")) {
            this.is = ItemFactory.getItemStackFromConfig("Categories." + configPath + ".Main-Menu-Item");
        } else {
            this.is = ItemFactory.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA" +
                    "6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTA1OWQ1OWViNGU1OWM" +
                    "zMWVlY2Y5ZWNlMmY5Y2YzOTM0ZTQ1YzBlYzQ3NmZjODZiZmFlZjhlYTkxM2VhNzE" +
                    "wIn19fQ==", ChatColor.DARK_GRAY + "" + ChatColor.ITALIC);
        }
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setDisplayName(MessageManager.getMessage("Menu." + configPath));
        is.setItemMeta(itemMeta);
    }

    /**
     * Gets the ItemStack in Main Menu.
     *
     * @return The ItemStack in Main Menu.
     */
    public ItemStack getItemStack() {
        return is;
    }

    public Integer getSlot() {
        return slot;
    }

    /**
     * Checks if the category is enabled.
     *
     * @return {@code true} if enabled, otherwise {@code false}.
     */
    public boolean isEnabled() {
        return !(this == MORPHS && !Bukkit.getPluginManager().isPluginEnabled("LibsDisguises"))
                && SettingsManager.getConfig().getBoolean("Categories-Enabled." + configPath);
    }

    /**
     * Checks if the category should have a back arrow in its menu.
     *
     * @return {@code true} if has arrow, otherwise {@code false}
     */
    public boolean hasGoBackArrow() {
        return !(!UltraCosmeticsData.get().areTreasureChestsEnabled()
                && enabledSize() == 1)
                && (boolean) (SettingsManager.getConfig().get("Categories." + configPath + ".Go-Back-Arrow"));
    }

    /**
     * @return Config Path.
     */
    public String getConfigPath() {
        return configPath;
    }

    public String getActivateMenu() {
        return MessageManager.getMessage("Menu." + activateMenu);
    }

    public String getClearConfigPath() {
        return clearConfigPath;
    }

    public String getDeactivateMenu() {
        return MessageManager.getMessage("Menu." + deactivateMenu);
    }

    public String getChatPlaceholder() {
        return chatPlaceholder;
    }

    public String getActivateConfig() {
        return activateConfig;
    }

    public String getDeactivateConfig() {
        return deactivateConfig;
    }

    public abstract CosmeticMenu getMenu(UltraCosmetics ultraCosmetics);

    public abstract List<? extends CosmeticType> getEnabled();
}
