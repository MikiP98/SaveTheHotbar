package io.github.mikip98.savethehotbar.config;

import io.github.mikip98.savethehotbar.config.enums.ContainDropMode;
import io.github.mikip98.savethehotbar.config.enums.ExperienceMode;
import io.github.mikip98.savethehotbar.config.enums.ExperienceCalculation;

public class ModConfig extends DefaultConfig {
    public static boolean enabled = dEnabled;

    public static boolean
            saveHotbar = dSaveHotbar,
            saveArmor = dSaveArmor,
            saveSecondHand = dSaveSecondHand;

    public static ExperienceMode experienceBehaviour = dExperienceBehaviour;
    public static ExperienceCalculation experienceCalculationMode = dExperienceCalculationMode;
    public static float experienceFraction = dExperienceFraction;

    public static boolean
            randomSpread = dRandomSpread,  // Does not do anything if containDrop is true
            containDrop = dContainDrop,
            logDeathCoordinatesInChat = dLogDeathCoordinatesInChat,
            logGraveCoordinatesInChat = dLogGraveCoordinatesInChat;

    public static float
            randomDropChance = dRandomDropChance,
            rarityDropChanceDecrease = dRarityDropChanceDecrease,
            luckDropChanceDecrease = dLuckDropChanceDecrease;

    public static ContainDropMode containDropMode = dContainDropMode;  // Does not do anything if containDrop is false

    public static int sackMaxSpawnRadius = dSackMaxSpawnRadius;
    public static int mobGraveMaxSpawnRadius = dMobGraveMaxSpawnRadius;
    public static boolean allowGravesToSpawnOnSlabs = dAllowGravesToSpawnOnSlabs;

    // ------------ MOD SUPPORT ------------
    public static boolean
            saveArsenal = dSaveArsenal;
}
