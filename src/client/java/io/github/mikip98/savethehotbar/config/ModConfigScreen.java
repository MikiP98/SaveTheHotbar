package io.github.mikip98.savethehotbar.config;

import io.github.mikip98.savethehotbar.config.enums.ContainDropMode;
import io.github.mikip98.savethehotbar.config.enums.ExperienceCalculation;
import io.github.mikip98.savethehotbar.config.enums.ExperienceMode;
import io.github.mikip98.savethehotbar.config.enums.LogicOperator;
import io.github.mikip98.savethehotbar.config.enums.itemTypes.VanillaItemTypes;
import io.github.mikip98.savethehotbar.config.io.ConfigSaver;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.*;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ModConfigScreen {
    public static Screen createScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setSavingRunnable(ConfigSaver::saveConfigToFile)
                .setParentScreen(parentScreen)
                .setTitle(getTranslationKey("screen.main"));

        // Create a root category
        ConfigCategory rootCategory = builder.getOrCreateCategory(Text.literal("General Settings"));

        // --- Main toggle ---
        rootCategory.addEntry(getBooleanEntry(
                "enable",
                ModConfig.enable, ModConfig.dEnable,
                value -> ModConfig.enable = value
        ));
        rootCategory.addEntry(getEnumEntry(
                "item_keeping_logic_operator",
                LogicOperator.class,
                ModConfig.itemKeepingLogicOperator, ModConfig.dItemKeepingLogicOperator,
                value -> ModConfig.itemKeepingLogicOperator = value
        ));

        rootCategory.addEntry(getSlotControlCategory());
        rootCategory.addEntry(getItemTypeControlCategory());
        rootCategory.addEntry(getExpControlCategory());
        rootCategory.addEntry(getRandomDropControlCategory());
        rootCategory.addEntry(getDropControlCategory());

        rootCategory.addEntry(getBooleanEntry(
                "log_death_coordinates_in_chat",
                ModConfig.logDeathCoordinatesInChat, ModConfig.dLogDeathCoordinatesInChat,
                value -> ModConfig.logDeathCoordinatesInChat = value
        ));
        rootCategory.addEntry(getBooleanEntry(
                "log_grave_coordinates_in_chat",
                ModConfig.logGraveCoordinatesInChat, ModConfig.dLogGraveCoordinatesInChat,
                value -> ModConfig.logGraveCoordinatesInChat = value
        ));

        return builder.build();
    }


    // --- Item Slots ---
    protected static SubCategoryListEntry getSlotControlCategory() {
        SubCategoryBuilder slotControlCategory = getSubCategory("slot_control");

        // Vanilla
        slotControlCategory.add(getBooleanEntry(
                "save_hotbar",
                ModConfig.saveHotbar, ModConfig.dSaveHotbar,
                value -> ModConfig.saveHotbar = value
        ));
        slotControlCategory.add(getBooleanEntry(
                "save_armour",
                ModConfig.saveArmor, ModConfig.dSaveArmor,
                value -> ModConfig.saveArmor = value
        ));
        slotControlCategory.add(getBooleanEntry(
                "save_second_hand",
                ModConfig.saveSecondHand, ModConfig.dSaveSecondHand,
                value -> ModConfig.saveSecondHand = value
        ));
        slotControlCategory.add(getBooleanEntry(
                "save_main_inventory",
                ModConfig.saveMainInventory, ModConfig.dSaveMainInventory,
                value -> ModConfig.saveMainInventory = value
        ));

        // Modded
        SubCategoryBuilder moddedSlotsSettings = getSubCategory("modded_slots_settings");
        moddedSlotsSettings.add(getBooleanEntry(
                "save_arsenal_back_slot",
                ModConfig.saveArsenal, ModConfig.dSaveArsenal,
                value -> ModConfig.saveArsenal = value
        ));
        slotControlCategory.add(moddedSlotsSettings.build());

        return slotControlCategory.build();
    }


    // --- Item Types ---
    protected static SubCategoryListEntry getItemTypeControlCategory() {
        SubCategoryBuilder itemTypeControlCategory = getSubCategory("item_type_control");

        for (VanillaItemTypes type : VanillaItemTypes.values()) {
            itemTypeControlCategory.add(getBooleanEntry(
                    "keep_" + type.name().toLowerCase(),
                    ModConfig.vanillaItemTypesKeepingMap.get(type), ModConfig.dVanillaItemTypesKeepingMap.get(type),
                    value -> ModConfig.vanillaItemTypesKeepingMap.put(type, value)
            ));
        }

        return itemTypeControlCategory.build();
    }


    // --- EXP ---
    protected static SubCategoryListEntry getExpControlCategory() {
        SubCategoryBuilder expControlCategory = getSubCategory("exp_control");

        expControlCategory.add(getEnumEntry(
                "experience_behaviour", ExperienceMode.class,
                ModConfig.experienceBehaviour, ModConfig.dExperienceBehaviour,
                value -> ModConfig.experienceBehaviour = value
        ));
        expControlCategory.add(getEnumEntry(
                "experience_calculation_mode", ExperienceCalculation.class,
                ModConfig.experienceCalculationMode, ModConfig.dExperienceCalculationMode,
                value -> ModConfig.experienceCalculationMode = value
        ));
        expControlCategory.add(getFloatEntry(
                "experience_fraction",
                ModConfig.experienceFraction, ModConfig.dExperienceFraction,
                value -> ModConfig.experienceFraction = value
        ));

        return expControlCategory.build();
    }


    protected static SubCategoryListEntry getRandomDropControlCategory() {
        SubCategoryBuilder randomDropControlCategory = getSubCategory("random_drop_control");

        randomDropControlCategory.add(getFloatEntry(
                "random_drop_chance",
                ModConfig.randomDropChance, ModConfig.dRandomDropChance,
                value -> ModConfig.randomDropChance = value
        ));
        randomDropControlCategory.add(getFloatEntry(
                "rarity_drop_chance_decrease",
                ModConfig.rarityDropChanceDecrease, ModConfig.dRarityDropChanceDecrease,
                value -> ModConfig.rarityDropChanceDecrease = value
        ));
        randomDropControlCategory.add(getFloatEntry(
                "luck_drop_chance_decrease",
                ModConfig.luckDropChanceDecrease, ModConfig.dLuckDropChanceDecrease,
                value -> ModConfig.luckDropChanceDecrease = value
        ));

        return randomDropControlCategory.build();
    }


    protected static SubCategoryListEntry getDropControlCategory() {
        SubCategoryBuilder dropControlCategory = getSubCategory("drop_control");

        dropControlCategory.add(getBooleanEntry(
                "random_item_spread",
                ModConfig.randomSpread, ModConfig.dRandomSpread,
                value -> ModConfig.randomSpread = value
        ));
        dropControlCategory.add(getBooleanEntry(
                "contain_drop",
                ModConfig.containDrop, ModConfig.dContainDrop,
                value -> ModConfig.containDrop = value
        ));
        dropControlCategory.add(getEnumEntry(
                "contain_drop_mode", ContainDropMode.class,
                ModConfig.containDropMode, ModConfig.dContainDropMode,
                value -> ModConfig.containDropMode = value
        ));

        SubCategoryBuilder graveSpawningLogic = getSubCategory("grave_spawning_logic");
        graveSpawningLogic.add(getIntegerEntry(
                "sack_max_spawn_radius",
                ModConfig.sackMaxSpawnRadius, ModConfig.dSackMaxSpawnRadius,
                value -> ModConfig.sackMaxSpawnRadius = value
        ));
        graveSpawningLogic.add(getIntegerEntry(
                "mob_head_grave_max_spawn_radius",
                ModConfig.mobGraveMaxSpawnRadius, ModConfig.dMobGraveMaxSpawnRadius,
                value -> ModConfig.mobGraveMaxSpawnRadius = value
        ));
        graveSpawningLogic.add(getBooleanEntry(
                "allow_mob_heads_graves_to_spawn_on_slabs",
                ModConfig.allowGravesToSpawnOnSlabs, ModConfig.dAllowGravesToSpawnOnSlabs,
                value -> ModConfig.allowGravesToSpawnOnSlabs = value
        ));
        dropControlCategory.add(graveSpawningLogic.build());

        return dropControlCategory.build();
    }


    // --- Config Entry Util ---
    protected static BooleanListEntry getBooleanEntry(
            String name, boolean currentValue, boolean defaultValue, Consumer<Boolean> setter
    ) {
        return ConfigEntryBuilder.create()
                .startBooleanToggle(getSettingTranslationKey(name), currentValue)
                .setTooltip(getSettingTooltipTranslationKey(name))
                .setDefaultValue(defaultValue)
                .setSaveConsumer(setter)
                .build();
    }
    protected static <T extends Enum<?>> EnumListEntry<T> getEnumEntry(
            String name, Class<T> enumClass, T currentValue, T defaultValue, Consumer<T> setter
    ) {
        return ConfigEntryBuilder.create()
                .startEnumSelector(getSettingTranslationKey(name), enumClass, currentValue)
                .setTooltip(getSettingTooltipTranslationKey(name))
                .setDefaultValue(defaultValue)
                .setSaveConsumer(setter)
                .build();
    }
    protected static FloatListEntry getFloatEntry(
            String name, float currentValue, float defaultValue, Consumer<Float> setter
    ) {
        return ConfigEntryBuilder.create()
                .startFloatField(getSettingTranslationKey(name), currentValue)
                .setTooltip(getSettingTooltipTranslationKey(name))
                .setDefaultValue(defaultValue)
                .setMax(1.0f)
                .setMin(0.0f)
                .setSaveConsumer(setter)
                .build();
    }
    protected static IntegerListEntry getIntegerEntry(
            String name, int currentValue, int defaultValue, Consumer<Integer> setter
    ) {
        return ConfigEntryBuilder.create()
                .startIntField(getSettingTranslationKey(name), currentValue)
                .setTooltip(getSettingTooltipTranslationKey(name))
                .setDefaultValue(defaultValue)
                .setMin(0)
                .setSaveConsumer(setter)
                .build();
    }
    protected static SubCategoryBuilder getSubCategory(@NotNull String name) {
        return ConfigEntryBuilder.create()
                .startSubCategory(getCategoryTranslationKey(name))
                .setTooltip(getCategoryTooltipTranslationKey(name));
    }


    // --- Generic Util ---
    protected static Text getCategoryTooltipTranslationKey(@NotNull String name) {
        return getCategoryTranslationKey(name + ".tooltip");
    }
    protected static Text getCategoryTranslationKey(@NotNull String name) {
        return getTranslationKey("category." + name);
    }
    protected static Text getSettingTooltipTranslationKey(@NotNull String name) {
        return getSettingTranslationKey(name + ".tooltip");
    }
    protected static Text getSettingTranslationKey(@NotNull String name) {
        return getTranslationKey("setting." + name);
    }
    protected static Text getTranslationKey(@NotNull String name) {
        return Text.translatable("config.savethehotbar." + name);
    }
}
