package io.github.mikip98.savethehotbar.config;

import io.github.mikip98.savethehotbar.config.enums.*;
import io.github.mikip98.savethehotbar.config.enums.itemTypes.VanillaItemTypes;
import io.github.mikip98.savethehotbar.config.io.ConfigSaver;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.*;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ModConfigScreen {
    public static Screen createScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setSavingRunnable(ConfigSaver::saveConfigToFile)
                .setParentScreen(parentScreen)
                .setTitle(getTranslationKey("screen.main"));

        // Create a root category
        ConfigCategory rootCategory = builder.getOrCreateCategory(Component.literal("General Settings"));

        // --- Main toggle ---
        rootCategory.addEntry(getBooleanEntry(
                "enable",
                ModConfig.INSTANCE.enable, ModConfig.DEFAULT.enable,
                ModConfig.INSTANCE::setEnable
        ));
        rootCategory.addEntry(getEnumEntry(
                "item_keeping_logic_operator",
                LogicOperator.class,
                ModConfig.INSTANCE.itemKeepingLogicOperator, ModConfig.DEFAULT.itemKeepingLogicOperator,
                ModConfig.INSTANCE::setItemKeepingLogicOperator
        ));

        rootCategory.addEntry(getSlotControlCategory());
        rootCategory.addEntry(getItemTypeControlCategory());
        rootCategory.addEntry(getExpControlCategory());
        rootCategory.addEntry(getRandomDropControlCategory());
        rootCategory.addEntry(getDropControlCategory());

        rootCategory.addEntry(getBooleanEntry(
                "log_death_coordinates_in_chat",
                ModConfig.INSTANCE.logDeathCoordinatesInChat, ModConfig.DEFAULT.logDeathCoordinatesInChat,
                ModConfig.INSTANCE::setLogDeathCoordinatesInChat
        ));
        rootCategory.addEntry(getBooleanEntry(
                "log_grave_coordinates_in_chat",
                ModConfig.INSTANCE.logGraveCoordinatesInChat, ModConfig.DEFAULT.logGraveCoordinatesInChat,
                ModConfig.INSTANCE::setLogGraveCoordinatesInChat
        ));

        return builder.build();
    }


    // --- Item Slots ---
    protected static SubCategoryListEntry getSlotControlCategory() {
        SubCategoryBuilder slotControlCategory = getSubCategory("slot_control");

        // Vanilla
        slotControlCategory.add(getBooleanEntry(
                "save_hotbar",
                ModConfig.INSTANCE.saveHotbar, ModConfig.DEFAULT.saveHotbar,
                ModConfig.INSTANCE::setSaveHotbar
        ));
        slotControlCategory.add(getBooleanEntry(
                "save_armour",
                ModConfig.INSTANCE.saveArmor, ModConfig.DEFAULT.saveArmor,
                ModConfig.INSTANCE::setSaveArmor
        ));
        slotControlCategory.add(getBooleanEntry(
                "save_second_hand",
                ModConfig.INSTANCE.saveSecondHand, ModConfig.DEFAULT.saveSecondHand,
                ModConfig.INSTANCE::setSaveSecondHand
        ));
        slotControlCategory.add(getBooleanEntry(
                "save_main_inventory",
                ModConfig.INSTANCE.saveMainInventory, ModConfig.DEFAULT.saveMainInventory,
                ModConfig.INSTANCE::setSaveMainInventory
        ));

        // Modded
        SubCategoryBuilder moddedSlotsSettings = getSubCategory("modded_slots_settings");
        moddedSlotsSettings.add(getBooleanEntry(
                "save_arsenal_back_slot",
                ModConfig.INSTANCE.saveArsenal, ModConfig.DEFAULT.saveArsenal,
                ModConfig.INSTANCE::setSaveArsenal
        ));
        slotControlCategory.add(moddedSlotsSettings.build());

        return slotControlCategory.build();
    }


    // --- Item Types ---
    protected static SubCategoryListEntry getItemTypeControlCategory() {
        SubCategoryBuilder itemTypeControlCategory = getSubCategory("item_type_control");

        itemTypeControlCategory.add(getEnumEntry(
                "item_category_overlap_resolution", OverlapResolution.class,
                ModConfig.INSTANCE.overlapResolution, ModConfig.DEFAULT.overlapResolution,
                ModConfig.INSTANCE::setOverlapResolution
        ));

        for (VanillaItemTypes type : VanillaItemTypes.values()) {
            itemTypeControlCategory.add(getBooleanEntry(
                    "keep_" + type.name().toLowerCase(),
                    ModConfig.INSTANCE.vanillaItemTypesKeepingMap.get(type), ModConfig.DEFAULT.vanillaItemTypesKeepingMap.get(type),
                    value -> ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(type, value)
            ));
        }

        return itemTypeControlCategory.build();
    }


    // --- EXP ---
    protected static SubCategoryListEntry getExpControlCategory() {
        SubCategoryBuilder expControlCategory = getSubCategory("exp_control");

        expControlCategory.add(getEnumEntry(
                "experience_behaviour", ExperienceMode.class,
                ModConfig.INSTANCE.experienceBehaviour, ModConfig.DEFAULT.experienceBehaviour,
                ModConfig.INSTANCE::setExperienceBehaviour
        ));
        expControlCategory.add(getEnumEntry(
                "experience_calculation_mode", ExperienceCalculation.class,
                ModConfig.INSTANCE.experienceCalculationMode, ModConfig.DEFAULT.experienceCalculationMode,
                ModConfig.INSTANCE::setExperienceCalculationMode
        ));
        expControlCategory.add(getFloatEntry(
                "experience_fraction",
                ModConfig.INSTANCE.experienceFraction, ModConfig.DEFAULT.experienceFraction,
                ModConfig.INSTANCE::setExperienceFraction
        ));

        return expControlCategory.build();
    }


    protected static SubCategoryListEntry getRandomDropControlCategory() {
        SubCategoryBuilder randomDropControlCategory = getSubCategory("random_drop_control");

        randomDropControlCategory.add(getFloatEntry(
                "random_drop_chance",
                ModConfig.INSTANCE.randomDropChance, ModConfig.DEFAULT.randomDropChance,
                ModConfig.INSTANCE::setRandomDropChance
        ));
        randomDropControlCategory.add(getFloatEntry(
                "rarity_drop_chance_decrease",
                ModConfig.INSTANCE.rarityDropChanceDecrease, ModConfig.DEFAULT.rarityDropChanceDecrease,
                ModConfig.INSTANCE::setRarityDropChanceDecrease
        ));
        randomDropControlCategory.add(getFloatEntry(
                "luck_drop_chance_decrease",
                ModConfig.INSTANCE.luckDropChanceDecrease, ModConfig.DEFAULT.luckDropChanceDecrease,
                ModConfig.INSTANCE::setLuckDropChanceDecrease
        ));

        return randomDropControlCategory.build();
    }


    protected static SubCategoryListEntry getDropControlCategory() {
        SubCategoryBuilder dropControlCategory = getSubCategory("drop_control");

        dropControlCategory.add(getBooleanEntry(
                "random_item_spread",
                ModConfig.INSTANCE.randomSpread, ModConfig.DEFAULT.randomSpread,
                ModConfig.INSTANCE::setRandomSpread
        ));
        dropControlCategory.add(getBooleanEntry(
                "contain_drop",
                ModConfig.INSTANCE.containDrop, ModConfig.DEFAULT.containDrop,
                ModConfig.INSTANCE::setContainDrop
        ));
        dropControlCategory.add(getEnumEntry(
                "contain_drop_mode", ContainDropMode.class,
                ModConfig.INSTANCE.containDropMode, ModConfig.DEFAULT.containDropMode,
                ModConfig.INSTANCE::setContainDropMode
        ));

        SubCategoryBuilder graveSpawningLogic = getSubCategory("grave_spawning_logic");
        graveSpawningLogic.add(getIntegerEntry(
                "sack_max_spawn_radius",
                ModConfig.INSTANCE.sackMaxSpawnRadius, ModConfig.DEFAULT.sackMaxSpawnRadius,
                ModConfig.INSTANCE::setSackMaxSpawnRadius
        ));
        graveSpawningLogic.add(getIntegerEntry(
                "mob_head_grave_max_spawn_radius",
                ModConfig.INSTANCE.mobGraveMaxSpawnRadius, ModConfig.DEFAULT.mobGraveMaxSpawnRadius,
                ModConfig.INSTANCE::setMobGraveMaxSpawnRadius
        ));
        graveSpawningLogic.add(getBooleanEntry(
                "allow_mob_heads_graves_to_spawn_on_slabs",
                ModConfig.INSTANCE.allowGravesToSpawnOnSlabs, ModConfig.DEFAULT.allowGravesToSpawnOnSlabs,
                ModConfig.INSTANCE::setAllowGravesToSpawnOnSlabs
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
    protected static Component getCategoryTooltipTranslationKey(@NotNull String name) {
        return getCategoryTranslationKey(name + ".tooltip");
    }
    protected static Component getCategoryTranslationKey(@NotNull String name) {
        return getTranslationKey("category." + name);
    }
    protected static Component getSettingTooltipTranslationKey(@NotNull String name) {
        return getSettingTranslationKey(name + ".tooltip");
    }
    protected static Component getSettingTranslationKey(@NotNull String name) {
        return getTranslationKey("setting." + name);
    }
    protected static Component getTranslationKey(@NotNull String name) {
        return Component.translatable("config.savethehotbar." + name);
    }
}
