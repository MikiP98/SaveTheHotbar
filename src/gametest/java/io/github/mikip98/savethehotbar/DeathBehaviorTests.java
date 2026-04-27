package io.github.mikip98.savethehotbar;

import io.github.mikip98.savethehotbar.config.ModConfig;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeathBehaviorTests implements FabricGameTest {

    // Helper Method: Common setup fixture
    private void giveUniversalItems(Player player) {
        // Slot 0: Hotbar
        player.getInventory().setItem(0, new ItemStack(Items.DIAMOND_SWORD));
        // Slot 15: Main Inventory
        player.getInventory().setItem(15, new ItemStack(Items.DIRT, 64));
        // Slot 39: Helmet
        player.getInventory().setItem(39, new ItemStack(Items.IRON_HELMET));
    }

    // Parametrized Test Generator
    @SuppressWarnings("unused")
    @GameTestGenerator
    public Collection<TestFunction> generateKeepInventoryTests() {
        List<TestFunction> tests = new ArrayList<>();

        boolean[] keepHotbarConfigs = {true, false};

        for (boolean keepHotbar : keepHotbarConfigs) {
            String testName = "test_death_keep_hotbar_" + keepHotbar;

            tests.add(new TestFunction(
                    "my_mod_batch",                  // Batch ID
                    testName,                        // Unique Test Name
                    FabricGameTest.EMPTY_STRUCTURE,  // Structure Template
                    Rotation.NONE,                   // Rotation (Mojmap name)
                    100,                             // Max ticks before failure
                    0L,                              // Setup ticks
                    true,                            // Required to pass?
                    // The actual lambda containing your test logic (uses GameTestHelper)
                    helper -> runParameterizedDeathTest(helper, keepHotbar)
            ));
        }

        return tests;
    }

    // The core test logic executed by the generator
    private void runParameterizedDeathTest(GameTestHelper helper, boolean keepHotbarConfig) {
        // 1. Apply the parametrized configuration
        ModConfig.saveHotbar = keepHotbarConfig;

        // Force vanilla keepInventory to true
        helper.getLevel().getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, helper.getLevel().getServer());

        // 2. Spawn original player (No cast needed!)
        net.minecraft.world.entity.player.Player originalPlayer = helper.makeMockSurvivalPlayer();
        giveUniversalItems(originalPlayer);

        // 3. Kill the player
        originalPlayer.hurt(helper.getLevel().damageSources().generic(), 1000.0f);

        // 4. Assertions (Check the originalPlayer directly!)
        if (keepHotbarConfig) {
            if (!originalPlayer.getInventory().getItem(0).is(Items.DIAMOND_SWORD)) {
                helper.fail("Player should have kept the Diamond Sword in hotbar!");
            }
        } else {
            if (!originalPlayer.getInventory().getItem(0).isEmpty()) {
                helper.fail("Player should NOT have kept the Diamond Sword in hotbar!");
            }

            // Create a search box 10 blocks in all directions around where the player died
            AABB searchBounds = originalPlayer.getBoundingBox().inflate(10.0D);

            // Look for dropped items in that search box
            boolean droppedSword = false;
            for (ItemEntity item : helper.getLevel().getEntitiesOfClass(ItemEntity.class, searchBounds, e -> true)) {
                if (item.getItem().is(Items.DIAMOND_SWORD)) droppedSword = true;
            }
            if (!droppedSword) {
                helper.fail("Diamond Sword should have dropped in the world!");
            }
        }

        if (!originalPlayer.getInventory().getItem(39).isEmpty()) {
            helper.fail("Helmet should have been dropped regardless of hotbar config!");
        }

        // Test passes successfully
        helper.succeed();
    }
}
