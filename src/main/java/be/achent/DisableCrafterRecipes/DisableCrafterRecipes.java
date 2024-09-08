package be.achent.DisableCrafterRecipes;

import be.achent.DisableCrafterRecipes.Commands.DisableCrafterRecipesCommands;
import be.achent.DisableCrafterRecipes.Commands.DisableCrafterRecipesTabCompleter;
import be.achent.DisableCrafterRecipes.Events.DisableCrafterRecipesEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DisableCrafterRecipes extends JavaPlugin implements Listener {

    private FileConfiguration languageConfig;
    private File languageConfigFile;
    public Set<Material> blockedRecipes;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        loadBlockedRecipes();
        loadLanguageConfig();
        updateConfigFile("config.yml", "config-default.yml");
        updateConfigFile("language.yml", "language-default.yml");

        Bukkit.getPluginManager().registerEvents(new DisableCrafterRecipesEvent(blockedRecipes), this);

        getCommand("disablecrafterrecipes").setExecutor(new DisableCrafterRecipesCommands(this));
        getCommand("disablecrafterrecipes").setTabCompleter(new DisableCrafterRecipesTabCompleter());
    }

    public String getLanguageMessage(String path) {
        String message = this.languageConfig.getString(path);
        if (message != null) {
            return formatMessage(message);
        } else {
            getLogger().warning("Le chemin de message '" + path + "' n'a pas été trouvé dans language.yml");
            return "";
        }
    }

    public void reloadLanguageConfig() {
        if (languageConfigFile == null) {
            languageConfigFile = new File(getDataFolder(), "language.yml");
        }
        if (!languageConfigFile.exists()) {
            saveResource("language.yml", false);
        }
        languageConfig = YamlConfiguration.loadConfiguration(languageConfigFile);

        InputStream defaultStream = getResource("language-default.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            languageConfig.setDefaults(defaultConfig);
        }
    }

    private void loadLanguageConfig() {
        File languageFile = new File(getDataFolder(), "language.yml");
        if (!languageFile.exists()) {
            saveResource("language.yml", false);
        }
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);
    }

    public String formatMessage(String message) {
        if (message == null) {
            return "";
        }
        String prefix = this.languageConfig.getString("messages.prefix");
        assert prefix != null;
        message = message.replace("{prefix}", prefix);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private void updateConfigFile(String fileName, String defaultFileName) {
        File configFile = new File(getDataFolder(), fileName);
        if (!configFile.exists()) {
            saveResource(fileName, false);
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        InputStream defaultConfigStream = getResource(defaultFileName);
        if (defaultConfigStream == null) {
            getLogger().severe("Fichier de configuration par défaut " + defaultFileName + " non trouvé.");
            return;
        }

        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
        for (String key : defaultConfig.getKeys(true)) {
            if (!config.contains(key)) {
                config.set(key, defaultConfig.get(key));
            }
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            getLogger().severe("Erreur lors de la sauvegarde du fichier " + fileName + " : " + e.getMessage());
        }
    }

    public void loadBlockedRecipes() {
        getLogger().info("Loading blocked recipes from config...");
        blockedRecipes = new HashSet<>();

        List<String> recipeStrings = getConfig().getStringList("blocked_recipes");

        for (String item : recipeStrings) {
            try {
                Material material = Material.matchMaterial(item);
                if (material != null) {
                    blockedRecipes.add(material);
                } else {
                    getLogger().warning("Material '" + item + "' is not a valid Material.");
                }
            } catch (Exception e) {
                getLogger().warning("Error loading material: " + item);
            }
        }
    }
}