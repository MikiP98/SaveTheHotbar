package io.github.mikip98.savethehotbar.config;

import io.github.mikip98.savethehotbar.enums.ContainDropMode;

public class DefaultConfig {
    public static final boolean
            dSaveHotbar = true,
            dSaveArmor = true,
            dSaveSecondHand = true,
            dKeepExperience = false,
            dRandomSpread = false,  // Does not do anything if containDrop is true
            dContainDrop = false,
            dLogDeathCoordinatesInChat = false,
            dLogGraveCoordinatesInChat = false;

    public static final float
            dRandomDropChance = .0f,
            dRarityDropChanceDecrease = 2.0f;

    public static final ContainDropMode dContainDropMode = ContainDropMode.SACK;

    public static final int dMobGraveMaxSpawnRadius = 32;
}
