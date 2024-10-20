package io.github.mikip98.savethehotbar.config;

import io.github.mikip98.savethehotbar.enums.ContainDropMode;

public abstract class ModConfig extends DefaultConfig {
    public static boolean
            saveHotbar = dSaveHotbar,
            saveArmor = dSaveArmor,
            saveSecondHand = dSaveSecondHand,
            keepExperience = dKeepExperience,
            randomSpread = dRandomSpread,  // Does not do anything if containDrop is true
            containDrop = dContainDrop,
            logDeathCoordinatesInChat = dLogDeathCoordinatesInChat,
            logGraveCoordinatesInChat = dLogGraveCoordinatesInChat;

    public static float
            randomDropChance = dRandomDropChance,
            rarityDropChanceDecrease = dRarityDropChanceDecrease;

    public static ContainDropMode containDropMode = dContainDropMode;  // Does not do anything if containDrop is false

    public static int mobGraveMaxSpawnRadius = 32;
}
