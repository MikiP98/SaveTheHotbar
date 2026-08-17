package io.github.mikip98.savethehotbar;

import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.config.enums.ContainDropMode;
import io.github.mikip98.savethehotbar.config.enums.LogicOperator;
import io.github.mikip98.savethehotbar.config.enums.OverlapResolution;
import io.github.mikip98.savethehotbar.config.enums.ItemTypes;
import io.github.mikip98.savethehotbar.content.blockentities.GraveContainerBlockEntity;
#if MC_VERSION < 12108 import net.fabricmc.fabric.api.gametest.v1.FabricGameTest; #endif
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTestHelper;
#if MC_VERSION < 12108 import net.minecraft.gametest.framework.GameTestGenerator; #endif
#if MC_VERSION < 12108 import net.minecraft.gametest.framework.TestFunction; #endif
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemFilteringTests #if MC_VERSION < 12108 implements FabricGameTest #endif {
    #if MC_VERSION < 12004
    static final Item GRASS_BLOCK_ITEM = Items.GRASS;
    #else
    static final Item GRASS_BLOCK_ITEM = Items.GRASS_BLOCK;
    #endif

    // =================================================================================================================
    // === COMMON INVENTORY SETUP ======================================================================================
    // =================================================================================================================
    // TODO: Finish porting to 1.21.8+
    #if MC_VERSION < 12108
    protected void giveUniversalItems(Player player) {
        Inventory inventory = player.getInventory();
        giveUniversalInventory(new InventoryWrapper(inventory.items));
        giveUniversalHotbar(new HotbarWrapper(inventory.items));
        giveUniversalArmour(new ArmourWrapper(inventory.armor));
        giveUniversalOffHand(new LeftHandWrapper(inventory.offhand));
    }
    #endif

    protected void giveUniversalInventory(InventoryWrapper inventory) {
        inventory.add(Items.IRON_PICKAXE);   // TOOL
        inventory.add(Items.DIAMOND_SWORD);  // WEAPON (melee)
        inventory.add(Items.BOW);            // WEAPON (ranged)

        inventory.add(Items.GOLDEN_CHESTPLATE);  // ARMOUR
        inventory.add(Items.CARVED_PUMPKIN);     // EQUIPMENT
        inventory.add(Items.SKELETON_SKULL);     // EQUIPMENT

        inventory.add(Items.SPLASH_POTION);      // POTION
        inventory.add(Items.EXPERIENCE_BOTTLE);  // POTION

        inventory.add(Items.COOKED_BEEF);       // FOOD
        inventory.add(Items.TORCH);             // DEFAULT LIGHT SOURCE
        inventory.add(Items.REDSTONE_LAMP);     // POSSIBLE LIGHT SOURCE
        inventory.add(Items.SPECTRAL_ARROW);    // AMMUNITION

        inventory.add(Items.DIRT, 64);   // OTHER (block)
        inventory.add(GRASS_BLOCK_ITEM, 32);  // OTHER (block)
        inventory.add(Items.SUGAR_CANE);         // OTHER (item)
    }

    protected void giveUniversalHotbar(HotbarWrapper hotbar) {
        hotbar.add(Items.CROSSBOW);                  // WEAPON (ranged)
        hotbar.add(Items.DIAMOND_AXE);               // WEAPON (melee)
        hotbar.add(Items.GOLDEN_APPLE, 32);  // FOOD
        hotbar.add(Items.TORCH, 64);         // DEFAULT LIGHT SOURCE
        hotbar.add(Items.COBBLESTONE, 64);   // OTHER (block)
        hotbar.add(Items.COBBLESTONE, 64);   // OTHER (block)
    }

    protected void giveUniversalArmour(ArmourWrapper armour) {
        armour.setHelmet(Items.CARVED_PUMPKIN);  // EQUIPMENT
        armour.setBoots(Items.LEATHER_BOOTS);    // ARMOUR
    }

    protected void giveUniversalOffHand(LeftHandWrapper leftHand) {
        leftHand.setLeftHand(Items.SHIELD);  // TOOL
    }
    // =================================================================================================================

    // =================================================================================================================
    // === Configuration ===============================================================================================
    // =================================================================================================================
    protected static ModConfig saveOnlyHotbar() {
        ModConfig testConfig = new ModConfig();
        testConfig.slotControl.saveArmor = false;
        testConfig.slotControl.saveSecondHand = false;
        return testConfig;
    }
    protected static ModConfig saveOnlyMainInventory() {
        ModConfig testConfig = new ModConfig();
        testConfig.slotControl.saveArmor = false;
        testConfig.slotControl.saveHotbar = false;
        testConfig.slotControl.saveSecondHand = false;
        testConfig.slotControl.saveMainInventory = true;
        return testConfig;
    }
    protected static ModConfig saveOnlyLightSources() {
        ModConfig testConfig = new ModConfig();
        testConfig.slotControl.saveMainInventory = true;
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.AMMUNITION, false);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.OTHER, false);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.ARMOUR, false);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.EQUIPMENT, false);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.FOOD, false);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.POTION, false);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.TOOL, false);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.WEAPON, false);
        return testConfig;
    }
    protected static ModConfig saveOtherInHotbar() {
        ModConfig testConfig = saveOnlyHotbar();
        testConfig.itemTypeControl.overlapResolution = OverlapResolution.STRICT;
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.AMMUNITION, false);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.OTHER, true);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.ARMOUR, false);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.EQUIPMENT, false);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.FOOD, false);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.POTION, false);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.TOOL, false);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.WEAPON, false);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.LIGHT_SOURCE_ON, false);
        testConfig.itemTypeControl.itemTypesKeepingMap.put(ItemTypes.POSSIBLE_LIGHT_SOURCE, false);
        return testConfig;
    }
    protected static ModConfig saveOtherAndHotbar() {
        ModConfig testConfig = saveOtherInHotbar();
        testConfig.itemKeepingLogicOperator = LogicOperator.OR;
        return testConfig;
    }
    // TODO: Consider if the current test configs are enough or if more needs to be added
    // =================================================================================================================

    // TODO: Finish porting to 1.21.8+
    #if MC_VERSION < 12108
    // Parametrized Test Generator
    @SuppressWarnings("unused")
    @GameTestGenerator
    public Collection<TestFunction> generateKeepInventoryTests() {
        List<TestFunction> tests = new ArrayList<>();

        final String batchName = "save_the_hotbar_test_item_filtration";
        final String testPrefix = "test_item_filtration_";
        final TestEntry[] testEntries = getTestEntries();

        for (TestEntry entry : testEntries) {
            final String testName = testPrefix + entry.testName;
            tests.add(new TestFunction(
                    batchName,                       // Batch ID
                    testName,                        // Unique Test Name
                    FabricGameTest.EMPTY_STRUCTURE,  // Structure Template
                    Rotation.NONE,                   // Rotation
                    10,                              // Max ticks before failure (0.5s) TODO: Check what is the true min
                    0L,                              // Setup ticks
                    true,                            // Required to pass? TODO: Check why
                    // The actual lambda containing your test logic (uses GameTestHelper)
                    helper -> runParameterizedDeathTest(helper, entry.testConfig, entry.expectedResult)
            ));
        }

        return tests;
    }
    #endif

    private TestEntry[] getTestEntries() {

        final ItemStack[] empty = new ItemStack[0];
        final ItemStack shield = new ItemStack(Items.SHIELD);

        final ItemStack[] fullHotbar = new ItemStack[]{
                new ItemStack(Items.CROSSBOW),
                new ItemStack(Items.DIAMOND_AXE),
                new ItemStack(Items.GOLDEN_APPLE, 32),
                new ItemStack(Items.TORCH, 64),
                new ItemStack(Items.COBBLESTONE, 64),
                new ItemStack(Items.COBBLESTONE, 64)
        };

        final ItemStack[] fullMainInv = new ItemStack[]{
                new ItemStack(Items.IRON_PICKAXE),
                new ItemStack(Items.DIAMOND_SWORD),
                new ItemStack(Items.BOW),
                new ItemStack(Items.GOLDEN_CHESTPLATE),
                new ItemStack(Items.CARVED_PUMPKIN),
                new ItemStack(Items.SKELETON_SKULL),
                new ItemStack(Items.SPLASH_POTION),
                new ItemStack(Items.EXPERIENCE_BOTTLE),
                new ItemStack(Items.COOKED_BEEF),
                new ItemStack(Items.TORCH),
                new ItemStack(Items.REDSTONE_LAMP),
                new ItemStack(Items.SPECTRAL_ARROW),
                new ItemStack(Items.DIRT, 64),
                new ItemStack(GRASS_BLOCK_ITEM, 32),
                new ItemStack(Items.SUGAR_CANE)
        };

        final ItemStack[] fullArmor = new ItemStack[]{
                new ItemStack(Items.CARVED_PUMPKIN),
                new ItemStack(Items.LEATHER_BOOTS)
        };

        final ItemStack[] lightHotbarKept = new ItemStack[]{ new ItemStack(Items.TORCH, 64) };
        final ItemStack[] lightHotbarDropped = new ItemStack[]{
                new ItemStack(Items.CROSSBOW),
                new ItemStack(Items.DIAMOND_AXE),
                new ItemStack(Items.GOLDEN_APPLE, 32),
                new ItemStack(Items.COBBLESTONE, 64),
                new ItemStack(Items.COBBLESTONE, 64)
        };

        final ItemStack[] lightMainKept = new ItemStack[]{
                new ItemStack(Items.TORCH),
                new ItemStack(Items.REDSTONE_LAMP)
        };

        final ItemStack[] lightMainDropped = new ItemStack[]{
                new ItemStack(Items.IRON_PICKAXE),
                new ItemStack(Items.DIAMOND_SWORD),
                new ItemStack(Items.BOW),
                new ItemStack(Items.GOLDEN_CHESTPLATE),
                new ItemStack(Items.CARVED_PUMPKIN),
                new ItemStack(Items.SKELETON_SKULL),
                new ItemStack(Items.SPLASH_POTION),
                new ItemStack(Items.EXPERIENCE_BOTTLE),
                new ItemStack(Items.COOKED_BEEF),
                new ItemStack(Items.SPECTRAL_ARROW),
                new ItemStack(Items.DIRT, 64),
                new ItemStack(GRASS_BLOCK_ITEM, 32),
                new ItemStack(Items.SUGAR_CANE)
        };

        final ItemStack[] otherHotbarKept = new ItemStack[]{
                new ItemStack(Items.COBBLESTONE, 64),
                new ItemStack(Items.COBBLESTONE, 64)
        };

        final ItemStack[] otherHotbarDropped = new ItemStack[]{
                new ItemStack(Items.CROSSBOW),
                new ItemStack(Items.DIAMOND_AXE),
                new ItemStack(Items.GOLDEN_APPLE, 32),
                new ItemStack(Items.TORCH, 64)
        };

        final ItemStack[] otherMainKept = new ItemStack[]{
                new ItemStack(Items.DIRT, 64),
                new ItemStack(GRASS_BLOCK_ITEM, 32),
                new ItemStack(Items.SUGAR_CANE)
        };

        final ItemStack[] otherMainDropped = new ItemStack[]{
                new ItemStack(Items.IRON_PICKAXE),
                new ItemStack(Items.DIAMOND_SWORD),
                new ItemStack(Items.BOW),
                new ItemStack(Items.GOLDEN_CHESTPLATE),
                new ItemStack(Items.CARVED_PUMPKIN),
                new ItemStack(Items.SKELETON_SKULL),
                new ItemStack(Items.SPLASH_POTION),
                new ItemStack(Items.EXPERIENCE_BOTTLE),
                new ItemStack(Items.COOKED_BEEF),
                new ItemStack(Items.TORCH),
                new ItemStack(Items.REDSTONE_LAMP),
                new ItemStack(Items.SPECTRAL_ARROW)
        };


        return new TestEntry[]{
                new TestEntry(
                        "only_hotbar",
                        saveOnlyHotbar(),
                        new ExpectedItems(
                                new ItemsPerSlots(fullHotbar, empty, empty, ItemStack.EMPTY),
                                new ItemsPerSlots(empty, fullMainInv, fullArmor, shield)
                        )
                ),
                new TestEntry(
                        "only_main_inventory",
                        saveOnlyMainInventory(),
                        new ExpectedItems(
                                new ItemsPerSlots(empty, fullMainInv, empty, ItemStack.EMPTY),
                                new ItemsPerSlots(fullHotbar, empty, fullArmor, shield)
                        )
                ),
                new TestEntry(
                        "only_light_sources",
                        saveOnlyLightSources(),
                        new ExpectedItems(
                                new ItemsPerSlots(lightHotbarKept, lightMainKept, empty, ItemStack.EMPTY),
                                new ItemsPerSlots(lightHotbarDropped, lightMainDropped, fullArmor, shield)
                        )
                ),
                new TestEntry(
                        "other_in_hotbar",
                        saveOtherInHotbar(),
                        new ExpectedItems(
                                new ItemsPerSlots(otherHotbarKept, empty, empty, ItemStack.EMPTY),
                                new ItemsPerSlots(otherHotbarDropped, fullMainInv, fullArmor, shield)
                        )
                ),
                new TestEntry(
                        "other_and_hotbar",
                        saveOtherAndHotbar(),
                        new ExpectedItems(
                                new ItemsPerSlots(fullHotbar, otherMainKept, empty, ItemStack.EMPTY),
                                new ItemsPerSlots(empty, otherMainDropped, fullArmor, shield)
                        )
                )
        };
    }


    record TestEntry(String testName, ModConfig testConfig, ExpectedItems expectedResult) {}
    record ExpectedItems(ItemsPerSlots keptItems, ItemsPerSlots droppedItems) {}
    record ItemsPerSlots(ItemStack[] hotbarItems, ItemStack[] inventoryItems, ItemStack[] armourSlotsItems, ItemStack leftHandItem) {}

    // TODO: Finish porting to 1.21.8+
    #if MC_VERSION < 12108
    // The core test logic executed by the generator
    private void runParameterizedDeathTest(GameTestHelper helper, ModConfig testConfig, ExpectedItems expectedResult) {
        ModConfig.INSTANCE = testConfig;
        ModConfig.INSTANCE.dropControl.containDrop = true;
        ModConfig.INSTANCE.dropControl.containDropMode = ContainDropMode.SACK;
        ModConfig.INSTANCE.dropControl.graveSpawningLogic.sackMaxSpawnRadius = 0;

        #if MC_VERSION < 12006
        final Player player = helper.makeMockSurvivalPlayer();
        #else
        final Player player = helper.makeMockPlayer(GameType.SURVIVAL);  // TODO: Check if this can be used before 1.20.6
        #endif
        giveUniversalItems(player);

        final Level level = helper.getLevel();
        final BlockPos pos = player.blockPosition();

        player.hurt(level.damageSources().generic(), 1000.0f);

        final Inventory postDeathInventory = player.getInventory();

        final GraveContainerBlockEntity sackContainer = (GraveContainerBlockEntity) level.getBlockEntity(pos);
        assert sackContainer != null;
        final NonNullList<ItemStack> sackItems = sackContainer.getItems();

        // Hotbar
        for (ItemStack expectedKept : expectedResult.keptItems.hotbarItems) {
            boolean found = false;
            for (int i = 0; i < 9; ++i) {
                final ItemStack stack = postDeathInventory.items.get(i);
                if (ItemStack.matches(stack, expectedKept)) {
                    found = true;
                    stack.copyAndClear();
                    break;
                }
            }
            if (!found) helper.fail("Expected ItemStack '" + expectedKept + "' has not been found in the post death inventory (hotbar)");
        }
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = postDeathInventory.items.get(i);
            helper.assertTrue(
                    stack.isEmpty(),
                    "Non expected item found in post death (hotbar) inventory: " + stack
            );
        }
        for (ItemStack expectedDrop : expectedResult.droppedItems.hotbarItems) {
            boolean found = false;
            for (ItemStack drop : sackItems) {
                if (ItemStack.matches(drop, expectedDrop)) {
                    found = true;
                    drop.copyAndClear();
                    break;
                }
            }
            if (!found) helper.fail("Expected ItemStack '" + expectedDrop + "' (from hotbar) has not been found in the the drop");
        }

        // Inventory
        for (ItemStack expectedKept : expectedResult.keptItems.inventoryItems) {
            boolean found = false;
            for (int i = 9; i < 27 + 9; ++i) {
                final ItemStack stack = postDeathInventory.items.get(i);
                if (ItemStack.matches(stack, expectedKept)) {
                    found = true;
                    stack.copyAndClear();
                    break;
                }
            }
            if (!found) helper.fail("Expected ItemStack '" + expectedKept + "' has not been found in the post death inventory (main inventory)");
        }
        for (int i = 9; i < 27 + 9; ++i) {
            final ItemStack stack = postDeathInventory.items.get(i);
            helper.assertTrue(
                    stack.isEmpty(),
                    "Non expected item found in post death (main inventory) inventory: " + stack
            );
        }
        for (ItemStack expectedDrop : expectedResult.droppedItems.inventoryItems) {
            boolean found = false;
            for (ItemStack drop : sackItems) {
                if (ItemStack.matches(drop, expectedDrop)) {
                    found = true;
                    drop.copyAndClear();
                    break;
                }
            }
            if (!found) helper.fail("Expected ItemStack '" + expectedDrop + "' (from main inventory) has not been found in the the drop");
        }

        // Armour
        for (ItemStack expectedKept : expectedResult.keptItems.armourSlotsItems) {
            boolean found = false;
            for (ItemStack stack : postDeathInventory.armor) {
                if (ItemStack.matches(stack, expectedKept)) {
                    found = true;
                    stack.copyAndClear();
                    break;
                }
            }
            if (!found) helper.fail("Expected ItemStack '" + expectedKept + "' has not been found in the post death inventory (armour)");
        }
        for (ItemStack stack : postDeathInventory.armor) {
            helper.assertTrue(
                    stack.isEmpty(),
                    "Non expected item found in post death (armour) inventory: " + stack
            );
        }
        for (ItemStack expectedDrop : expectedResult.droppedItems.armourSlotsItems) {
            boolean found = false;
            for (ItemStack drop : sackItems) {
                if (ItemStack.matches(drop, expectedDrop)) {
                    found = true;
                    drop.copyAndClear();
                    break;
                }
            }
            if (!found) helper.fail("Expected ItemStack '" + expectedDrop + "' (from armour) has not been found in the the drop");
        }

        // Left Hand
        final ItemStack expectedLeftHandItem = expectedResult.keptItems.leftHandItem;
        final ItemStack leftHandItem = postDeathInventory.offhand.get(0);
        if (expectedLeftHandItem != null) {
            helper.assertTrue(ItemStack.matches(leftHandItem, expectedLeftHandItem), "Left hand item '" + leftHandItem + "' does not match the expected item '" + expectedLeftHandItem + "'");
        } else {
            helper.assertTrue(leftHandItem.isEmpty(), "Found unexpected left hand item: " + leftHandItem);
        }
        final ItemStack expectedLeftHandDrop = expectedResult.droppedItems.leftHandItem;
        if (expectedLeftHandDrop != null) {
            boolean found = false;
            for (ItemStack drop : sackItems) {
                if (ItemStack.matches(drop, expectedLeftHandDrop)) {
                    found = true;
                    drop.copyAndClear();
                    break;
                }
            }
            if (!found) helper.fail("Expected ItemStack '" + expectedLeftHandDrop + "' (from left hand) has not been found in the the drop");
        }

        // Last drop check
        for (ItemStack drop : sackItems) {
            helper.assertTrue(
                    drop.isEmpty(),
                    "Non expected item found in drop: " + drop
            );
        }

        helper.succeed();
    }
    #endif



    protected static class LimitedSizeWrapper {
        protected byte counter = 0;
        protected NonNullList<ItemStack> slots;
        protected byte size;
        protected byte offset;

        public LimitedSizeWrapper(NonNullList<ItemStack> slots, int size, int offset) {
            this.slots = slots;
            this.size = (byte) size;
            this.offset = (byte) offset;
        }
        protected void add(ItemStack stack) {
            if (counter >= size) throw new IndexOutOfBoundsException("Inventory is already full");
            slots.set(offset + counter++, stack);
        }
        public void add(Item item, int amount) {
            add(new ItemStack(item, amount));
        }
        public void add(Item item) {
            add(item, 1);
        }
    }
    protected static class InventoryWrapper extends LimitedSizeWrapper {
        public InventoryWrapper(NonNullList<ItemStack> items) {
            super(items, 27, 9);
        }
    }
    protected static class HotbarWrapper extends LimitedSizeWrapper {
        public HotbarWrapper(NonNullList<ItemStack> items) {
            super(items, 9, 0);
        }
    }
    protected static class ArmourWrapper {
        protected NonNullList<ItemStack> slots;
        public ArmourWrapper(NonNullList<ItemStack> slots) {
            this.slots = slots;
        }
        public void setHelmet(Item helmet) {
            setSlot((byte) 0, helmet);
        }
        @SuppressWarnings("unused")
        public void setChestplate(Item chestplate) {
            setSlot((byte) 1, chestplate);
        }
        @SuppressWarnings("unused")
        public void setLeggings(Item leggings) {
            setSlot((byte) 2, leggings);
        }
        public void setBoots(Item boots) {
            setSlot((byte) 3, boots);
        }
        protected void setSlot(byte slot, Item item) {
            slots.set(slot, new ItemStack(item));
        }
    }
    protected static class LeftHandWrapper {
        protected NonNullList<ItemStack> slots;
        public LeftHandWrapper(NonNullList<ItemStack> slots) {
            this.slots = slots;
        }
        public void setLeftHand(Item leftHand, int amount) {
            slots.set(0, new ItemStack(leftHand, amount));
        }
        public void setLeftHand(Item leftHand) {
            setLeftHand(leftHand, 1);
        }
    }
}
