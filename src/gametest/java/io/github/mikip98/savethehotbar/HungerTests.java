package io.github.mikip98.savethehotbar;

import com.mojang.authlib.GameProfile;
import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.config.enums.*;
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

public class HungerTests #if MC_VERSION < 12108 implements FabricGameTest #endif {
    // TODO: Finish porting to 1.21.8+
    #if MC_VERSION < 12108
    @SuppressWarnings("unused")
    @GameTestGenerator
    public Collection<TestFunction> generateKeepInventoryTests() {
        final String batchName = "save_the_hotbar_test_hunger";
        final String testPrefix = "test_hunger_";

        ModConfig vanilla = new ModConfig();
        vanilla.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.VANILLA;

        ModConfig keepHunger = new ModConfig();
        keepHunger.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.KEEP;

        ModConfig clampHunger_0_20 = new ModConfig();
        clampHunger_0_20.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.CLAMP;
        clampHunger_0_20.hungerControl.minFoodLevel = 0;
        clampHunger_0_20.hungerControl.maxFoodLevel = 20;

        ModConfig clampHunger_5_15 = new ModConfig();
        clampHunger_5_15.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.CLAMP;
        clampHunger_5_15.hungerControl.minFoodLevel = 5;
        clampHunger_5_15.hungerControl.maxFoodLevel = 15;

        ModConfig clampHunger_10_10 = new ModConfig();
        clampHunger_10_10.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.CLAMP;
        clampHunger_10_10.hungerControl.minFoodLevel = 10;
        clampHunger_10_10.hungerControl.maxFoodLevel = 10;

        ModConfig fraction_0 = new ModConfig();
        fraction_0.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION;
        fraction_0.hungerControl.foodLevelFraction = 0;

        ModConfig fraction_05 = new ModConfig();
        fraction_05.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION;
        fraction_05.hungerControl.foodLevelFraction = 0.5f;

        ModConfig fraction_1 = new ModConfig();
        fraction_1.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION;
        fraction_1.hungerControl.foodLevelFraction = 1;

        ModConfig fraction_2 = new ModConfig();
        fraction_2.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION;
        fraction_2.hungerControl.foodLevelFraction = 2;

        ModConfig fraction_0_clamp_0_20 = new ModConfig();
        fraction_0_clamp_0_20.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_0_clamp_0_20.hungerControl.foodLevelFraction = 0;
        fraction_0_clamp_0_20.hungerControl.minFoodLevel = 0;
        fraction_0_clamp_0_20.hungerControl.maxFoodLevel = 20;

        ModConfig fraction_0_clamp_5_15 = new ModConfig();
        fraction_0_clamp_5_15.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_0_clamp_5_15.hungerControl.foodLevelFraction = 0;
        fraction_0_clamp_5_15.hungerControl.minFoodLevel = 5;
        fraction_0_clamp_5_15.hungerControl.maxFoodLevel = 15;

        ModConfig fraction_0_clamp_10_10 = new ModConfig();
        fraction_0_clamp_10_10.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_0_clamp_10_10.hungerControl.foodLevelFraction = 0;
        fraction_0_clamp_10_10.hungerControl.minFoodLevel = 10;
        fraction_0_clamp_10_10.hungerControl.maxFoodLevel = 10;

        ModConfig fraction_05_clamp_0_20 = new ModConfig();
        fraction_05_clamp_0_20.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_05_clamp_0_20.hungerControl.foodLevelFraction = 0.5f;
        fraction_05_clamp_0_20.hungerControl.minFoodLevel = 0;
        fraction_05_clamp_0_20.hungerControl.maxFoodLevel = 20;

        ModConfig fraction_05_clamp_5_15 = new ModConfig();
        fraction_05_clamp_5_15.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_05_clamp_5_15.hungerControl.foodLevelFraction = 0.5f;
        fraction_05_clamp_5_15.hungerControl.minFoodLevel = 5;
        fraction_05_clamp_5_15.hungerControl.maxFoodLevel = 15;

        ModConfig fraction_05_clamp_10_10 = new ModConfig();
        fraction_05_clamp_10_10.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_05_clamp_10_10.hungerControl.foodLevelFraction = 0.5f;
        fraction_05_clamp_10_10.hungerControl.minFoodLevel = 10;
        fraction_05_clamp_10_10.hungerControl.maxFoodLevel = 10;

        ModConfig fraction_1_clamp_0_20 = new ModConfig();
        fraction_1_clamp_0_20.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_1_clamp_0_20.hungerControl.foodLevelFraction = 1;
        fraction_1_clamp_0_20.hungerControl.minFoodLevel = 0;
        fraction_1_clamp_0_20.hungerControl.maxFoodLevel = 20;

        ModConfig fraction_1_clamp_5_15 = new ModConfig();
        fraction_1_clamp_5_15.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_1_clamp_5_15.hungerControl.foodLevelFraction = 1;
        fraction_1_clamp_5_15.hungerControl.minFoodLevel = 5;
        fraction_1_clamp_5_15.hungerControl.maxFoodLevel = 15;

        ModConfig fraction_1_clamp_10_10 = new ModConfig();
        fraction_1_clamp_10_10.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_1_clamp_10_10.hungerControl.foodLevelFraction = 1;
        fraction_1_clamp_10_10.hungerControl.minFoodLevel = 10;
        fraction_1_clamp_10_10.hungerControl.maxFoodLevel = 10;

        ModConfig fraction_2_clamp_0_20 = new ModConfig();
        fraction_2_clamp_0_20.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_2_clamp_0_20.hungerControl.foodLevelFraction = 2;
        fraction_2_clamp_0_20.hungerControl.minFoodLevel = 0;
        fraction_2_clamp_0_20.hungerControl.maxFoodLevel = 20;

        ModConfig fraction_2_clamp_5_15 = new ModConfig();
        fraction_2_clamp_5_15.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_2_clamp_5_15.hungerControl.foodLevelFraction = 2;
        fraction_2_clamp_5_15.hungerControl.minFoodLevel = 5;
        fraction_2_clamp_5_15.hungerControl.maxFoodLevel = 15;

        ModConfig fraction_2_clamp_10_10 = new ModConfig();
        fraction_2_clamp_10_10.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.FRACTION_AND_CLAMP;
        fraction_2_clamp_10_10.hungerControl.foodLevelFraction = 2;
        fraction_2_clamp_10_10.hungerControl.minFoodLevel = 10;
        fraction_2_clamp_10_10.hungerControl.maxFoodLevel = 10;

        ModConfig setHunger_0 = new ModConfig();
        setHunger_0.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.SET;
        setHunger_0.hungerControl.maxFoodLevel = 0;

        ModConfig setHunger_10 = new ModConfig();
        setHunger_10.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.SET;
        setHunger_10.hungerControl.maxFoodLevel = 10;

        ModConfig setHunger_20 = new ModConfig();
        setHunger_20.hungerControl.foodLevelCalculationMode = FoodLevelCalculationMode.SET;
        setHunger_20.hungerControl.maxFoodLevel = 20;

        final Map<String, TestEntry> testEntries = Map.<String, TestEntry>ofEntries(
                Map.entry("vanilla_from_20", new TestEntry(vanilla, 20, 20)),
                Map.entry("vanilla_from_0", new TestEntry(vanilla, 0, 20)),
                Map.entry("vanilla_from_10", new TestEntry(vanilla, 10, 20)),

                Map.entry("keep_from_20", new TestEntry(keepHunger, 20, 20)),
                Map.entry("keep_from_0", new TestEntry(keepHunger, 0, 0)),
                Map.entry("keep_from_10", new TestEntry(keepHunger, 10, 10)),

                Map.entry("clampHunger_0_20_from_20", new TestEntry(clampHunger_0_20, 20, 20)),
                Map.entry("clampHunger_0_20_from_0", new TestEntry(clampHunger_0_20, 0, 0)),
                Map.entry("clampHunger_0_20_from_10", new TestEntry(clampHunger_0_20, 10, 10)),

                Map.entry("clampHunger_5_15_from_20", new TestEntry(clampHunger_5_15, 20, 15)),
                Map.entry("clampHunger_5_15_from_0", new TestEntry(clampHunger_5_15, 0, 5)),
                Map.entry("clampHunger_5_15_from_10", new TestEntry(clampHunger_5_15, 10, 10)),

                Map.entry("clampHunger_10_10_from_20", new TestEntry(clampHunger_10_10, 20, 10)),
                Map.entry("clampHunger_10_10_from_0", new TestEntry(clampHunger_10_10, 0, 10)),
                Map.entry("clampHunger_10_10_from_10", new TestEntry(clampHunger_10_10, 10, 10)),

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

                Map.entry("set_0_from_20", new TestEntry(setHunger_0, 20, 0)),
                Map.entry("set_0_from_0", new TestEntry(setHunger_0, 0, 0)),
                Map.entry("set_0_from_10", new TestEntry(setHunger_0, 10, 0)),

                Map.entry("set_10_from_20", new TestEntry(setHunger_10, 20, 10)),
                Map.entry("set_10_from_0", new TestEntry(setHunger_10, 0, 10)),
                Map.entry("set_10_from_10", new TestEntry(setHunger_10, 10, 10)),

                Map.entry("set_20_from_20", new TestEntry(setHunger_20, 20, 20)),
                Map.entry("set_20_from_0", new TestEntry(setHunger_20, 0, 20)),
                Map.entry("set_20_from_10", new TestEntry(setHunger_20, 10, 20))
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

    private record TestEntry(ModConfig config, int initialValue, int expectedValue) {}

    private void runParameterizedDeathTest(GameTestHelper helper, TestEntry testParams) {
        ModConfig.INSTANCE = testParams.config;

        final ServerLevel level = helper.getLevel();

        final ServerPlayer player = FakePlayer.get(
                level,
                new GameProfile(UUID.randomUUID(), "OldPlayer")
        );
        player.getFoodData().setFoodLevel(testParams.initialValue);
        player.hurt(level.damageSources().generic(), 1000.0f);

        final ServerPlayer respawnedPlayer = FakePlayer.get(
                level,
                new GameProfile(UUID.randomUUID(), "NewPlayer")
        );
        respawnedPlayer.restoreFrom(player, false);

        final int afterDeathPlayerHunger = respawnedPlayer.getFoodData().getFoodLevel();
        helper.assertTrue(
                afterDeathPlayerHunger == testParams.expectedValue,
                msg("Wrong hunger level after death! Expected '" + testParams.expectedValue + "', got '" + afterDeathPlayerHunger + "'")
        );
        helper.succeed();
    }
}
