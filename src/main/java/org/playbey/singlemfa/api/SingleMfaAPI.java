package org.playbey.singlemfa.api;

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
}