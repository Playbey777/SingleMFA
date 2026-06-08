package org.playbey.singlemfa.managers;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.playbey.singlemfa.SingleMFA;
import org.playbey.singlemfa.api.ExternalMultiplier;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RewardManager {

    private final SingleMFA plugin;

    public RewardManager(SingleMFA plugin) {
        this.plugin = plugin;
    }

    public void processReward(Player player, String source, String type) {
        processReward(player, source, type, false);
    }

    public double processReward(Player player, String source, String type, boolean silent) {
        ConfigManager cm = plugin.getConfigManager();
        if (!cm.getConfig().getBoolean("modules." + type + ".enabled", true)) {
            return 0.0;
        }

        String path = "modules." + type + ".list." + source;
        String rangeString;
        String displayName = source;

        if (cm.getConfig().isConfigurationSection(path)) {
            rangeString = cm.getConfig().getString(path + ".reward");
            displayName = cm.getConfig().getString(path + ".name", source);
        } else {
            rangeString = cm.getConfig().getString(path);
        }

        if (rangeString == null || rangeString.isEmpty()) {
            return 0.0;
        }

        double baseAmount = parseAmount(rangeString);
        if (baseAmount <= 0) return 0.0;

        double finalAmount = applyMultipliers(player, source, baseAmount, type);
        finalAmount = Math.round(finalAmount * 100.0) / 100.0;

        if (finalAmount > 0) {
            plugin.getEconomy().depositPlayer(player, finalAmount);
            if (!silent) {
                sendMessages(player, displayName, finalAmount, type);
            }
        }

        return finalAmount;
    }

    private double applyMultipliers(Player player, String source, double baseAmount, String type) {
        ConfigManager cm = plugin.getConfigManager();
        List<String> excluded = cm.getConfig().getStringList("multipliers.excluded-sources");

        if (excluded.contains(source)) {
            return baseAmount;
        }

        double maxPermMultiplier = 1.0;
        ConfigurationSection perms = cm.getConfig().getConfigurationSection("multipliers.permissions");

        if (perms != null) {
            for (String perm : perms.getKeys(false)) {
                if (player.hasPermission(perm)) {
                    maxPermMultiplier = Math.max(maxPermMultiplier, perms.getDouble(perm));
                }
            }
        }

        double externalMultiplierSum = 0.0;
        for (ExternalMultiplier ext : plugin.getApi().getMultipliers()) {
            externalMultiplierSum += ext.getMultiplier(player, source, type);
        }

        double finalMultiplier = maxPermMultiplier + externalMultiplierSum;
        return baseAmount * Math.max(1.0, finalMultiplier);
    }

    private double parseAmount(String str) {
        try {
            if (str.contains("-")) {
                String[] parts = str.split("-");
                double min = Double.parseDouble(parts[0]);
                double max = Double.parseDouble(parts[1]);
                return min + (max - min) * ThreadLocalRandom.current().nextDouble();
            } else {
                return Double.parseDouble(str);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return 0.0;
        }
    }

    private void sendMessages(Player player, String source, double amount, String type) {
        ConfigManager cm = plugin.getConfigManager();
        boolean sendChat = cm.getConfig().getBoolean("modules." + type + ".chat-message", true);
        boolean sendActionBar = cm.getConfig().getBoolean("modules." + type + ".actionbar-message", true);
        String amountStr = String.valueOf(amount);

        if (sendChat) {
            String msgKey = type.equals("mobs") ? "mob-kill-chat" : "block-break-chat";
            String msg = cm.getMessage(msgKey)
                    .replace("%amount%", amountStr)
                    .replace("%source%", source);
            if (!msg.isEmpty()) player.sendMessage(msg);
        }

        if (sendActionBar) {
            String msgKey = type.equals("mobs") ? "mob-kill-actionbar" : "block-break-actionbar";
            String msg = cm.getRawMessage(msgKey)
                    .replace("%amount%", amountStr)
                    .replace("%source%", source);
            if (!msg.isEmpty()) player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
        }
    }
}