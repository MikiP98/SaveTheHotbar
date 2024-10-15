package io.github.mikip98.savethehotbar.config;

import io.github.mikip98.savethehotbar.enums.ContainDropMode;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModConfigScreen {
    public static Screen createScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setSavingRunnable(ConfigSaver::saveConfigToFile)
                .setParentScreen(parentScreen)
                .setTitle(Text.literal("SaveTheHotbar! Configuration Screen"));

        // Create a root category
        ConfigCategory rootCategory = builder.getOrCreateCategory(Text.literal("General Settings"));

        rootCategory.addEntry(ConfigEntryBuilder.create()
                .startBooleanToggle(Text.literal("Save Hotbar"), ModConfig.saveHotbar)
                .setTooltip(Text.literal("Keep the all the hotbar items after death"))
                .setDefaultValue(DefaultConfig.dSaveHotbar)
                .setSaveConsumer(value -> ModConfig.saveHotbar = value)
                .build()
        );

        rootCategory.addEntry(ConfigEntryBuilder.create()
                .startBooleanToggle(Text.literal("Save Armor"), ModConfig.saveArmor)
                .setTooltip(Text.literal("Keep the all the armor pieces after death"))
                .setDefaultValue(DefaultConfig.dSaveArmor)
                .setSaveConsumer(value -> ModConfig.saveArmor = value)
                .build()
        );

        rootCategory.addEntry(ConfigEntryBuilder.create()
                .startBooleanToggle(Text.literal("Save Second Hand"), ModConfig.saveSecondHand)
                .setTooltip(Text.literal("Keep the second hand item after death"))
                .setDefaultValue(DefaultConfig.dSaveSecondHand)
                .setSaveConsumer(value -> ModConfig.saveSecondHand = value)
                .build()
        );

        rootCategory.addEntry(ConfigEntryBuilder.create()
                .startBooleanToggle(Text.literal("Random Item Spread"), ModConfig.randomSpread)
                .setTooltip(Text.literal("If `False`, all the items will drop in the exact position of your death and will not spread outwards. If `True`, all the dropped items will be spread randomly in all directions"))
                .setDefaultValue(DefaultConfig.dRandomSpread)
                .setSaveConsumer(value -> ModConfig.randomSpread = value)
                .build()
        );

        rootCategory.addEntry(ConfigEntryBuilder.create()
                .startBooleanToggle(Text.literal("Contain Drop"), ModConfig.containDrop)
                .setTooltip(Text.literal("If `False`, all non-kept items will be dropped after death. If `True`, all non-kept will be stored in a block specified by the \"Contain Drop Mode\" option"))
                .setDefaultValue(DefaultConfig.dContainDrop)
                .setSaveConsumer(value -> ModConfig.containDrop = value)
                .build()
        );

        rootCategory.addEntry(ConfigEntryBuilder.create()
                .startEnumSelector(Text.literal("Contain Drop Mode"), ContainDropMode.class, ModConfig.containDropMode)
                .setTooltip(Text.literal("Changes the block in which non-kept items will be stored after death if `Contain Drop` is `True`"))
                .setDefaultValue(DefaultConfig.dContainDropMode)
                .setSaveConsumer(value -> ModConfig.containDropMode = value)
                .build()
        );

        rootCategory.addEntry(ConfigEntryBuilder.create()
                .startFloatField(Text.literal("Random Drop Chance"), ModConfig.randomDropChance)
                .setTooltip(Text.literal("The chance that a normally kept item will be randomly dropped (e.g. a hotbar item) (0.0 -> 0%; 1.0 -> 100%)"))
                .setDefaultValue(DefaultConfig.dRandomDropChance)
                .setMax(1.0f)
                .setMin(0.0f)
                .setSaveConsumer(value -> ModConfig.randomDropChance = value)
                .build()
        );

        rootCategory.addEntry(ConfigEntryBuilder.create()
                .startFloatField(Text.literal("Rarity Drop Chance Decrease"), ModConfig.rarityDropChanceDecrease)
                .setTooltip(Text.literal("A dividor of the random drop chance for each Rarity increase (e.g. item with rarity UNCOMMON will have the drop chance halved and RARE will have the drop chance halved again)"))
                .setDefaultValue(DefaultConfig.dRarityDropChanceDecrease)
                .setMin(0.0f)
                .setSaveConsumer(value -> ModConfig.rarityDropChanceDecrease = value)
                .build()
        );

        return builder.build();
    }

}
