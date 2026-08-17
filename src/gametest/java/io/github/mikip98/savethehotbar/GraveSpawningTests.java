package io.github.mikip98.savethehotbar;

import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.config.enums.ContainDropMode;
import io.github.mikip98.savethehotbar.content.blockentities.GraveContainerBlockEntity;
#if MC_VERSION < 12108
import io.github.mikip98.savethehotbar.registries.BlockRegistry;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest; #endif
#if MC_VERSION >= 12108 import net.fabricmc.fabric.api.gametest.v1.GameTest; #endif
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
#if MC_VERSION < 12108 import net.minecraft.gametest.framework.GameTestGenerator; #endif
#if MC_VERSION < 12108 import net.minecraft.gametest.framework.GameTest; #endif
import net.minecraft.gametest.framework.GameTestHelper;
#if MC_VERSION < 12108 import net.minecraft.gametest.framework.TestFunction; #endif
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
#if MC_VERSION >= 12006 import net.minecraft.world.level.GameType; #endif
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.function.Function;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class GraveSpawningTests #if MC_VERSION < 12108 implements FabricGameTest #endif {
    protected static final short OVERWORLD_TOP_BUILD_LIMIT = 319;
    protected static final short OVERWORLD_BOTTOM_BUILD_LIMIT = -64;

    protected static final short NETHER_TOP_BUILD_LIMIT = 255;
    protected static final short NETHER_BOTTOM_BUILD_LIMIT = 0;

    protected static final short END_TOP_BUILD_LIMIT = 255;
    protected static final short END_BOTTOM_BUILD_LIMIT = 0;

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
        LOGGER.warn("Running clearTestArea");

        final int maxBuildHeight = level.avlGetTopBuildLimit();
        final int minBuildHeight = level.avlGetBottomBuildLimit();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    int targetY = center.getY() + y;

                    if (targetY < minBuildHeight || targetY > maxBuildHeight) {
                        continue;
                    }

                    BlockPos targetPos = center.offset(x, y, z);
                    BlockState targetState = level.getBlockState(targetPos);

                    if (!targetState.isAir() && !targetState.is(Blocks.STRUCTURE_BLOCK)) {
                        level.removeBlock(targetPos, false);
                    }
                }
            }
        }
        LOGGER.warn("Finished running clearTestArea");
    }

    static final ModConfig configSackRad0 = getDropEverythingSackConfig();
    static final ModConfig configSackRad1 = getDropEverythingSackConfig();
    static final ModConfig configSackRad7 = getDropEverythingSackConfig();
    static {
        configSackRad0.dropControl.graveSpawningLogic.sackMaxSpawnRadius = 0;
        configSackRad1.dropControl.graveSpawningLogic.sackMaxSpawnRadius = 1;
        configSackRad7.dropControl.graveSpawningLogic.sackMaxSpawnRadius = 7;
    }


    // --- RADIUS 0 ---
    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius0BelowBottomLimit(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad0, level -> level.avlGetBottomBuildLimit() - 32, OVERWORLD_BOTTOM_BUILD_LIMIT + 1
        );
    }

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius0BelowBottomLimitMinus1(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad0, level -> level.avlGetBottomBuildLimit() - 1, OVERWORLD_BOTTOM_BUILD_LIMIT + 1
        );
    }

//    @SuppressWarnings("unused")
//    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
//    public void testGraveSpawningSackRadius0AtBottomLimit(GameTestHelper helper) {
//        runParameterizedSackTest(
//                helper, configSackRad0, Level::avlGetBottomBuildLimit, OVERWORLD_BOTTOM_BUILD_LIMIT + 1
//        );
//    }
    // TODO: Uncomment, fix, and report to Manifold that Extension class method reference crashes the compiler

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius0AtBottomLimitPlus1(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad0, level -> level.avlGetBottomBuildLimit() + 1, OVERWORLD_BOTTOM_BUILD_LIMIT + 1
        );
    }

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius0NormalHeight(GameTestHelper helper) {
        runParameterizedSackTest(helper, configSackRad0, level -> 63, 63);
    }

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius0AtTopLimitMinus1(GameTestHelper helper) {
        runParameterizedSackTest(helper, configSackRad0, level -> level.avlGetTopBuildLimit() - 1, OVERWORLD_TOP_BUILD_LIMIT - 1);
    }

//    @SuppressWarnings("unused")
//    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
//    public void testGraveSpawningSackRadius0AtTopLimit(GameTestHelper helper) {
//        runParameterizedSackTest(helper, configSackRad0, Level::avlGetTopBuildLimit, OVERWORLD_TOP_BUILD_LIMIT);
//    }
    // TODO: Uncomment, fix, and report to Manifold that Extension class method reference crashes the compiler

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius0AtTopLimitPlus1(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad0, level -> level.avlGetTopBuildLimit() + 1, OVERWORLD_TOP_BUILD_LIMIT
        );
    }

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius0AboveTopLimit(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad0, level -> level.avlGetTopBuildLimit() + 32, OVERWORLD_TOP_BUILD_LIMIT
        );
    }


    // --- RADIUS 1 ---
    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius1BelowBottomLimit(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad1, level -> level.avlGetBottomBuildLimit() - 32, OVERWORLD_BOTTOM_BUILD_LIMIT + 1
        );
    }

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius1AtBottomLimitMinus1(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad1, level -> level.avlGetBottomBuildLimit() - 1, OVERWORLD_BOTTOM_BUILD_LIMIT + 1
        );
    }

//    @SuppressWarnings("unused")
//    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
//    public void testGraveSpawningSackRadius1AtBottomLimit(GameTestHelper helper) {
//        runParameterizedSackTest(
//                helper, configSackRad1, Level::avlGetBottomBuildLimit, OVERWORLD_BOTTOM_BUILD_LIMIT + 1
//        );
//    }
    // TODO: Uncomment, fix, and report to Manifold that Extension class method reference crashes the compiler

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius1AtBottomLimitPlus1(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad1, level -> level.avlGetBottomBuildLimit() + 1, OVERWORLD_BOTTOM_BUILD_LIMIT + 1
        );
    }

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius1AtTopLimitMinus1(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad1, level -> level.avlGetTopBuildLimit() - 1, OVERWORLD_TOP_BUILD_LIMIT - 1
        );
    }

//    @SuppressWarnings("unused")
//    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
//    public void testGraveSpawningSackRadius1AtTopLimit(GameTestHelper helper) {
//        runParameterizedSackTest(
//                helper, configSackRad1, Level::avlGetTopBuildLimit, OVERWORLD_TOP_BUILD_LIMIT
//        );
//    }
    // TODO: Uncomment, fix, and report to Manifold that Extension class method reference crashes the compiler

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius1AtTopLimitPlus1(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad1, level -> level.avlGetTopBuildLimit() + 1, OVERWORLD_TOP_BUILD_LIMIT
        );
    }

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius1AboveTopLimit(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad1, level -> level.avlGetTopBuildLimit() + 32, OVERWORLD_TOP_BUILD_LIMIT
        );
    }


    // --- RADIUS 7 ---
    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius7BelowBottomLimit(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad7, level -> level.avlGetBottomBuildLimit() - 32, OVERWORLD_BOTTOM_BUILD_LIMIT + 1
        );
    }

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius7AtBottomLimitMinus1(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad7, level -> level.avlGetBottomBuildLimit() - 1, OVERWORLD_BOTTOM_BUILD_LIMIT + 1
        );
    }

//    @SuppressWarnings("unused")
//    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
//    public void testGraveSpawningSackRadius7AtBottomLimit(GameTestHelper helper) {
//        runParameterizedSackTest(
//                helper, configSackRad7, Level::avlGetBottomBuildLimit, OVERWORLD_BOTTOM_BUILD_LIMIT + 1
//        );
//    }
    // TODO: Uncomment, fix, and report to Manifold that Extension class method reference crashes the compiler

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius7AtBottomLimitPlus1(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad7, level -> level.avlGetBottomBuildLimit() + 1, OVERWORLD_BOTTOM_BUILD_LIMIT + 1
        );
    }

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius7AtTopLimitMinus1(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad7, level -> level.avlGetTopBuildLimit() - 1, OVERWORLD_TOP_BUILD_LIMIT - 1
        );
    }

//    @SuppressWarnings("unused")
//    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
//    public void testGraveSpawningSackRadius7AtTopLimit(GameTestHelper helper) {
//        runParameterizedSackTest(
//                helper, configSackRad7, Level::avlGetTopBuildLimit, OVERWORLD_TOP_BUILD_LIMIT
//        );
//    }
    // TODO: Uncomment, fix, and report to Manifold that Extension class method reference crashes the compiler

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius7AtTopLimitPlus1(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad7, level -> level.avlGetTopBuildLimit() + 1, OVERWORLD_TOP_BUILD_LIMIT
        );
    }

    @SuppressWarnings("unused")
    @GameTest(#if MC_VERSION < 12108 batch = "save_the_hotbar_test_grave_spawning", template = FabricGameTest.EMPTY_STRUCTURE #endif)
    public void testGraveSpawningSackRadius7AboveTopLimit(GameTestHelper helper) {
        runParameterizedSackTest(
                helper, configSackRad7, level -> level.avlGetTopBuildLimit() + 32, OVERWORLD_TOP_BUILD_LIMIT
        );
    }
    // TODO: Finish tests


    private void runParameterizedSackTest(GameTestHelper helper, ModConfig config, Function<ServerLevel, Integer> deathYResolver, int expectedY) {
        ModConfig.INSTANCE = config;

        final ServerLevel level = helper.getLevel();

        final int deathY = deathYResolver.apply(level);

        final BlockPos basePos = helper.absolutePos(new BlockPos(32, 32, 32));

        final BlockPos deathPos = new BlockPos(basePos.getX(), deathY, basePos.getZ());
        final BlockPos expectedBlockPos = new BlockPos(basePos.getX(), expectedY, basePos.getZ());

        clearTestArea(level, expectedBlockPos, 15);

        simulateDeath(helper, level, deathPos, expectedBlockPos);

        helper.succeed();
    }


    @FunctionalInterface
    private interface StructureBuilder {
        void build(ServerLevel level, BlockPos center);
    }

    private record PriorityTestEntry(ModConfig config, int clearRadius, BlockPos expectedOffset, StructureBuilder structureBuilder) {}

    private void fillCube(ServerLevel level, BlockPos center, int radius, Block block) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    level.setBlock(center.offset(x, y, z), block.defaultBlockState(), 3);
                }
            }
        }
    }

    #if MC_VERSION < 12004
    static final Block SHORT_GRASS = Blocks.GRASS;
    #else
    static final Block SHORT_GRASS = Blocks.SHORT_GRASS;
    #endif

    // TODO: Finish porting to 1.21.8+
    #if MC_VERSION < 12108
    @SuppressWarnings("unused")
    @GameTestGenerator
    public Collection<TestFunction> generateSackPriorityTests() {
        final String batchName = "save_the_hotbar_test_grave_priority";
        final String testPrefix = "test_priority_";

        ModConfig configSackRad1 = getDropEverythingSackConfig();
        configSackRad1.dropControl.graveSpawningLogic.sackMaxSpawnRadius = 1;

        ModConfig configSackRad2 = getDropEverythingSackConfig();
        configSackRad2.dropControl.graveSpawningLogic.sackMaxSpawnRadius = 2;

        // Remember that the spawning radius should be lower than 32, else structure block can be overwritten

        final Map<String, PriorityTestEntry> testEntries = Map.ofEntries(
                // --- RADIUS 1 ---
                Map.entry("rad1_3x3_bedrock_cube", new PriorityTestEntry(
                        configSackRad1, 3, BlockPos.ZERO, // Expect center (0,0,0)
                        (level, center) -> fillCube(level, center, 1, Blocks.BEDROCK)
                )),
                Map.entry("rad1_3x3_bedrock_1_stone", new PriorityTestEntry(
                        configSackRad1, 3, new BlockPos(1, 1, 1), // Expect offset (1,1,1)
                        (level, center) -> {
                            fillCube(level, center, 1, Blocks.BEDROCK);
                            level.setBlock(center.offset(1, 1, 1), Blocks.STONE.defaultBlockState(), 3);
                        }
                )),
                Map.entry("rad1_3x3_bedrock_1_grass", new PriorityTestEntry(
                        configSackRad1, 3, new BlockPos(1, 1, 1), // Expect offset (1,1,1)
                        (level, center) -> {
                            fillCube(level, center, 1, Blocks.BEDROCK);
                            level.setBlock(center.offset(1, 1, 1), SHORT_GRASS.defaultBlockState(), 3);
                        }
                )),
                Map.entry("rad1_3x3_bedrock_stone_and_grass", new PriorityTestEntry(
                        configSackRad1, 3, new BlockPos(1, 1, 1), // Expect offset (1,1,1) due to replaceable priority
                        (level, center) -> {
                            fillCube(level, center, 1, Blocks.BEDROCK);
                            level.setBlock(center.offset(1, 1, 1), SHORT_GRASS.defaultBlockState(), 3); // Replaceable
                            level.setBlock(center.offset(-1, -1, -1), Blocks.STONE.defaultBlockState(), 3); // Destructible
                        }
                )),
                Map.entry("rad1_3x3_lava_pool", new PriorityTestEntry(
                        configSackRad1, 3, BlockPos.ZERO, // Expect center (0,0,0) replacing lava
                        (level, center) -> fillCube(level, center, 1, Blocks.LAVA)
                )),

                // --- RADIUS 2 ---
                Map.entry("rad2_5x5_stone_1_grass", new PriorityTestEntry(
                        configSackRad2, 5, new BlockPos(2, 2, 2), // Expect offset (2,2,2) because replaceable > distance
                        (level, center) -> {
                            fillCube(level, center, 2, Blocks.STONE);
                            level.setBlock(center.offset(2, 2, 2), SHORT_GRASS.defaultBlockState(), 3);
                        }
                )),
                Map.entry("rad2_5x5_bedrock_2_stone_dist", new PriorityTestEntry(
                        configSackRad2, 5, new BlockPos(1, 0, 0), // Expect offset (1,0,0) because it is closer than (2,0,0)
                        (level, center) -> {
                            fillCube(level, center, 2, Blocks.BEDROCK);
                            level.setBlock(center.offset(1, 0, 0), Blocks.STONE.defaultBlockState(), 3); // Closer
                            level.setBlock(center.offset(2, 0, 0), Blocks.STONE.defaultBlockState(), 3); // Further
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
    #endif

    private void runPriorityTest(GameTestHelper helper, PriorityTestEntry params) {
        ModConfig.INSTANCE = params.config();
        final ServerLevel level = helper.getLevel();

        final BlockPos basePos = helper.absolutePos(new BlockPos(32, 32, 32));

        clearTestArea(level, basePos, params.clearRadius());
        params.structureBuilder().build(level, basePos);

        final BlockPos expectedPos = basePos.offset(params.expectedOffset());

        simulateDeath(helper, level, basePos, expectedPos);

        clearTestArea(level, basePos, params.clearRadius());
        helper.succeed();
    }



    private void simulateDeath(GameTestHelper helper, ServerLevel level, BlockPos blockPos, BlockPos expectedPos) {
        LOGGER.warn("Running simulateDeath");
        #if MC_VERSION < 12006
        final Player player = helper.makeMockSurvivalPlayer();
        #else
        final Player player = helper.makeMockPlayer(GameType.SURVIVAL);  // TODO: Check if this can be used before 1.20.6
        #endif
        player.setNoGravity(true);
        player.setPos(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);

        // Give an item to the player to trigger grave spawn on death
        player.getInventory().add(new ItemStack(Items.DIAMOND));

        player.hurt(level.damageSources().generic(), 1000.0f);

        final BlockState blockState = level.getBlockState(expectedPos);
        helper.assertTrue(
                blockState.is(BlockRegistry.SACK),
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

        // Clean up the world manually after manipulating it outside the structure
        graveContainerEntity.getItems().clear();
        level.removeBlock(expectedPos, false);
    }
}
