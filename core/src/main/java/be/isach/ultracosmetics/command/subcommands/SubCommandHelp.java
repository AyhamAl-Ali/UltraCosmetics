package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.CommandManager;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.menu.menus.MenuGadgets;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Menu {@link SubCommand SubCommand}.
 *
 * @author iSach
 * @since 12-21-2015
 */
public class SubCommandHelp extends SubCommand {

    public SubCommandHelp(UltraCosmetics ultraCosmetics) {
        super("Shows help message", "ultracosmetics.command.help", "/uc help", ultraCosmetics, "help");
        //this.menuGadgets = new MenuGadgets(getUltraCosmetics());
    }

    @Override
    protected void onExePlayer(Player sender, String... args) {
        getUltraCosmetics().getCommandManager().showHelp(sender, 1);
    }

    @Override
    protected void onExeConsole(ConsoleCommandSender sender, String... args) {
        getUltraCosmetics().getCommandManager().showHelp(sender, 1);
    }

}

