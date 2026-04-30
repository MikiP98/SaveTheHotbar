package io.github.mikip98.savethehotbar.deathProcessing;

import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.config.enums.OverlapResolution;
import io.github.mikip98.savethehotbar.config.enums.itemTypes.VanillaItemTypes;
#if MC_VERSION == 12001
import io.github.mikip98.savethehotbar.deathProcessing.moddedSlotsHandlers.Arsenal;
#endif
import io.github.mikip98.savethehotbar.deathProcessing.moddedSlotsHandlers.SlotSupport;
import io.github.mikip98.savethehotbar.modDetection.SupportedSlotMods;
import io.github.mikip98.savethehotbar.registries.itemTypeRegistry.ItemTypeConfig;
import io.github.mikip98.savethehotbar.registries.itemTypeRegistry.ItemTypesConfiguration;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.core.NonNullList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class SlotHandler implements SlotSupport {
    protected Player player;
    protected Inventory inventory;

    public SlotHandler(Inventory inventory) {
        this.player = inventory.player;
        this.inventory = inventory;
    }

    // ------------ CHECK FOR NON-KEPT ITEMS ---------------------------------------------------------------------------
    public List<ItemStack> getNonKeptItems() {
        List<ItemStack> nonKeptItems = new ArrayList<>();
        LOGGER.info("Checking for vanilla non-kept items...");
        getVanillaNonKeptItems(nonKeptItems);
        LOGGER.info("Checking for modded non-kept items...");
        getModdedNonKeptItems(nonKeptItems);
        return nonKeptItems;
    }

    // ------------ Vanilla ------------
    protected void getVanillaNonKeptItems(List<ItemStack> nonKeptItems) {
        // Main -> Armor -> Offhand
        checkForDropHotbar(nonKeptItems, inventory.items);
        checkForDrop(nonKeptItems, ModConfig.saveArmor, inventory.armor);
        checkForDrop(nonKeptItems, ModConfig.saveSecondHand, inventory.offhand);
    }
    protected void checkForDropHotbar(List<ItemStack> drop, NonNullList<ItemStack> slots) {
        for (int i = 0; i < slots.size(); i++) {
            ItemStack stack = slots.get(i);
            final boolean shouldKeepHotbar = Inventory.isHotbarSlot(i) && ModConfig.saveHotbar;
            final boolean shouldKeepInventory = !Inventory.isHotbarSlot(i) && ModConfig.saveMainInventory;
            final boolean shouldKeep = shouldKeep(shouldKeepHotbar || shouldKeepInventory, shouldKeepItem(stack));
            if (!stack.isEmpty() && (!shouldKeep || shouldDropRandomly(stack))) {
                drop.add(slots.get(i).copyAndClear());
            }
        }
    }
    protected void checkForDrop(List<ItemStack> drop, boolean shouldKeep, NonNullList<ItemStack> slots) {
        for (ItemStack stack : slots) {
            if (shouldDrop(stack, shouldKeep)) {
                drop.add(stack.copyAndClear());
            }
        }
    }

    // ------------ Modded ------------
    protected void getModdedNonKeptItems(List<ItemStack> nonKeptItems) {
        // Keep Arsenal
        #if MC_VERSION == 12001
        if (SupportedSlotMods.ARSENAL.isLoaded())
            nonKeptItems.addAll(Arsenal.getItemsToDrop(player, this::shouldDrop));
        #endif
    }
    // -----------------------------------------------------------------------------------------------------------------

    protected boolean shouldDrop(ItemStack stack, boolean shouldKeepSlot) {
        return shouldDrop(stack, shouldKeepSlot, player);
    }
    public static boolean shouldDrop(ItemStack stack, boolean shouldKeepSlot, Player player) {
        return !stack.isEmpty() && (!shouldKeep(shouldKeepSlot, shouldKeepItem(stack)) || shouldDropRandomly(stack, player));
    }
    public static boolean shouldKeep(boolean shouldKeepSlot, boolean shouldKeepItemType) {
        return ModConfig.itemKeepingLogicOperator.apply(shouldKeepSlot, shouldKeepItemType);
    }
    public static boolean shouldKeepItem(ItemStack itemStack) {
        ArrayList<VanillaItemTypes> itemTypes = new ArrayList<>();
        for (VanillaItemTypes type : VanillaItemTypes.values()) {
            if (type == VanillaItemTypes.OTHER) continue;
            final ItemTypeConfig config = ItemTypesConfiguration.vanillaItemTypes.get(type);
            if (config.isItemStackOfType(itemStack)) itemTypes.add(type);
        }
        if (itemTypes.isEmpty()) itemTypes.add(VanillaItemTypes.OTHER);

        Predicate<VanillaItemTypes> predicate = (itemType) -> ModConfig.vanillaItemTypesKeepingMap.get(itemType);
        if (ModConfig.overlapResolution == OverlapResolution.LENIENT) {
            return itemTypes.stream().anyMatch(predicate);
        } else {
            return itemTypes.stream().allMatch(predicate);
        }
    }


    /**
     * Helper function that determines if the given item stack should be randomly dropped on death
     * @param stack ItemStack, which can be randomly dropped
     * @return Whether the item should be randomly dropped
     */
    protected boolean shouldDropRandomly(ItemStack stack) {
        return shouldDropRandomly(stack, player);
    }
    public static boolean shouldDropRandomly(ItemStack stack, Player player) {
        return player.getRandom().nextFloat() < getRandomDropChance(stack.getRarity(), player);
    }
    protected static float getRandomDropChance(Rarity rarity, Player player) {
        float dropChance = ModConfig.randomDropChance;

        // Luck
        final MobEffectInstance luck = player.getEffect(MobEffects.LUCK);
        if (luck != null) dropChance *= 1.0f - (luck.getAmplifier() * ModConfig.luckDropChanceDecrease);

        // Item rarity
        dropChance *= 1.0f - (rarityToPower(rarity) * ModConfig.rarityDropChanceDecrease);

        // rdc = 20%
        // rrdcd = 20%
        // additive Epic -> 12%
        // multiply Epic -> 12.8%
        // hybrid Epic -> 12%
        // lrdcd = 20%
        // luck 2
        // additive Epic -> 4%
        // multiply Epic -> 8.192%
        // hybrid Epic -> 7.2%

        return dropChance;
    }
    protected static int rarityToPower(Rarity rarity) {
        return switch (rarity) {
            case COMMON -> 0;
            case UNCOMMON -> 1;
            case RARE -> 2;
            case EPIC -> 3;
        };
    }
}
