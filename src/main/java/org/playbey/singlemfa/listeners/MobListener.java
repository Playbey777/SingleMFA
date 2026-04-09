package org.playbey.singlemfa.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.playbey.singlemfa.SingleMFA;

public class MobListener implements Listener {

    private final SingleMFA plugin;
    private final NamespacedKey spawnerKey;

    public MobListener(SingleMFA plugin) {
        this.plugin = plugin;
        this.spawnerKey = new NamespacedKey(plugin, "spawner_mob");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (!plugin.getConfigManager().getConfig().getBoolean("modules.mobs.anti-abuse-spawner", true)) {
            return;
        }
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            event.getEntity().getPersistentDataContainer().set(spawnerKey, PersistentDataType.BYTE, (byte) 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (killer == null) return;

        if (plugin.getConfigManager().getConfig().getBoolean("modules.mobs.anti-abuse-spawner", true)) {
            if (entity.getPersistentDataContainer().has(spawnerKey, PersistentDataType.BYTE)) {
                return;
            }
        }

        String source = entity.getType().name();
        plugin.getRewardManager().processReward(killer, source, "mobs");
    }
}