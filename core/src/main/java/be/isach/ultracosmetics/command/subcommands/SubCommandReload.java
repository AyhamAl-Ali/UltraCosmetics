package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.menu.Menus;
import be.isach.ultracosmetics.menu.menus.MenuGadgets;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Menu {@link SubCommand SubCommand}.
 *
 * @author iSach
 * @since 12-21-2015
 */
public class SubCommandReload extends SubCommand {

    public SubCommandReload(UltraCosmetics ultraCosmetics) {
        super("Reload config", "ultracosmetics.command.reload", "/uc reload", ultraCosmetics, "reload");
        //this.menuGadgets = new MenuGadgets(getUltraCosmetics());
    }

    private MenuGadgets menuGadgets;

    @Override
    protected void onExePlayer(Player sender, String... args) {
        getUltraCosmetics().reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Config files reloaded.");
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        getUltraCosmetics().reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Config files reloaded.");
    }

}

