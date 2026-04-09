package org.playbey.singlemfa.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.playbey.singlemfa.SingleMFA;
import org.playbey.singlemfa.utils.ColorUtils;
import java.io.File;

public class ConfigManager {

    private final SingleMFA plugin;
    private FileConfiguration config;
    private FileConfiguration messages;

    public ConfigManager(SingleMFA plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();

        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getMessage(String key) {
        if (messages == null) return "";
        String prefix = messages.getString("prefix", "");
        String message = messages.getString(key, "");
        if (message.isEmpty()) return "";
        return ColorUtils.color(prefix + message);
    }

    public String getRawMessage(String key) {
        if (messages == null) return "";
        return ColorUtils.color(messages.getString(key, ""));
    }
}