package io.github.mikip98.savethehotbar.config;

import io.github.mikip98.savethehotbar.config.enums.ContainDropMode;
import io.github.mikip98.savethehotbar.config.enums.ExperienceCalculation;
import io.github.mikip98.savethehotbar.config.enums.ExperienceMode;
import io.github.mikip98.savethehotbar.config.io.ConfigSaver;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModConfigScreen {
    public static Screen createScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setSavingRunnable(ConfigSaver::saveConfigToFile)
                .setParentScreen(parentScreen)
                .setTitle(Text.of("SaveTheHotbar! Configuration Screen"));

        // Create a root category
        ConfigCategory rootCategory = builder.getOrCreateCategory(Text.literal("General Settings"));

        // --- Main toggle ---
        rootCategory.addEntry(ConfigEntryBuilder.create()
                .startBooleanToggle(Text.of("Enabled"), ModConfig.enabled)
                .setTooltip(Text.of("Should the mod be enabled"))
                .setDefaultValue(ModConfig.dEnabled)
                .setSaveConsumer(value -> ModConfig.enabled = value)
                .build()
        );

        // --- Item Slots ---
        // Vanilla
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
        // Modded
        SubCategoryBuilder moddedSlotSettings = ConfigEntryBuilder.create().startSubCategory(Text.of("Modded Slot Settings"));
        moddedSlotSettings.add(ConfigEntryBuilder.create()
                .startBooleanToggle(Text.of("Save Arsenal Back Slot"), ModConfig.saveArsenal)
                .setTooltip(Text.of("Keep Arsenal's back slot after death"))
                .setDefaultValue(ModConfig.dSaveArsenal)
                .setSaveConsumer(value -> ModConfig.saveArsenal = value)
                .build()
        );
        rootCategory.addEntry(moddedSlotSettings.build());

        // --- EXP ---
        rootCategory.addEntry(ConfigEntryBuilder.create()
                .startEnumSelector(Text.of("Experience Behaviour"), ExperienceMode.class, ModConfig.experienceBehaviour)
                .setTooltip(Text.of("""
                        Determines what will happen to the player's experience after death.
                        - DROP -> Experience will drop in the place of death
                        - STORE -> Expereince will be kept safe in the grave (required Contain Drop to be enabled)
                        - KEEP -> Experience is kept after death
                        The experience amount, either dropped, stored or kept, is determined by Experience Calculation Mode
                        """))
                .setDefaultValue(ModConfig.dExperienceBehaviour)
                .setSaveConsumer(value -> ModConfig.experienceBehaviour = value)
                .build()
        );
        rootCategory.addEntry(ConfigEntryBuilder.create()
                .startEnumSelector(Text.of("Experience Calculation Mode"), ExperienceCalculation.class, ModConfig.experienceCalculationMode)
                .setTooltip(Text.of("""
                        Determines the amount of EXP that will be dropped, stored or kept after death.=
                        - ALL -> The experience stays unchanged
                        - FRACTION -> The experience is multiplied by Experience Fraction
                        - VANILLA -> New experience will be equal to '{player_level} * 7'
                        The modes are sorted from the most to the least forgiving
                        ALL wil result in the most EXP, VANILLA with the least (unless 'Experience Fraction' is set very low)
                        """))
                .setDefaultValue(ModConfig.dExperienceCalculationMode)
                .setSaveConsumer(value -> ModConfig.experienceCalculationMode = value)
                .build()
        );
        rootCategory.addEntry(ConfigEntryBuilder.create()
                .startFloatField(Text.of("Experience Fraction"), ModConfig.experienceFraction)
                .setTooltip(Text.of("The fraction of experience that should be dropped/stored/kept after death"))
                .setDefaultValue(ModConfig.dExperienceFraction)
                .setMax(1.0f)
                .setMin(0.0f)
                .setSaveConsumer(value -> ModConfig.experienceFraction = value)
                .build()
        );


        SubCategoryBuilder randomItemLoseChanceSettings = ConfigEntryBuilder.create().startSubCategory(Text.of("Random Item Lose Chance Settings"));
        randomItemLoseChanceSettings.add(ConfigEntryBuilder.create()
                .startFloatField(Text.literal("Random Drop Chance"), ModConfig.randomDropChance)
                .setTooltip(Text.literal("The chance that a normally kept item will be randomly dropped/stored (e.g. a hotbar item) (0.0 -> 0%; 1.0 -> 100%)"))
                .setDefaultValue(DefaultConfig.dRandomDropChance)
                .setMax(1.0f)
                .setMin(0.0f)
                .setSaveConsumer(value -> ModConfig.randomDropChance = value)
                .build()
        );
        randomItemLoseChanceSettings.add(ConfigEntryBuilder.create()
                .startFloatField(Text.literal("Rarity Drop Chance Decrease"), ModConfig.rarityDropChanceDecrease)
                .setTooltip(Text.literal("A percentage by which the random drop chance will be decreased (e.g. rdc = 20%, rdcd = 20%, UNCOMMON item will have 16% change to drop)"))
                .setDefaultValue(DefaultConfig.dRarityDropChanceDecrease)
                .setMax(1.0f)
                .setMin(0.0f)
                .setSaveConsumer(value -> ModConfig.rarityDropChanceDecrease = value)
                .build()
        );
        randomItemLoseChanceSettings.add(ConfigEntryBuilder.create()
                .startFloatField(Text.literal("Luck Drop Chance Decrease"), ModConfig.luckDropChanceDecrease)
                .setTooltip(Text.literal("A percentage by which the random drop chance will be decreased (e.g. rdc = 20%, ldcd = 20%, with luck 1, item will have 16% change to drop)"))
                .setDefaultValue(DefaultConfig.dRarityDropChanceDecrease)
                .setMax(1.0f)
                .setMin(0.0f)
                .setSaveConsumer(value -> ModConfig.luckDropChanceDecrease = value)
                .build()
        );
        rootCategory.addEntry(randomItemLoseChanceSettings.build());


        rootCategory.addEntry(ConfigEntryBuilder.create()
                .startBooleanToggle(Text.literal("Random Item Spread"), ModConfig.randomSpread)
                .setTooltip(Text.literal("If `False`, all the items will drop in the exact position of your death and will not spread outwards.\nIf `True`, all the dropped items will be spread randomly in all directions like in vanilla"))
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
                .setTooltip(Text.literal("""
                    Changes the block in which non-kept items will be stored after death if `Contain Drop` is `True`.
                        - `"SACK"`
                          After player death, a sack will be spawned, containing all the non-kept items.
                          Drops items on destruction. Can be waterlogged. Does not drop itself nor any XP.
                        - `"SKELETON_HEAD"` / `"ZOMBIE_HEAD"` / `"RANDOM_HEAD"`
                          After player death, **mob head grave** will be spawned, containing all the non-kept items.
                          Drops items on destruction. Does not drop itself nor any XP.
                          The Grave will be spawned in the first found valid spawn location, which is a full block with replaceable block on top.
                          If such location inside `mobGraveMaxSpawnRadius` radius isn't found, a `SACK` grave will be spawned instead.
                        - `"GRAVE"`
                          GRAVESTONES https://modrinth.com/mod/pneumono_gravestones MOD REQUIRED!!!
                          After player death, a grave from the `Gravestones` mod will be spawned,
                          containing the non-kept items and XP according to its configuration."""
                ))
                .setDefaultValue(DefaultConfig.dContainDropMode)
                .setSaveConsumer(value -> ModConfig.containDropMode = value)
                .build()
        );

        SubCategoryBuilder graveSpawningLogic = ConfigEntryBuilder.create().startSubCategory(Text.of("Grave Spawning Logic"));
        graveSpawningLogic.add(ConfigEntryBuilder.create()
                .startIntField(Text.literal("Sack Max Spawn Radius"), ModConfig.sackMaxSpawnRadius)
                .setTooltip(Text.literal("The maximum search radius for a valid sack spawning position. If no valid place is found, a Sack will be spawned directly at the death position replacing whatever block was there."))
                .setDefaultValue(DefaultConfig.dSackMaxSpawnRadius)
                .setMin(0)
                .setSaveConsumer(value -> ModConfig.sackMaxSpawnRadius = value)
                .build()
        );
        graveSpawningLogic.add(ConfigEntryBuilder.create()
                .startIntField(Text.literal("Mob Head Grave Max Spawn Radius"), ModConfig.mobGraveMaxSpawnRadius)
                .setTooltip(Text.literal("The maximum search radius for a valid mob grave spawning position. If no valid place will be found, a Sack will be spawned instead"))
                .setDefaultValue(DefaultConfig.dMobGraveMaxSpawnRadius)
                .setMin(0)
                .setSaveConsumer(value -> ModConfig.mobGraveMaxSpawnRadius = value)
                .build()
        );
        graveSpawningLogic.add(ConfigEntryBuilder.create()
                .startBooleanToggle(Text.of("Allow Mob Head Graves To Spawn On Slabs"), ModConfig.allowGravesToSpawnOnSlabs)
                .setTooltip(Text.of("If `True`, a top slab block will also be considered a valid grave spawning block"))
                .setDefaultValue(DefaultConfig.dAllowGravesToSpawnOnSlabs)
                .setSaveConsumer(value -> ModConfig.allowGravesToSpawnOnSlabs = value)
                .build()
        );
        rootCategory.addEntry(graveSpawningLogic.build());


        rootCategory.addEntry(ConfigEntryBuilder.create()
                .startBooleanToggle(Text.literal("Log Death Coordinates in Chat"), ModConfig.logDeathCoordinatesInChat)
                .setTooltip(Text.literal("If `True`, the death coordinates will be sent in chat upon player death"))
                .setDefaultValue(DefaultConfig.dLogDeathCoordinatesInChat)
                .setSaveConsumer(value -> ModConfig.logDeathCoordinatesInChat = value)
                .build()
        );

        rootCategory.addEntry(ConfigEntryBuilder.create()
                .startBooleanToggle(Text.literal("Log Grave Coordinates in Chat"), ModConfig.logGraveCoordinatesInChat)
                .setTooltip(Text.literal("If `True`, the grave spawn coordinates will be sent in chat upon player death"))
                .setDefaultValue(DefaultConfig.dLogGraveCoordinatesInChat)
                .setSaveConsumer(value -> ModConfig.logGraveCoordinatesInChat = value)
                .build()
        );


        return builder.build();
    }
}
