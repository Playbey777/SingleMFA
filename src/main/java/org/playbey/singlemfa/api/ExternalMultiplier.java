package org.playbey.singlemfa.api;

import org.bukkit.entity.Player;

public interface ExternalMultiplier {
    double getMultiplier(Player player, String sourceName, String actionType);
}