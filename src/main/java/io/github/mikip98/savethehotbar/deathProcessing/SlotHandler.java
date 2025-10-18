package io.github.mikip98.savethehotbar.deathProcessing;

import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.deathProcessing.moddedSlotsHandlers.Arsenal;
import io.github.mikip98.savethehotbar.deathProcessing.moddedSlotsHandlers.SlotSupport;
import io.github.mikip98.savethehotbar.modDetection.SupportedSlotMods;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class SlotHandler implements SlotSupport {
    protected PlayerEntity player;
    protected PlayerInventory inventory;

    public SlotHandler(PlayerInventory inventory) {
        this.player = inventory.player;
        this.inventory = inventory;
    }

    // ------------ CHECK FOR NON-KEPT ITEMS ---------------------------------------------------------------------------
    public NonKeptItems getNonKeptItems() {
        LOGGER.info("Checking for vanilla non-kept items...");
        VanillaDropSet vanillaDropSet = null;
        try {
            vanillaDropSet = getVanillaNonKeptItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("Checking for modded non-kept items...");
        return new NonKeptItems(vanillaDropSet.vanillaDrop, vanillaDropSet.vanillaDropIDs, getModdedNonKeptItems());
    }
    public record NonKeptItems(List<ItemStack> vanillaDrop, List<Integer> vanillaSlotIds, Map<SupportedSlotMods, List<ItemStack>> moddedDrop) {}

    // ------------ Vanilla ------------
    protected VanillaDropSet getVanillaNonKeptItems() {
        // Main -> Armor -> Offhand
        List<ItemStack> vanillaDrop = new ArrayList<>();
        List<Integer> vanillaDropIDs = new ArrayList<>();

        // Keep hotbar
        checkForDropHotbar(vanillaDrop, vanillaDropIDs, ModConfig.saveHotbar, inventory.main);
        // Keep armor
        checkForDrop(vanillaDrop, vanillaDropIDs, ModConfig.saveArmor, inventory.armor);
        // Keep second hand
        checkForDrop(vanillaDrop, vanillaDropIDs, ModConfig.saveSecondHand, inventory.offHand);

        return new VanillaDropSet(vanillaDrop, vanillaDropIDs);
    }
    protected void checkForDropHotbar(List<ItemStack> drop, List<Integer> dropIds, boolean shouldKeep, DefaultedList<ItemStack> slots) {
        for (int i = 0; i < slots.size(); i++) {
            ItemStack stack = slots.get(i);
            if (!stack.isEmpty() && (!PlayerInventory.isValidHotbarIndex(i) || !shouldKeep || shouldDropRandomly(stack))) {
                drop.add(slots.get(i).copyAndEmpty());
                dropIds.add(i);
            }
        }
    }
    protected void checkForDrop(List<ItemStack> drop, List<Integer> dropIds, boolean shouldKeep, DefaultedList<ItemStack> slots) {
        if (!shouldKeep || ModConfig.randomDropChance != 0) {
            for (int i = 0; i < slots.size(); i++) {
                ItemStack stack = slots.get(i);
                if (shouldDrop(stack, shouldKeep)) {
                    drop.add(slots.get(i).copyAndEmpty());
                    dropIds.add(i);
                }
            }
        }
    }
    protected record VanillaDropSet(List<ItemStack> vanillaDrop, List<Integer> vanillaDropIDs) {}

    // ------------ Modded ------------
    protected Map<SupportedSlotMods, List<ItemStack>> getModdedNonKeptItems() {
        // Mod Support
        Map<SupportedSlotMods, List<ItemStack>> moddedDrop = new EnumMap<>(SupportedSlotMods.class);

        // Keep Arsenal
        if (SupportedSlotMods.ARSENAL.isLoaded())
            moddedDrop.put(SupportedSlotMods.ARSENAL, Arsenal.getItemsToDrop(player, this::shouldDrop));

        return moddedDrop;
    }
    // -----------------------------------------------------------------------------------------------------------------

    protected boolean shouldDrop(ItemStack stack, boolean shouldKeep) {
        return !stack.isEmpty() && (!shouldKeep || shouldDropRandomly(stack));
    }
    /**
     * Helper function that determines if the given item stack should be randomly dropped on death
     * @param stack ItemStack, which can be randomly dropped
     * @return Whether the item should be randomly dropped
     */
    protected boolean shouldDropRandomly(ItemStack stack) {
        return player.getRandom().nextFloat() < getRandomDropChance(stack.getRarity(), player);
    }
    protected static float getRandomDropChance(Rarity rarity, PlayerEntity player) {
        float dropChance = ModConfig.randomDropChance;

        // Luck
        final StatusEffectInstance luck = player.getStatusEffect(StatusEffects.LUCK);
        if (luck != null) dropChance *= 1.0f - (luck.getAmplifier() * ModConfig.luckDropChanceDecrease) / 100.0f;

        // Item rarity
        dropChance *= 1.0f - (rarityToPower(rarity) * ModConfig.rarityDropChanceDecrease) / 100.0f;

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
