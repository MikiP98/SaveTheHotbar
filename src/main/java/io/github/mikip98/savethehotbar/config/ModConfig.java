package io.github.mikip98.savethehotbar.config;

import io.github.mikip98.savethehotbar.enums.ContainDropMode;

public abstract class ModConfig extends DefaultConfig {
    public static boolean
            saveHotbar = dSaveHotbar,
            saveArmor = dSaveArmor,
            saveSecondHand = dSaveSecondHand,
            randomSpread = dRandomSpread,  // Does not do anything if containDrop is true
            containDrop = dContainDrop;

    public static float
            randomDropChance = dRandomDropChance,
            rarityDropChanceDecrease = dRarityDropChanceDecrease;

    public static ContainDropMode containDropMode = dContainDropMode;
}
