package io.github.mikip98.savethehotbar;

import com.mojang.authlib.GameProfile;
import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.config.enums.ContainDropMode;
import io.github.mikip98.savethehotbar.content.blockentities.GraveContainerBlockEntity;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.function.Function;

public class GraveSpawningTests implements FabricGameTest {
    protected static ModConfig getDropEverythingSackConfig() {
        ModConfig config = new ModConfig();

        config.slotControl.saveHotbar = false;
        config.slotControl.saveArmor = false;
        config.slotControl.saveSecondHand = false;
        config.slotControl.saveMainInventory = false;
        config.slotControl.moddedSlotsSettings.saveArsenal = false;

        config.dropControl.containDrop = true;
        config.dropControl.containDropMode = ContainDropMode.SACK;

        return config;
    }



    private void clearTestArea(ServerLevel level, BlockPos center, int radius) {
        final int minBuildHeight = level.getMinBuildHeight();
        final int maxBuildHeight = level.getMaxBuildHeight() - 1;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    int targetY = center.getY() + y;

                    if (targetY < minBuildHeight || targetY > maxBuildHeight) {
                        continue;
                    }

                    BlockPos targetPos = center.offset(x, y, z);
                    if (!level.getBlockState(targetPos).isAir()) {
                        level.removeBlock(targetPos, false);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @GameTestGenerator
    public Collection<TestFunction> generateSackBuildLimitTests() {
        final String batchName = "save_the_hotbar_test_grave_spawning";
        final String testPrefix = "test_grave_spawning_";

        ModConfig configSackRad0 = getDropEverythingSackConfig();
        configSackRad0.dropControl.graveSpawningLogic.sackMaxSpawnRadius = 0;

        ModConfig configSackRad1 = getDropEverythingSackConfig();
        configSackRad1.dropControl.graveSpawningLogic.sackMaxSpawnRadius = 1;

        ModConfig configSackRad7 = getDropEverythingSackConfig();
        configSackRad7.dropControl.graveSpawningLogic.sackMaxSpawnRadius = 7;

        final Map<String, TestEntry> testEntries = Map.<String, TestEntry>ofEntries(
                // --- RADIUS 0 ---
                Map.entry("sack_radius_0_below_bottom_limit", new TestEntry(
                        configSackRad0,level -> level.getMinBuildHeight() - 32, level -> level.getMinBuildHeight() + 1
                )),
                Map.entry("sack_radius_0_at_bottom_limit", new TestEntry(
                        configSackRad0, LevelReader::getMinBuildHeight, level -> level.getMinBuildHeight() + 1
                )),
                Map.entry("sack_radius_0_at_bottom_limit_plus_1", new TestEntry(
                        configSackRad0, level -> level.getMinBuildHeight() + 1, level -> level.getMinBuildHeight() + 1
                )),
                Map.entry("sack_radius_0_normal_height", new TestEntry(
                        configSackRad0, level -> 63, level -> 63
                )),
                Map.entry("sack_radius_0_at_top_limit_minus_1", new TestEntry(
                        configSackRad0, level -> level.getMaxBuildHeight() - 1, level -> level.getMaxBuildHeight() - 1
                )),
                Map.entry("sack_radius_0_at_top_limit", new TestEntry(
                        configSackRad0, LevelHeightAccessor::getMaxBuildHeight, level -> level.getMaxBuildHeight() - 1
                )),
                Map.entry("sack_radius_0_above_top_limit", new TestEntry(
                        configSackRad0, level -> level.getMaxBuildHeight() + 32, level -> level.getMaxBuildHeight() - 1
                )),

                // --- RADIUS 1 ---
                Map.entry("sack_radius_1_below_bottom_limit", new TestEntry(
                        configSackRad1, level -> level.getMinBuildHeight() - 32, level -> level.getMinBuildHeight() + 1
                )),
                Map.entry("sack_radius_1_at_bottom_limit", new TestEntry(
                        configSackRad1, LevelReader::getMinBuildHeight, level -> level.getMinBuildHeight() + 1
                )),
                Map.entry("sack_radius_1_at_bottom_limit_plus_1", new TestEntry(
                        configSackRad1, level -> level.getMinBuildHeight() + 1, level -> level.getMinBuildHeight() + 1
                )),
                Map.entry("sack_radius_1_at_top_limit_minus_1", new TestEntry(
                        configSackRad1, level -> level.getMaxBuildHeight() - 1, level -> level.getMaxBuildHeight() - 1
                )),
                Map.entry("sack_radius_1_at_top_limit", new TestEntry(
                        configSackRad1, LevelHeightAccessor::getMaxBuildHeight, level -> level.getMaxBuildHeight() - 1
                )),
                Map.entry("sack_radius_1_above_top_limit", new TestEntry(
                        configSackRad1, level -> level.getMaxBuildHeight() + 32, level -> level.getMaxBuildHeight() - 1
                )),

                // --- RADIUS 7 ---
                Map.entry("sack_radius_7_below_bottom_limit", new TestEntry(
                        configSackRad7, level -> level.getMinBuildHeight() - 32, level -> level.getMinBuildHeight() + 1
                )),
                Map.entry("sack_radius_7_at_bottom_limit", new TestEntry(
                        configSackRad7, LevelReader::getMinBuildHeight, level -> level.getMinBuildHeight() + 1
                )),
                Map.entry("sack_radius_7_at_bottom_limit_plus_1", new TestEntry(
                        configSackRad7, level -> level.getMinBuildHeight() + 1, level -> level.getMinBuildHeight() + 1
                )),
                Map.entry("sack_radius_7_at_top_limit_minus_1", new TestEntry(
                        configSackRad7, level -> level.getMaxBuildHeight() - 1, level -> level.getMaxBuildHeight() - 1
                )),
                Map.entry("sack_radius_7_at_top_limit", new TestEntry(
                        configSackRad7, LevelHeightAccessor::getMaxBuildHeight, level -> level.getMaxBuildHeight() - 1
                )),
                Map.entry("sack_radius_7_above_top_limit", new TestEntry(
                        configSackRad7, level -> level.getMaxBuildHeight() + 32, level -> level.getMaxBuildHeight() - 1
                ))

                // TODO: Finish tests
        );

        List<TestFunction> tests = new ArrayList<>(testEntries.size());
        for (Map.Entry<String, TestEntry> entry : testEntries.entrySet()) {
            final String testName = testPrefix + entry.getKey();
            tests.add(new TestFunction(
                    batchName,                       // Batch ID
                    testName,                        // Unique Test Name
                    FabricGameTest.EMPTY_STRUCTURE,  // Structure Template
                    Rotation.NONE,                   // Rotation
                    100,                             // Max ticks before failure
                    0L,                              // Setup ticks
                    true,                            // Required to pass
                    helper -> runParameterizedSackTest(helper, entry.getValue())
            ));
        }
        return tests;
    }

    private void runParameterizedSackTest(GameTestHelper helper, TestEntry testParams) {
        ModConfig.INSTANCE = testParams.config();

        final ServerLevel level = helper.getLevel();

        final int deathY = testParams.deathYResolver().apply(level);
        final int expectedY = testParams.expectedYResolver().apply(level);

        final BlockPos basePos = helper.absolutePos(BlockPos.ZERO);
        final BlockPos deathPos = new BlockPos(basePos.getX(), deathY, basePos.getZ());
        final BlockPos expectedBlockPos = new BlockPos(basePos.getX(), expectedY, basePos.getZ());

        clearTestArea(level, expectedBlockPos, 15);

        simulateDeath(helper, level, deathPos, expectedBlockPos);

        // Clean up the world manually after manipulating it outside the structure
        level.removeBlock(expectedBlockPos, false);
        helper.succeed();
    }

    private record TestEntry(ModConfig config, Function<ServerLevel, Integer> deathYResolver, Function<ServerLevel, Integer> expectedYResolver) {}



    @FunctionalInterface
    private interface StructureBuilder {
        void build(ServerLevel level, BlockPos center);
    }

    private record PriorityTestEntry(ModConfig config, int clearRadius, BlockPos expectedOffset, StructureBuilder structureBuilder) {}

    private void fillCube(ServerLevel level, BlockPos center, int radius, net.minecraft.world.level.block.Block block) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    level.setBlock(center.offset(x, y, z), block.defaultBlockState(), 3);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @GameTestGenerator
    public Collection<TestFunction> generateSackPriorityTests() {
        final String batchName = "save_the_hotbar_test_grave_priority";
        final String testPrefix = "test_priority_";

        ModConfig configSackRad1 = getDropEverythingSackConfig();
        configSackRad1.dropControl.graveSpawningLogic.sackMaxSpawnRadius = 1;

        ModConfig configSackRad2 = getDropEverythingSackConfig();
        configSackRad2.dropControl.graveSpawningLogic.sackMaxSpawnRadius = 2;

        final Map<String, PriorityTestEntry> testEntries = Map.<String, PriorityTestEntry>ofEntries(
                // --- RADIUS 1 ---
                Map.entry("rad1_3x3_bedrock_cube", new PriorityTestEntry(
                        configSackRad1, 3, BlockPos.ZERO, // Expect center (0,0,0)
                        (level, center) -> fillCube(level, center, 1, net.minecraft.world.level.block.Blocks.BEDROCK)
                )),
                Map.entry("rad1_3x3_bedrock_1_stone", new PriorityTestEntry(
                        configSackRad1, 3, new BlockPos(1, 1, 1), // Expect offset (1,1,1)
                        (level, center) -> {
                            fillCube(level, center, 1, net.minecraft.world.level.block.Blocks.BEDROCK);
                            level.setBlock(center.offset(1, 1, 1), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), 3);
                        }
                )),
                Map.entry("rad1_3x3_bedrock_1_grass", new PriorityTestEntry(
                        configSackRad1, 3, new BlockPos(1, 1, 1), // Expect offset (1,1,1)
                        (level, center) -> {
                            fillCube(level, center, 1, net.minecraft.world.level.block.Blocks.BEDROCK);
                            level.setBlock(center.offset(1, 1, 1), net.minecraft.world.level.block.Blocks.GRASS.defaultBlockState(), 3);
                        }
                )),
                Map.entry("rad1_3x3_bedrock_stone_and_grass", new PriorityTestEntry(
                        configSackRad1, 3, new BlockPos(1, 1, 1), // Expect offset (1,1,1) due to replaceable priority
                        (level, center) -> {
                            fillCube(level, center, 1, net.minecraft.world.level.block.Blocks.BEDROCK);
                            level.setBlock(center.offset(1, 1, 1), net.minecraft.world.level.block.Blocks.GRASS.defaultBlockState(), 3); // Replaceable
                            level.setBlock(center.offset(-1, -1, -1), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), 3); // Destructible
                        }
                )),
                Map.entry("rad1_3x3_lava_pool", new PriorityTestEntry(
                        configSackRad1, 3, BlockPos.ZERO, // Expect center (0,0,0) replacing lava
                        (level, center) -> fillCube(level, center, 1, net.minecraft.world.level.block.Blocks.LAVA)
                )),

                // --- RADIUS 2 ---
                Map.entry("rad2_5x5_stone_1_grass", new PriorityTestEntry(
                        configSackRad2, 5, new BlockPos(2, 2, 2), // Expect offset (2,2,2) because replaceable > distance
                        (level, center) -> {
                            fillCube(level, center, 2, net.minecraft.world.level.block.Blocks.STONE);
                            level.setBlock(center.offset(2, 2, 2), net.minecraft.world.level.block.Blocks.GRASS.defaultBlockState(), 3);
                        }
                )),
                Map.entry("rad2_5x5_bedrock_2_stone_dist", new PriorityTestEntry(
                        configSackRad2, 5, new BlockPos(1, 0, 0), // Expect offset (1,0,0) because it is closer than (2,0,0)
                        (level, center) -> {
                            fillCube(level, center, 2, net.minecraft.world.level.block.Blocks.BEDROCK);
                            level.setBlock(center.offset(1, 0, 0), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), 3); // Closer
                            level.setBlock(center.offset(2, 0, 0), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), 3); // Further
                        }
                ))
        );

        List<TestFunction> tests = new ArrayList<>(testEntries.size());
        for (Map.Entry<String, PriorityTestEntry> entry : testEntries.entrySet()) {
            tests.add(new TestFunction(
                    batchName,
                    testPrefix + entry.getKey(),
                    FabricGameTest.EMPTY_STRUCTURE,
                    Rotation.NONE,
                    100,
                    0L,
                    true,
                    helper -> runPriorityTest(helper, entry.getValue())
            ));
        }
        return tests;
    }

    private void runPriorityTest(GameTestHelper helper, PriorityTestEntry params) {
        ModConfig.INSTANCE = params.config();
        final ServerLevel level = helper.getLevel();

        final BlockPos basePos = helper.absolutePos(new BlockPos(0, 100, 0));

        clearTestArea(level, basePos, params.clearRadius());
        params.structureBuilder().build(level, basePos);

        final BlockPos expectedPos = basePos.offset(params.expectedOffset());

        simulateDeath(helper, level, basePos, expectedPos);

        clearTestArea(level, basePos, params.clearRadius());
        helper.succeed();
    }



    private void simulateDeath(GameTestHelper helper, ServerLevel level, BlockPos blockPos, BlockPos expectedPos) {
        final Player player = helper.makeMockSurvivalPlayer();
        player.setNoGravity(true);
        player.setPos(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);

        // Give an item to the player to trigger grave spawn on death
        player.getInventory().add(new ItemStack(Items.DIAMOND));

        player.hurt(level.damageSources().generic(), 1000.0f);

        final BlockState blockState = level.getBlockState(expectedPos);
        helper.assertTrue(
                blockState.is(SaveTheHotbar.SACK),
                "Sack did NOT spawn at expected pos: " + expectedPos + ", block found: " + blockState.getBlock().getName().getString()
        );

        final BlockEntity blockEntity = level.getBlockEntity(expectedPos);
        helper.assertTrue(
                blockEntity instanceof GraveContainerBlockEntity,
                "Block Entity at " + expectedPos + " is missing or is not a GraveContainerBlockEntity."
        );
        assert blockEntity instanceof GraveContainerBlockEntity;
        final GraveContainerBlockEntity graveContainerEntity = (GraveContainerBlockEntity) blockEntity;
        final NonNullList<ItemStack> drop = graveContainerEntity.getItems();

        helper.assertTrue(
                drop.stream().anyMatch((itemStack) -> ItemStack.isSameItem(itemStack, new ItemStack(Items.DIAMOND))),
                "The original diamond item for grave trigger creation is missing, drop: " + drop
        );
        // TODO: Check if the the non-replaceable destructible block if such was replaced is inside
    }
}
