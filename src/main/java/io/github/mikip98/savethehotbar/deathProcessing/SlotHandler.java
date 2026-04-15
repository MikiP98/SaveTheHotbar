package io.github.mikip98.savethehotbar.deathProcessing;

import io.github.mikip98.savethehotbar.config.ModConfig;
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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class SlotHandler implements SlotSupport {
    protected Player player;
    protected Inventory inventory;

    public SlotHandler(Inventory inventory) {
        this.player = inventory.player;
        this.inventory = inventory;
    }

    // ------------ CHECK FOR NON-KEPT ITEMS ---------------------------------------------------------------------------
    public NonKeptItems getNonKeptItems() {
        LOGGER.info("Checking for vanilla non-kept items...");
        final VanillaDropSet vanillaDropSet = getVanillaNonKeptItems();
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
        checkForDropHotbar(vanillaDrop, vanillaDropIDs, ModConfig.saveHotbar, inventory.items);
        // Keep armor
        checkForDrop(vanillaDrop, vanillaDropIDs, ModConfig.saveArmor, inventory.armor);
        // Keep second hand
        checkForDrop(vanillaDrop, vanillaDropIDs, ModConfig.saveSecondHand, inventory.offhand);

        return new VanillaDropSet(vanillaDrop, vanillaDropIDs);
    }
    protected void checkForDropHotbar(List<ItemStack> drop, List<Integer> dropIds, boolean shouldKeep, NonNullList<ItemStack> slots) {
        for (int i = 0; i < slots.size(); i++) {
            ItemStack stack = slots.get(i);
            if (!stack.isEmpty() && (!Inventory.isHotbarSlot(i) || !shouldKeep || shouldDropRandomly(stack))) {
                drop.add(slots.get(i).copyAndClear());
                dropIds.add(i);
            }
        }
    }
    protected void checkForDrop(List<ItemStack> drop, List<Integer> dropIds, boolean shouldKeep, NonNullList<ItemStack> slots) {
        if (!shouldKeep || ModConfig.randomDropChance != 0) {
            for (int i = 0; i < slots.size(); i++) {
                ItemStack stack = slots.get(i);
                if (shouldDrop(stack, shouldKeep)) {
                    drop.add(slots.get(i).copyAndClear());
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
        #if MC_VERSION == 12001
        if (SupportedSlotMods.ARSENAL.isLoaded())
            moddedDrop.put(SupportedSlotMods.ARSENAL, Arsenal.getItemsToDrop(player, this::shouldDrop));
        #endif

        return moddedDrop;
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
        boolean other = true;
        for (VanillaItemTypes type : VanillaItemTypes.values()) {
            if (type == VanillaItemTypes.OTHER) continue;
            final ItemTypeConfig config = ItemTypesConfiguration.vanillaItemTypes.get(type);
            if (
                    itemStack.is(config.getTagOverride())
                    || config.getTags().stream().anyMatch(itemStack::is)
                    || config.getClasses().stream().anyMatch(clazz -> clazz.isInstance(itemStack.getItem()))
                    || config.getPredicate() != null && config.getPredicate().apply(itemStack.getItem())
            ) {
                other = false;
                if (ModConfig.vanillaItemTypesKeepingMap.get(type))
                    return true;
            }
        }
        return other ? ModConfig.vanillaItemTypesKeepingMap.get(VanillaItemTypes.OTHER) : false;
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
