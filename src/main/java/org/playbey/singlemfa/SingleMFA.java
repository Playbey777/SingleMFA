package org.playbey.singlemfa;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.playbey.singlemfa.api.SingleMfaAPI;
import org.playbey.singlemfa.commands.MfaCommand;
import org.playbey.singlemfa.listeners.BlockListener;
import org.playbey.singlemfa.listeners.MobListener;
import org.playbey.singlemfa.managers.ConfigManager;
import org.playbey.singlemfa.managers.RewardManager;

public final class SingleMFA extends JavaPlugin {

    private static SingleMFA instance;
    private Economy econ = null;
    private ConfigManager configManager;
    private RewardManager rewardManager;
    private SingleMfaAPI api;

    @Override
    public void onEnable() {
        instance = this;

        if (!setupEconomy()) {
            getLogger().severe("Vault is missing or no economy plugin found! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        configManager = new ConfigManager(this);
        configManager.loadConfigs();

        api = new SingleMfaAPI();
        rewardManager = new RewardManager(this);

        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new MobListener(this), this);

        MfaCommand cmd = new MfaCommand(this);
        getCommand("mfa").setExecutor(cmd);
        getCommand("mfa").setTabCompleter(cmd);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static SingleMFA getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return econ;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public SingleMfaAPI getApi() {
        return api;
    }
}