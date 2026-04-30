package io.github.mikip98.savethehotbar;

import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.config.enums.LogicOperator;
import io.github.mikip98.savethehotbar.config.enums.OverlapResolution;
import io.github.mikip98.savethehotbar.config.enums.itemTypes.VanillaItemTypes;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeathBehaviorTests implements FabricGameTest {
    // =================================================================================================================
    // === COMMON INVENTORY SETUP ======================================================================================
    // =================================================================================================================
    protected void giveUniversalItems(Player player) {
        Inventory inventory = player.getInventory();
        giveUniversalInventory(new InventoryWrapper(inventory.items));
        giveUniversalHotbar(new HotbarWrapper(inventory.items));
        giveUniversalArmour(new ArmourWrapper(inventory.armor));
        giveUniversalOffHand(new LeftHandWrapper(inventory.offhand));
    }

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
        inventory.add(Items.GRASS, 32);  // OTHER (block)
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
    protected static void defaultConfig() {
        ModConfig.INSTANCE = new ModConfig();
    }
    // -----------------------------------------------------------------------------------------------------------------
    protected static void saveOnlyHotbar() {
        ModConfig.INSTANCE.saveArmor = false;
        ModConfig.INSTANCE.saveSecondHand = false;
    }
    protected static void saveOnlyMainInventory() {
        ModConfig.INSTANCE.saveArmor = false;
        ModConfig.INSTANCE.saveHotbar = false;
        ModConfig.INSTANCE.saveSecondHand = false;
        ModConfig.INSTANCE.saveMainInventory = true;
    }
    protected static void saveOnlyLightSources() {
        ModConfig.INSTANCE.saveMainInventory = true;
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.AMMUNITION, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.OTHER, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.ARMOUR, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.EQUIPMENT, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.FOOD, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.POTION, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.TOOL, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.WEAPON, false);
    }
    protected static void saveOtherAndHotbar() {
        ModConfig.INSTANCE.saveArmor = false;
        ModConfig.INSTANCE.saveSecondHand = false;
        ModConfig.INSTANCE.itemKeepingLogicOperator = LogicOperator.OR;
        ModConfig.INSTANCE.overlapResolution = OverlapResolution.STRICT;
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.AMMUNITION, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.OTHER, true);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.ARMOUR, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.EQUIPMENT, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.FOOD, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.POTION, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.TOOL, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.WEAPON, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.LIGHT_SOURCE_ON, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.POSSIBLE_LIGHT_SOURCE, false);
    }
    protected static void saveOtherInHotbar() {
        ModConfig.INSTANCE.saveArmor = false;
        ModConfig.INSTANCE.saveSecondHand = false;
        ModConfig.INSTANCE.overlapResolution = OverlapResolution.STRICT;
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.AMMUNITION, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.OTHER, true);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.ARMOUR, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.EQUIPMENT, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.FOOD, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.POTION, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.TOOL, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.WEAPON, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.LIGHT_SOURCE_ON, false);
        ModConfig.INSTANCE.vanillaItemTypesKeepingMap.put(VanillaItemTypes.POSSIBLE_LIGHT_SOURCE, false);
    }
    // =================================================================================================================

    // Parametrized Test Generator
    @SuppressWarnings("unused")
    @GameTestGenerator
    public Collection<TestFunction> generateKeepInventoryTests() {
        List<TestFunction> tests = new ArrayList<>();

        boolean[] keepHotbarConfigs = {true, false};

        for (boolean keepHotbar : keepHotbarConfigs) {
            String testName = "test_death_keep_hotbar_" + keepHotbar;

            tests.add(new TestFunction(
                    "save_the_hotbar_test_batch",    // Batch ID
                    testName,                        // Unique Test Name
                    FabricGameTest.EMPTY_STRUCTURE,  // Structure Template
                    Rotation.NONE,                   // Rotation
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
        ModConfig.INSTANCE.saveHotbar = keepHotbarConfig;

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
