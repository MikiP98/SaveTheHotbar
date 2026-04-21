package io.github.mikip98.savethehotbar.config;

import io.github.mikip98.savethehotbar.config.enums.*;
import io.github.mikip98.savethehotbar.config.enums.itemTypes.VanillaItemTypes;

import java.util.EnumMap;
import java.util.Map;

public class ModConfig extends DefaultConfig {
    public static boolean enable = dEnable;

    // TODO: Presets?

    public static LogicOperator itemKeepingLogicOperator = dItemKeepingLogicOperator;

    public static boolean
            saveHotbar = dSaveHotbar,
            saveArmor = dSaveArmor,
            saveSecondHand = dSaveSecondHand,
            saveMainInventory = dSaveMainInventory;

    public static OverlapResolution overlapResolution = dOverlapResolution;
    public static Map<VanillaItemTypes, Boolean> vanillaItemTypesKeepingMap = new EnumMap<>(dVanillaItemTypesKeepingMap);

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
