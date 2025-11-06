package io.github.mikip98.savethehotbar.config;

import io.github.mikip98.savethehotbar.config.enums.ContainDropMode;
import io.github.mikip98.savethehotbar.config.enums.ExperienceMode;
import io.github.mikip98.savethehotbar.config.enums.ExperienceCalculation;
import io.github.mikip98.savethehotbar.config.enums.LogicOperator;
import io.github.mikip98.savethehotbar.config.enums.itemTypes.VanillaItemTypes;

import java.util.EnumMap;
import java.util.Map;

public abstract class DefaultConfig {
    public static final boolean dEnable = true;

    public static final LogicOperator dItemKeepingLogicOperator = LogicOperator.AND;

    public static final boolean
            dSaveHotbar = true,
            dSaveArmor = true,
            dSaveSecondHand = true,
            dSaveMainInventory = false;

    public static Map<VanillaItemTypes, Boolean> dVanillaItemTypesKeepingMap = new EnumMap<>(VanillaItemTypes.class);
    static {
        for (VanillaItemTypes type : VanillaItemTypes.values()) {
            dVanillaItemTypesKeepingMap.put(type, true);
        }
    }

    public static final ExperienceMode dExperienceBehaviour = ExperienceMode.DROP;
    public static final ExperienceCalculation dExperienceCalculationMode = ExperienceCalculation.FRACTION;
    public static final float dExperienceFraction = 0.2f;

    public static final boolean
            dRandomSpread = false,  // Does not do anything if containDrop is true
            dContainDrop = false,
            dLogDeathCoordinatesInChat = false,
            dLogGraveCoordinatesInChat = false;

    public static final float
            dRandomDropChance = .0f,
            dRarityDropChanceDecrease = 0.2f,
            dLuckDropChanceDecrease = 0.2f;

    public static final ContainDropMode dContainDropMode = ContainDropMode.SACK;

    public static final int dSackMaxSpawnRadius = 1;
    public static final int dMobGraveMaxSpawnRadius = 32;
    public static boolean dAllowGravesToSpawnOnSlabs = false;

    public static final boolean dSaveArsenal = true;
}
