package io.github.mikip98.savethehotbar.config;

import io.github.mikip98.savethehotbar.config.enums.*;
import io.github.mikip98.savethehotbar.config.enums.itemTypes.VanillaItemTypes;
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;

@Setter
public class ModConfig {
    public static ModConfig INSTANCE = new ModConfig();
    public static final ModConfig DEFAULT = new ModConfig();

    public boolean enable = true;

    // TODO: Presets?

    public LogicOperator itemKeepingLogicOperator = LogicOperator.AND;

    public boolean
            saveHotbar = true,
            saveArmor = true,
            saveSecondHand = true,
            saveMainInventory = false;

    public OverlapResolution overlapResolution = OverlapResolution.LENIENT;
    public Map<VanillaItemTypes, Boolean> vanillaItemTypesKeepingMap = getVanillaItemTypesKeepingMap();

    protected static Map<VanillaItemTypes, Boolean> getVanillaItemTypesKeepingMap() {
        Map<VanillaItemTypes, Boolean> vanillaItemTypesKeepingMap = new EnumMap<>(VanillaItemTypes.class);
        for (VanillaItemTypes type : VanillaItemTypes.values()) {
            vanillaItemTypesKeepingMap.put(type, true);
        }
        return vanillaItemTypesKeepingMap;
    }

    public ExperienceMode experienceBehaviour = ExperienceMode.DROP;
    public ExperienceCalculation experienceCalculationMode = ExperienceCalculation.FRACTION;
    public float experienceFraction = 0.2f;

    public boolean
            randomSpread = false,  // Does not do anything if containDrop is true
            containDrop = false,
            logDeathCoordinatesInChat = false,
            logGraveCoordinatesInChat = false;

    public float
            randomDropChance = .0f,
            rarityDropChanceDecrease = 0.2f,
            luckDropChanceDecrease = 0.2f;

    public ContainDropMode containDropMode = ContainDropMode.SACK;  // Does not do anything if containDrop is false

    public int sackMaxSpawnRadius = 1;
    public int mobGraveMaxSpawnRadius = 32;
    public boolean allowGravesToSpawnOnSlabs = false;

    // ------------ MOD SUPPORT ------------
    public boolean saveArsenal = true;
}
