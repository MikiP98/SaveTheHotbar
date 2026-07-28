package io.github.mikip98.savethehotbar.deathProcessing;

import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.config.enums.ExperienceMode;
#if MC_VERSION == 12001
import io.github.mikip98.savethehotbar.deathProcessing.moddedSlotsHandlers.Arsenal;
import io.github.mikip98.savethehotbar.modDetection.SupportedSlotMods;
#endif
#if MC_VERSION >= 12100 import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents; #endif
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.List;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class DeathManager {
    // -----------------------------------------------------------------------------------------------------------------

    protected final Inventory inventory;
    protected final Player player;
    protected final ItemDropper rawItemDropFunction;

    public DeathManager(Inventory inventory, ItemDropper itemDropper) {
        LOGGER.info("Creating DeathManager");
        this.inventory = inventory;
        this.player = inventory.player;
        this.rawItemDropFunction = itemDropper;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @FunctionalInterface
    public interface ItemDropper {
        void dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership);
    }

    // -----------------------------------------------------------------------------------------------------------------

    public void managePlayerDeath() {
        if (ModConfig.INSTANCE.logDeathCoordinatesInChat) {
            player.avlSendServerMessage(Component.literal("Death coordinates: " + player.blockPosition()).withStyle(ChatFormatting.AQUA));
        }

        // --- Manage Curse of Vanishing ---
        LOGGER.info("Destroying Cursed Items...");
        destroyVanishingCursedItems();

        LOGGER.info("Calculating new EXP amount...");
        final int exp = ModConfig.INSTANCE.expControl.experienceCalculationMode.calculateExperience(player);

        LOGGER.info("Checking for non-kept items...");
        final SlotHandler slotHandler = new SlotHandler(inventory);
        final List<ItemStack> nonKeptItems = slotHandler.getNonKeptItems();

        LOGGER.info("Handling the non-kept items...");
        int storedExperience = 0;
        if (ModConfig.INSTANCE.expControl.experienceBehaviour == ExperienceMode.STORE) {
            LOGGER.info("Experience will be stored in the grave...");
            storedExperience = exp;
        }
        final ContainerHandler containerHandler = new ContainerHandler(player, nonKeptItems, storedExperience, rawItemDropFunction);
        containerHandler.handleDrop();

        // Manage EXPERIENCE
        player.experienceProgress = 0.0F;
        player.experienceLevel = 0;
        player.totalExperience = 0;
        if (exp > 0) {
            LOGGER.info("Handling not stored experience...");
            // Drop teh EXP if the mode is set to 'DROP' or 'containDrop' is false as no grave will spawn
            if (ModConfig.INSTANCE.expControl.experienceBehaviour == ExperienceMode.DROP || !ModConfig.INSTANCE.dropControl.containDrop) dropEXP(exp);
            else if (ModConfig.INSTANCE.expControl.experienceBehaviour == ExperienceMode.KEEP) player.giveExperiencePoints(exp);
        }

        // HUNGER & SATURATION is managed in 'COPY_FROM' event in 'EventRegistry' class
    }

    // ------------ CURSED ITEM DESTRUCTION ------------
    protected void destroyVanishingCursedItems() {
        // --------- Vanilla ---------
        #if MC_VERSION < 12105
        destroyVanishingCursedItems(inventory.items);
        destroyVanishingCursedItems(inventory.armor);
        destroyVanishingCursedItems(inventory.offhand);
        #else
        destroyVanishingCursedItems(inventory.getNonEquipmentItems());
        destroyVanishingCursedItem(player.getItemBySlot(EquipmentSlot.FEET));
        destroyVanishingCursedItem(player.getItemBySlot(EquipmentSlot.LEGS));
        destroyVanishingCursedItem(player.getItemBySlot(EquipmentSlot.CHEST));
        destroyVanishingCursedItem(player.getItemBySlot(EquipmentSlot.HEAD));
        destroyVanishingCursedItem(player.getOffhandItem());
        #endif
        // --------- Modded Slots ---------
        #if MC_VERSION == 12001
        if (SupportedSlotMods.ARSENAL.isLoaded()) Arsenal.destroyCursed(player);
        #endif
        // TODO: Make the enum store the function so that I can just iterate through the enum
        //  Like make it into a registry
    }
    protected static boolean hasVanishingCurse(ItemStack itemStack) {
        #if MC_VERSION < 12100
        return EnchantmentHelper.hasVanishingCurse(itemStack);
        #else
        return !itemStack.isEmpty() && EnchantmentHelper.has(itemStack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP);
        #endif
    }
    protected static void destroyVanishingCursedItems(NonNullList<ItemStack> slots) {
        slots.forEach(DeathManager::destroyVanishingCursedItem);
    }
    protected static void destroyVanishingCursedItem(ItemStack itemStack) {
        if (hasVanishingCurse(itemStack)) itemStack.setCount(0);
    }
    // -------------------------------------------------

    // ------------ DROP EXP ------------
    public void dropEXP(int exp) {
        final Level world = player.level();
        final RandomSource random = world.getRandom();
        dropEXP(exp, world, random, player.blockPosition());
    }
    public static void dropEXP(int exp, Level world, RandomSource random, BlockPos pos) {
        if (!world.isClientSide()) {
            final int expEntitiesCount = random.nextInt(7) + 1;

            int[] extEntitiesWights = new int[expEntitiesCount];
            int totalWeight = 0;
            for (int i = 0; i < expEntitiesCount; i++) {
                extEntitiesWights[i] = random.nextInt(9) + 1;
                totalWeight += extEntitiesWights[i];
            }

            int[] expEntitiesExperience = new int[expEntitiesCount];
            for (int i = 0; i < expEntitiesCount; i++) {
                expEntitiesExperience[i] = extEntitiesWights[i] * exp / totalWeight;
            }

            for (int i = 0; i < expEntitiesCount; i++) {
                world.addFreshEntity(new ExperienceOrb(world, pos.getX(), pos.getY(), pos.getZ(), expEntitiesExperience[i]));
            }
        }
    }
    // ----------------------------------
}
