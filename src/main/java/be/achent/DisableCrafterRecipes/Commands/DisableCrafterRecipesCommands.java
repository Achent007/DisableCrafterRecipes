package be.achent.DisableCrafterRecipes.Commands;

import be.achent.DisableCrafterRecipes.DisableCrafterRecipes;
import be.achent.DisableCrafterRecipes.Events.DisableCrafterRecipesEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class DisableCrafterRecipesCommands  implements CommandExecutor {

    private final DisableCrafterRecipes plugin;

    public DisableCrafterRecipesCommands(DisableCrafterRecipes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("disablecrafterrecipes.reload")) {
                sender.sendMessage(plugin.getLanguageMessage("messages.No Permission"));
                return true;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    plugin.reloadConfig();
                    plugin.loadBlockedRecipes();
                    plugin.reloadLanguageConfig();
                    sender.sendMessage(plugin.getLanguageMessage("messages.Reloaded"));

                    HandlerList.unregisterAll((Plugin) plugin);
                    Bukkit.getPluginManager().registerEvents(new DisableCrafterRecipesEvent(plugin.blockedRecipes), plugin);
                } catch (Exception e) {
                    plugin.getLogger().severe("Error reloading configurations: " + e.getMessage());
                    sender.sendMessage(plugin.getLanguageMessage("messages.ReloadError"));
                }
            });

            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(plugin.getLanguageMessage("messages.Incorrect usage"));
            return false;
        }

        if (!sender.hasPermission("disablecrafterrecipes.use")) {
            sender.sendMessage(plugin.getLanguageMessage("messages.No Permission"));
            return true;
        }

        return false;
    }
}