package io.github.mikip98.savethehotbar.config;

import io.github.mikip98.savethehotbar.config.enums.ContainDropMode;
import io.github.mikip98.savethehotbar.config.enums.ExperienceMode;
import io.github.mikip98.savethehotbar.config.enums.ExperienceCalculation;

public abstract class DefaultConfig {
    public static final boolean dEnabled = true;

    public static final boolean
            dSaveHotbar = true,
            dSaveArmor = true,
            dSaveSecondHand = true;

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
