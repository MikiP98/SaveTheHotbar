package io.github.mikip98.savethehotbar.deathProcessing;

import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.config.enums.ExperienceMode;
import io.github.mikip98.savethehotbar.modDetection.SupportedSlotMods;
#if MC_VERSION == 12001
import io.github.mikip98.savethehotbar.deathProcessing.moddedSlotsHandlers.Arsenal;
#endif
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
        if (ModConfig.logDeathCoordinatesInChat) {
            player.sendSystemMessage(Component.literal("Death coordinates: " + player.blockPosition()).withStyle(ChatFormatting.AQUA));
        }

        // --- Manage Curse of Vanishing ---
        LOGGER.info("Destroying Cursed Items...");
        destroyCursedItems();

        LOGGER.info("Calculating new EXP amount...");
        final int exp = ModConfig.experienceCalculationMode.calculateExperience(player);

        LOGGER.info("Checking for non-kept items...");
        final SlotHandler slotHandler = new SlotHandler(inventory);
        final List<ItemStack> nonKeptItems = slotHandler.getNonKeptItems();

        LOGGER.info("Handling the non-kept items...");
        int storedExperience = 0;
        if (ModConfig.experienceBehaviour == ExperienceMode.STORE) {
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
            if (ModConfig.experienceBehaviour == ExperienceMode.DROP || !ModConfig.containDrop) dropEXP(exp);
            else if (ModConfig.experienceBehaviour == ExperienceMode.KEEP) player.giveExperiencePoints(exp);
        }
    }

    // ------------ CURSED ITEM DESTRUCTION ------------
    protected void destroyCursedItems() {
        // --------- Vanilla ---------
        destroyCursedItems(inventory.items);
        destroyCursedItems(inventory.armor);
        destroyCursedItems(inventory.offhand);
        // --------- Modded Slots ---------
        #if MC_VERSION == 12001
        if (SupportedSlotMods.ARSENAL.isLoaded()) Arsenal.destroyCursed(player);
        #endif
        // TODO: Make the enum store the function so that I can just iterate through the enum
        //  Like make it into a registry
    }
    protected static void destroyCursedItems(NonNullList<ItemStack> slots) {
        slots.forEach(slot -> { if (EnchantmentHelper.hasVanishingCurse(slot)) slot.setCount(0); });
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
