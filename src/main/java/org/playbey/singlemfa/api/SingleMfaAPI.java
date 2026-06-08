package org.playbey.singlemfa.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.playbey.singlemfa.SingleMFA;
import org.playbey.singlemfa.listeners.BlockListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SingleMfaAPI {

    private final List<ExternalMultiplier> multipliers = new CopyOnWriteArrayList<>();

    public void registerMultiplier(ExternalMultiplier multiplier) {
        if (!multipliers.contains(multiplier)) {
            multipliers.add(multiplier);
        }
    }

    public void unregisterMultiplier(ExternalMultiplier multiplier) {
        multipliers.remove(multiplier);
    }

    public List<ExternalMultiplier> getMultipliers() {
        return multipliers;
    }

    public double processExternalBlockBreak(Player player, Block block, boolean silent) {
        SingleMFA plugin = SingleMFA.getInstance();

        if (plugin.getConfigManager().getConfig().getBoolean("modules.blocks.anti-abuse-placed", true)) {
            if (block.hasMetadata(BlockListener.META_KEY)) {
                return 0.0;
            }
        }

        return plugin.getRewardManager().processReward(player, block.getType().name(), "blocks", silent);
    }
}