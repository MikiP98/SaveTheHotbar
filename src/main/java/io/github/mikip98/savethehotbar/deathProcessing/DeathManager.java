package io.github.mikip98.savethehotbar.deathProcessing;

import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.config.enums.ExperienceMode;
import io.github.mikip98.savethehotbar.modDetection.SupportedSlotMods;
import io.github.mikip98.savethehotbar.deathProcessing.moddedSlotsHandlers.Arsenal;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class DeathManager {
    // -----------------------------------------------------------------------------------------------------------------

    protected final PlayerInventory inventory;
    protected final PlayerEntity player;
    protected final ItemDropper rawItemDropFunction;

    public DeathManager(PlayerInventory inventory, ItemDropper itemDropper) {
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
            player.sendMessage(Text.literal("Death coordinates: " + player.getBlockPos()).formatted(Formatting.AQUA));
        }

        // --- Manage Curse of Vanishing ---
        LOGGER.info("Destroying Cursed Items...");
        destroyCursedItems();

        LOGGER.info("Calculating new EXP amount...");
        final int exp = ModConfig.experienceCalculationMode.calculateExperience(player);

        final SlotHandler slotHandler = new SlotHandler(inventory);
        LOGGER.info("Checking for non-kept items...");
        final SlotHandler.NonKeptItems nonKeptItems = slotHandler.getNonKeptItems();

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
            if (ModConfig.experienceBehaviour == ExperienceMode.DROP) dropEXP(exp);
            else if (ModConfig.experienceBehaviour == ExperienceMode.KEEP) player.addExperience(exp);
        }
    }

    // ------------ CURSED ITEM DESTRUCTION ------------
    protected void destroyCursedItems() {
        // --------- Vanilla ---------
        destroyCursedItems(inventory.main);
        destroyCursedItems(inventory.armor);
        destroyCursedItems(inventory.offHand);
        // --------- Modded Slots ---------
        if (SupportedSlotMods.ARSENAL.isLoaded()) Arsenal.destroyCursed(player);
    }
    protected static void destroyCursedItems(DefaultedList<ItemStack> slots) {
        slots.forEach(slot -> { if (EnchantmentHelper.hasVanishingCurse(slot)) slot.setCount(0); });
    }
    // -------------------------------------------------

    // ------------ DROP EXP ------------
    public void dropEXP(int exp) {
        final World world = player.getWorld();
        final Random random = world.getRandom();
        dropEXP(exp, world, random, player.getBlockPos());
    }
    public static void dropEXP(int exp, World world, Random random, BlockPos pos) {
        if (!world.isClient()) {
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
                world.spawnEntity(new ExperienceOrbEntity(world, pos.getX(), pos.getY(), pos.getZ(), expEntitiesExperience[i]));
            }
        }
    }
    // ----------------------------------
}
