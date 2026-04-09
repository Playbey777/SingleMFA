package org.playbey.singlemfa.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.playbey.singlemfa.SingleMFA;

public class BlockListener implements Listener {

    private final SingleMFA plugin;
    private static final String META_KEY = "singlemfa_placed";

    public BlockListener(SingleMFA plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!plugin.getConfigManager().getConfig().getBoolean("modules.blocks.anti-abuse-placed", true)) {
            return;
        }
        event.getBlock().setMetadata(META_KEY, new FixedMetadataValue(plugin, true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (plugin.getConfigManager().getConfig().getBoolean("modules.blocks.anti-abuse-placed", true)) {
            if (block.hasMetadata(META_KEY)) {
                block.removeMetadata(META_KEY, plugin);
                return;
            }
        }

        String source = block.getType().name();
        plugin.getRewardManager().processReward(player, source, "blocks");
    }
}