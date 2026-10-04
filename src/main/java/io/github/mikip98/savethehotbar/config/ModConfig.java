package io.github.mikip98.savethehotbar.config;

import io.github.mikip98.savethehotbar.config.annotations.*;
import io.github.mikip98.savethehotbar.config.enums.*;
import io.github.mikip98.savethehotbar.config.enums.ItemTypes;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.EnumMap;
import java.util.Map;

@Config(name = "saveTheHotbar")
@ConfigExtension.Gui.GlobalTooltip
public class ModConfig implements ConfigData {
    // Do not remove 'transient' keyword, else AutoConfig will serialize it and enter infinite recursive loop (#199)
    @ConfigEntry.Gui.Excluded
    public static transient ModConfig INSTANCE;

    public boolean enable = true;

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public LogicOperator itemKeepingLogicOperator = LogicOperator.AND;

    // TODO: Presets?

    // --- Slot Control ---
    @ConfigEntry.Gui.CollapsibleObject
    public SlotControl slotControl = new SlotControl();

    public static class SlotControl {
        public boolean saveHotbar = true;
        public boolean saveArmor = true;
        public boolean saveSecondHand = true;
        public boolean saveMainInventory = false;

        @ConfigEntry.Gui.CollapsibleObject
        public ModdedSlotsSettings moddedSlotsSettings = new ModdedSlotsSettings();

        // --- MOD SUPPORT ---
        public static class ModdedSlotsSettings {
            public boolean saveArsenal = true;
        }
    }

    // --- Item Type Control ---
    @ConfigEntry.Gui.CollapsibleObject
    public ItemTypeControl itemTypeControl = new ItemTypeControl();

    public static class ItemTypeControl {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public OverlapResolution overlapResolution = OverlapResolution.LENIENT;
        @ConfigExtension.Gui.SkipGlobalTooltip
        public Map<ItemTypes, Boolean> itemTypesKeepingMap = getItemTypesKeepingMap();

        protected static Map<ItemTypes, Boolean> getItemTypesKeepingMap() {
            Map<ItemTypes, Boolean> map = new EnumMap<>(ItemTypes.class);
            for (ItemTypes type : ItemTypes.values()) {
                map.put(type, true);
            }
            return map;
        }
    }

    // --- EXP ---
    @ConfigEntry.Gui.CollapsibleObject
    public ExpControl expControl = new ExpControl();

    public static class ExpControl {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ExperienceMode experienceBehaviour = ExperienceMode.DROP;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ExperienceCalculation experienceCalculationMode = ExperienceCalculation.FRACTION;
        @ConfigExtension.Gui.FloatRange(min = 0.0f, max = 1.0f)
        public float experienceFraction = 0.2f;
    }

    // --- HUNGER & SATURATION ---
    @ConfigEntry.Gui.CollapsibleObject
    public HungerControl hungerControl = new HungerControl();

    public static class HungerControl {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public FoodLevelCalculationMode foodLevelCalculationMode = FoodLevelCalculationMode.VANILLA;
        @ConfigExtension.Gui.IntRange(min = 0) public int minFoodLevel = 6;
        @ConfigExtension.Gui.IntRange(min = 0) public int maxFoodLevel = 12;
        @ConfigExtension.Gui.FloatRange(min = 0.0f) public float foodLevelFraction = 0.5f;

        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public SaturationLevelCalculationMode saturationLevelCalculationMode = SaturationLevelCalculationMode.VANILLA;
        @ConfigExtension.Gui.FloatRange(min = 0.0f) public float minSaturation = 2.0f;
        @ConfigExtension.Gui.FloatRange(min = 0.0f) public float maxSaturation = 8.0f;
        @ConfigExtension.Gui.FloatRange(min = 0.0f) public float saturationFraction = 0.5f;
    }

    // --- Random Drop Control
    @ConfigEntry.Gui.CollapsibleObject
    public RandomDropControl randomDropControl = new RandomDropControl();

    public static class RandomDropControl {
        @ConfigExtension.Gui.FloatRange(min = 0.0f, max = 1.0f) public float randomDropChance = 0.0f;
        @ConfigExtension.Gui.FloatRange(min = 0.0f, max = 1.0f) public float rarityDropChanceDecrease = 0.2f;
        @ConfigExtension.Gui.FloatRange(min = 0.0f, max = 1.0f) public float luckDropChanceDecrease = 0.2f;
    }

    // --- Drop Control ---
    @ConfigEntry.Gui.CollapsibleObject
    public DropControl dropControl = new DropControl();

    public static class DropControl {
        public boolean randomSpread = false;  // Does not do anything if containDrop is true
        public boolean containDrop = false;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ContainDropMode containDropMode = ContainDropMode.SACK;

        @ConfigEntry.Gui.CollapsibleObject
        public GraveSpawningLogic graveSpawningLogic = new GraveSpawningLogic();

        public static class GraveSpawningLogic {
            @ConfigExtension.Gui.IntRange(min = 0) public int sackMaxSpawnRadius = 1;
            @ConfigExtension.Gui.IntRange(min = 0) public int mobGraveMaxSpawnRadius = 32;
            public boolean allowGravesToSpawnOnSlabs = false;
        }
    }

    public boolean logDeathCoordinatesInChat = false;
    public boolean logGraveCoordinatesInChat = false;
}
