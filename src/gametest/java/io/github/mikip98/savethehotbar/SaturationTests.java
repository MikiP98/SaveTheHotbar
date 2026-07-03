package io.github.mikip98.savethehotbar;

import com.mojang.authlib.GameProfile;
import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.config.enums.SaturationLevelCalculationMode;
import net.fabricmc.fabric.api.entity.FakePlayer;
#if MC_VERSION < 12108 import net.fabricmc.fabric.api.gametest.v1.FabricGameTest; #endif
#if MC_VERSION < 12108 import net.minecraft.gametest.framework.GameTestGenerator; #endif
import net.minecraft.gametest.framework.GameTestHelper;
#if MC_VERSION < 12108 import net.minecraft.gametest.framework.TestFunction; #endif
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Rotation;

import java.util.*;

import static io.github.mikip98.savethehotbar.Util.msg;

public class SaturationTests #if MC_VERSION < 12108 implements FabricGameTest #endif {
    // TODO: Finish porting to 1.21.8+
    #if MC_VERSION < 12108
    @SuppressWarnings("unused")
    @GameTestGenerator
    public Collection<TestFunction> generateKeepInventoryTests() {
        final String batchName = "save_the_hotbar_test_saturation";
        final String testPrefix = "test_saturation_";

        ModConfig vanilla = new ModConfig();
        vanilla.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.VANILLA;

        ModConfig keepSaturation = new ModConfig();
        keepSaturation.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.KEEP;

        ModConfig clampSaturation_0_20 = new ModConfig();
        clampSaturation_0_20.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.CLAMP;
        clampSaturation_0_20.hungerControl.minSaturation = 0;
        clampSaturation_0_20.hungerControl.maxSaturation = 20;

        ModConfig clampSaturation_5_15 = new ModConfig();
        clampSaturation_5_15.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.CLAMP;
        clampSaturation_5_15.hungerControl.minSaturation = 5;
        clampSaturation_5_15.hungerControl.maxSaturation = 15;

        ModConfig clampSaturation_10_10 = new ModConfig();
        clampSaturation_10_10.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.CLAMP;
        clampSaturation_10_10.hungerControl.minSaturation = 10;
        clampSaturation_10_10.hungerControl.maxSaturation = 10;

        ModConfig fraction_0 = new ModConfig();
        fraction_0.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION;
        fraction_0.hungerControl.saturationFraction = 0;

        ModConfig fraction_05 = new ModConfig();
        fraction_05.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION;
        fraction_05.hungerControl.saturationFraction = 0.5f;

        ModConfig fraction_1 = new ModConfig();
        fraction_1.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION;
        fraction_1.hungerControl.saturationFraction = 1;

        ModConfig fraction_2 = new ModConfig();
        fraction_2.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION;
        fraction_2.hungerControl.saturationFraction = 2;

        ModConfig fraction_0_clamp_0_20 = new ModConfig();
        fraction_0_clamp_0_20.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_0_clamp_0_20.hungerControl.saturationFraction = 0;
        fraction_0_clamp_0_20.hungerControl.minSaturation = 0;
        fraction_0_clamp_0_20.hungerControl.maxSaturation = 20;

        ModConfig fraction_0_clamp_5_15 = new ModConfig();
        fraction_0_clamp_5_15.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_0_clamp_5_15.hungerControl.saturationFraction = 0;
        fraction_0_clamp_5_15.hungerControl.minSaturation = 5;
        fraction_0_clamp_5_15.hungerControl.maxSaturation = 15;

        ModConfig fraction_0_clamp_10_10 = new ModConfig();
        fraction_0_clamp_10_10.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_0_clamp_10_10.hungerControl.saturationFraction = 0;
        fraction_0_clamp_10_10.hungerControl.minSaturation = 10;
        fraction_0_clamp_10_10.hungerControl.maxSaturation = 10;

        ModConfig fraction_05_clamp_0_20 = new ModConfig();
        fraction_05_clamp_0_20.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_05_clamp_0_20.hungerControl.saturationFraction = 0.5f;
        fraction_05_clamp_0_20.hungerControl.minSaturation = 0;
        fraction_05_clamp_0_20.hungerControl.maxSaturation = 20;

        ModConfig fraction_05_clamp_5_15 = new ModConfig();
        fraction_05_clamp_5_15.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_05_clamp_5_15.hungerControl.saturationFraction = 0.5f;
        fraction_05_clamp_5_15.hungerControl.minSaturation = 5;
        fraction_05_clamp_5_15.hungerControl.maxSaturation = 15;

        ModConfig fraction_05_clamp_10_10 = new ModConfig();
        fraction_05_clamp_10_10.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_05_clamp_10_10.hungerControl.saturationFraction = 0.5f;
        fraction_05_clamp_10_10.hungerControl.minSaturation = 10;
        fraction_05_clamp_10_10.hungerControl.maxSaturation = 10;

        ModConfig fraction_1_clamp_0_20 = new ModConfig();
        fraction_1_clamp_0_20.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_1_clamp_0_20.hungerControl.saturationFraction = 1;
        fraction_1_clamp_0_20.hungerControl.minSaturation = 0;
        fraction_1_clamp_0_20.hungerControl.maxSaturation = 20;

        ModConfig fraction_1_clamp_5_15 = new ModConfig();
        fraction_1_clamp_5_15.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_1_clamp_5_15.hungerControl.saturationFraction = 1;
        fraction_1_clamp_5_15.hungerControl.minSaturation = 5;
        fraction_1_clamp_5_15.hungerControl.maxSaturation = 15;

        ModConfig fraction_1_clamp_10_10 = new ModConfig();
        fraction_1_clamp_10_10.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_1_clamp_10_10.hungerControl.saturationFraction = 1;
        fraction_1_clamp_10_10.hungerControl.minSaturation = 10;
        fraction_1_clamp_10_10.hungerControl.maxSaturation = 10;

        ModConfig fraction_2_clamp_0_20 = new ModConfig();
        fraction_2_clamp_0_20.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_2_clamp_0_20.hungerControl.saturationFraction = 2;
        fraction_2_clamp_0_20.hungerControl.minSaturation = 0;
        fraction_2_clamp_0_20.hungerControl.maxSaturation = 20;

        ModConfig fraction_2_clamp_5_15 = new ModConfig();
        fraction_2_clamp_5_15.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_2_clamp_5_15.hungerControl.saturationFraction = 2;
        fraction_2_clamp_5_15.hungerControl.minSaturation = 5;
        fraction_2_clamp_5_15.hungerControl.maxSaturation = 15;

        ModConfig fraction_2_clamp_10_10 = new ModConfig();
        fraction_2_clamp_10_10.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_2_clamp_10_10.hungerControl.saturationFraction = 2;
        fraction_2_clamp_10_10.hungerControl.minSaturation = 10;
        fraction_2_clamp_10_10.hungerControl.maxSaturation = 10;

        ModConfig setSaturation_0 = new ModConfig();
        setSaturation_0.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.SET;
        setSaturation_0.hungerControl.maxSaturation = 0;

        ModConfig setSaturation_10 = new ModConfig();
        setSaturation_10.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.SET;
        setSaturation_10.hungerControl.maxSaturation = 10;

        ModConfig setSaturation_20 = new ModConfig();
        setSaturation_20.hungerControl.saturationLevelCalculationMode = SaturationLevelCalculationMode.SET;
        setSaturation_20.hungerControl.maxSaturation = 20;

        final Map<String, TestEntry> testEntries = Map.<String, TestEntry>ofEntries(
                Map.entry("vanilla_from_20", new TestEntry(vanilla, 20, 5)),
                Map.entry("vanilla_from_0", new TestEntry(vanilla, 0, 5)),
                Map.entry("vanilla_from_10", new TestEntry(vanilla, 10, 5)),

                Map.entry("keep_from_20", new TestEntry(keepSaturation, 20, 20)),
                Map.entry("keep_from_0", new TestEntry(keepSaturation, 0, 0)),
                Map.entry("keep_from_10", new TestEntry(keepSaturation, 10, 10)),

                Map.entry("clampSaturation_0_20_from_20", new TestEntry(clampSaturation_0_20, 20, 20)),
                Map.entry("clampSaturation_0_20_from_0", new TestEntry(clampSaturation_0_20, 0, 0)),
                Map.entry("clampSaturation_0_20_from_10", new TestEntry(clampSaturation_0_20, 10, 10)),

                Map.entry("clampSaturation_5_15_from_20", new TestEntry(clampSaturation_5_15, 20, 15)),
                Map.entry("clampSaturation_5_15_from_0", new TestEntry(clampSaturation_5_15, 0, 5)),
                Map.entry("clampSaturation_5_15_from_10", new TestEntry(clampSaturation_5_15, 10, 10)),

                Map.entry("clampSaturation_10_10_from_20", new TestEntry(clampSaturation_10_10, 20, 10)),
                Map.entry("clampSaturation_10_10_from_0", new TestEntry(clampSaturation_10_10, 0, 10)),
                Map.entry("clampSaturation_10_10_from_10", new TestEntry(clampSaturation_10_10, 10, 10)),

                Map.entry("fraction_0_from_20", new TestEntry(fraction_0, 20, 0)),
                Map.entry("fraction_0_from_0", new TestEntry(fraction_0, 0, 0)),
                Map.entry("fraction_0_from_10", new TestEntry(fraction_0, 10, 0)),

                Map.entry("fraction_05_from_20", new TestEntry(fraction_05, 20, 10)),
                Map.entry("fraction_05_from_0", new TestEntry(fraction_05, 0, 0)),
                Map.entry("fraction_05_from_10", new TestEntry(fraction_05, 10, 5)),

                Map.entry("fraction_1_from_20", new TestEntry(fraction_1, 20, 20)),
                Map.entry("fraction_1_from_0", new TestEntry(fraction_1, 0, 0)),
                Map.entry("fraction_1_from_10", new TestEntry(fraction_1, 10, 10)),

                Map.entry("fraction_2_from_20", new TestEntry(fraction_2, 20, 40)),  // Goes over vanilla limit, TODO: Add a separate config option to clamp it!
                Map.entry("fraction_2_from_0", new TestEntry(fraction_2, 0, 0)),
                Map.entry("fraction_2_from_10", new TestEntry(fraction_2, 10, 20)),

                Map.entry("fraction_0_clamp_0_20_from_20", new TestEntry(fraction_0_clamp_0_20, 20, 0)),
                Map.entry("fraction_0_clamp_0_20_from_0", new TestEntry(fraction_0_clamp_0_20, 0, 0)),
                Map.entry("fraction_0_clamp_0_20_from_10", new TestEntry(fraction_0_clamp_0_20, 10, 0)),

                Map.entry("fraction_0_clamp_5_15_from_20", new TestEntry(fraction_0_clamp_5_15, 20, 5)),
                Map.entry("fraction_0_clamp_5_15_from_0", new TestEntry(fraction_0_clamp_5_15, 0, 5)),
                Map.entry("fraction_0_clamp_5_15_from_10", new TestEntry(fraction_0_clamp_5_15, 10, 5)),

                Map.entry("fraction_0_clamp_10_10_from_20", new TestEntry(fraction_0_clamp_10_10, 20, 10)),
                Map.entry("fraction_0_clamp_10_10_from_0", new TestEntry(fraction_0_clamp_10_10, 0, 10)),
                Map.entry("fraction_0_clamp_10_10_from_10", new TestEntry(fraction_0_clamp_10_10, 10, 10)),

                Map.entry("fraction_05_clamp_0_20_from_20", new TestEntry(fraction_05_clamp_0_20, 20, 10)),
                Map.entry("fraction_05_clamp_0_20_from_0", new TestEntry(fraction_05_clamp_0_20, 0, 0)),
                Map.entry("fraction_05_clamp_0_20_from_10", new TestEntry(fraction_05_clamp_0_20, 10, 5)),

                Map.entry("fraction_05_clamp_5_15_from_20", new TestEntry(fraction_05_clamp_5_15, 20, 10)),
                Map.entry("fraction_05_clamp_5_15_from_0", new TestEntry(fraction_05_clamp_5_15, 0, 5)),
                Map.entry("fraction_05_clamp_5_15_from_10", new TestEntry(fraction_05_clamp_5_15, 10, 5)),

                Map.entry("fraction_05_clamp_10_10_from_20", new TestEntry(fraction_05_clamp_10_10, 20, 10)),
                Map.entry("fraction_05_clamp_10_10_from_0", new TestEntry(fraction_05_clamp_10_10, 0, 10)),
                Map.entry("fraction_05_clamp_10_10_from_10", new TestEntry(fraction_05_clamp_10_10, 10, 10)),

                Map.entry("fraction_1_clamp_0_20_from_20", new TestEntry(fraction_1_clamp_0_20, 20, 20)),
                Map.entry("fraction_1_clamp_0_20_from_0", new TestEntry(fraction_1_clamp_0_20, 0, 0)),
                Map.entry("fraction_1_clamp_0_20_from_10", new TestEntry(fraction_1_clamp_0_20, 10, 10)),

                Map.entry("fraction_1_clamp_5_15_from_20", new TestEntry(fraction_1_clamp_5_15, 20, 15)),
                Map.entry("fraction_1_clamp_5_15_from_0", new TestEntry(fraction_1_clamp_5_15, 0, 5)),
                Map.entry("fraction_1_clamp_5_15_from_10", new TestEntry(fraction_1_clamp_5_15, 10, 10)),

                Map.entry("fraction_1_clamp_10_10_from_20", new TestEntry(fraction_1_clamp_10_10, 20, 10)),
                Map.entry("fraction_1_clamp_10_10_from_0", new TestEntry(fraction_1_clamp_10_10, 0, 10)),
                Map.entry("fraction_1_clamp_10_10_from_10", new TestEntry(fraction_1_clamp_10_10, 10, 10)),

                Map.entry("fraction_2_clamp_0_20_from_20", new TestEntry(fraction_2_clamp_0_20, 20, 20)),
                Map.entry("fraction_2_clamp_0_20_from_0", new TestEntry(fraction_2_clamp_0_20, 0, 0)),
                Map.entry("fraction_2_clamp_0_20_from_10", new TestEntry(fraction_2_clamp_0_20, 10, 20)),

                Map.entry("fraction_2_clamp_5_15_from_20", new TestEntry(fraction_2_clamp_5_15, 20, 15)),
                Map.entry("fraction_2_clamp_5_15_from_0", new TestEntry(fraction_2_clamp_5_15, 0, 5)),
                Map.entry("fraction_2_clamp_5_15_from_10", new TestEntry(fraction_2_clamp_5_15, 10, 15)),

                Map.entry("fraction_2_clamp_10_10_from_20", new TestEntry(fraction_2_clamp_10_10, 20, 10)),
                Map.entry("fraction_2_clamp_10_10_from_0", new TestEntry(fraction_2_clamp_10_10, 0, 10)),
                Map.entry("fraction_2_clamp_10_10_from_10", new TestEntry(fraction_2_clamp_10_10, 10, 10)),

                Map.entry("set_0_from_20", new TestEntry(setSaturation_0, 20, 0)),
                Map.entry("set_0_from_0", new TestEntry(setSaturation_0, 0, 0)),
                Map.entry("set_0_from_10", new TestEntry(setSaturation_0, 10, 0)),

                Map.entry("set_10_from_20", new TestEntry(setSaturation_10, 20, 10)),
                Map.entry("set_10_from_0", new TestEntry(setSaturation_10, 0, 10)),
                Map.entry("set_10_from_10", new TestEntry(setSaturation_10, 10, 10)),

                Map.entry("set_20_from_20", new TestEntry(setSaturation_20, 20, 20)),
                Map.entry("set_20_from_0", new TestEntry(setSaturation_20, 0, 20)),
                Map.entry("set_20_from_10", new TestEntry(setSaturation_20, 10, 20))
        );

        List<TestFunction> tests = new ArrayList<>(testEntries.size());
        for (Map.Entry<String, TestEntry> entry : testEntries.entrySet()) {
            final String testName = testPrefix + entry.getKey();
            tests.add(new TestFunction(
                    batchName,                       // Batch ID
                    testName,                        // Unique Test Name
                    FabricGameTest.EMPTY_STRUCTURE,  // Structure Template
                    Rotation.NONE,                   // Rotation
                    10,                              // Max ticks before failure (0.5s) TODO: Check what is the true min
                    0L,                              // Setup ticks
                    true,                            // Required to pass? TODO: Check why
                    helper -> runParameterizedDeathTest(helper, entry.getValue())
            ));
        }
        return tests;
    }
    #endif

    private record TestEntry(ModConfig config, float initialValue, float expectedValue) {}

    private void runParameterizedDeathTest(GameTestHelper helper, TestEntry testParams) {
        ModConfig.INSTANCE = testParams.config;

        final ServerLevel level = helper.getLevel();

        final ServerPlayer player = FakePlayer.get(
                level,
                new GameProfile(UUID.randomUUID(), "OldPlayer")
        );
        player.getFoodData().setSaturation(testParams.initialValue);
        player.hurt(level.damageSources().generic(), 1000.0f);

        final ServerPlayer respawnedPlayer = FakePlayer.get(
                level,
                new GameProfile(UUID.randomUUID(), "NewPlayer")
        );
        respawnedPlayer.restoreFrom(player, false);

        final float afterDeathPlayerSaturation = respawnedPlayer.getFoodData().getSaturationLevel();
        helper.assertTrue(
                afterDeathPlayerSaturation == testParams.expectedValue,
                msg("Wrong saturation level after death! Expected '" + testParams.expectedValue + "', got '" + afterDeathPlayerSaturation + "'")
        );
        helper.succeed();
    }
}
