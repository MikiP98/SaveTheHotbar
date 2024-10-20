package io.github.mikip98.savethehotbar.mixin;

import io.github.mikip98.savethehotbar.ItemContainers.GravestoneHandler;
import io.github.mikip98.savethehotbar.ItemContainers.InternalContainersHandler;
import io.github.mikip98.savethehotbar.SaveTheHotbar;
import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.enums.ExperienceKeptCalculation;
import io.github.mikip98.savethehotbar.modDetection.DetectedMods;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.ArrayList;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Shadow
    @Nullable
    public abstract ItemEntity dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership);

    @Shadow
    private @Final PlayerInventory inventory;

//    @Inject(method = "dropInventory", at = @At("HEAD"))
//    public void keepHotbar(CallbackInfo ci) {
//        // This code is injected into the start of PlayerEntity.dropInventory()
//        System.out.println("SaveTheHotbar!: Saving hotbar");
//
//        if (saveHotbar) {
//            this.hotbar = new ArrayList<>(this.inventory.main);
//            for (int i = 0; i < this.hotbar.size(); i++) {
//                System.out.println(this.hotbar.get(i).toString());
//            }
////            this.inventory.main.clear();
//        }
//        if (saveArmor) {
//            this.armor = new ArrayList<>(this.inventory.armor);
////            this.inventory.armor.clear();
//        }
//        if (saveSecondHand) {
//            this.secondHand = new ArrayList<>(this.inventory.offHand);
////            this.inventory.offHand.clear();
//        }
//    }

    @Unique
    private static int rarityToPower(Rarity rarity) {
        return switch (rarity) {
            case COMMON -> 0;
            case UNCOMMON -> 1;
            case RARE -> 2;
            case EPIC -> 3;
        };
    }
    @Unique
    private static float getRandomDropChance(Rarity rarity) {
        if (Rarity.COMMON.equals(rarity)) {
            return ModConfig.randomDropChance;
        } else {
            return ModConfig.randomDropChance / (float) Math.pow(ModConfig.rarityDropChanceDecrease, rarityToPower(rarity));
        }
    }

    @Unique
    private static final ExperienceKeptCalculation experienceKeptCalculation = ExperienceKeptCalculation.VANILLA;

    /**
     * @author mikip98
     * @reason Keep hotbar
     */
    @Overwrite
    public void dropInventory() {
        if (ModConfig.logDeathCoordinatesInChat) {
            this.inventory.player.sendMessage(Text.literal("Death coordinates: " + this.inventory.player.getX() + ", " + this.inventory.player.getY() + ", " + this.inventory.player.getZ()));
        }

        ArrayList<ItemStack> mainDrop = new ArrayList<>();
        ArrayList<Integer> mainDropIDs = new ArrayList<>();

        ArrayList<ItemStack> armorDrop = new ArrayList<>();
        ArrayList<Integer> armorDropIDs = new ArrayList<>();

        ArrayList<ItemStack> secondHandDrop = new ArrayList<>();
        ArrayList<Integer> secondHandDropIDs = new ArrayList<>();

        // Manage Curse of Vanishing
        for (int i = 0; i < this.inventory.main.size(); i++) {
            if (EnchantmentHelper.hasVanishingCurse(this.inventory.main.get(i))) {
                this.inventory.main.set(i, ItemStack.EMPTY);
            }
        }
        for (int i = 0; i < this.inventory.armor.size(); i++) {
            if (EnchantmentHelper.hasVanishingCurse(this.inventory.armor.get(i))) {
                this.inventory.armor.set(i, ItemStack.EMPTY);
            }
        }
        for (int i = 0; i < this.inventory.offHand.size(); i++) {
            if (EnchantmentHelper.hasVanishingCurse(this.inventory.offHand.get(i))) {
                this.inventory.offHand.set(i, ItemStack.EMPTY);
            }
        }

        // Check keepInventory game rule
        if (!this.inventory.player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            // TODO: Create a config for this
            String message = "Game rule 'keepInventory' is not enabled! `SaveTheHotbar!` mod will not work! Dropping all items!";
            this.inventory.player.sendMessage(Text.of(message));
            LOGGER.error(message);
            for (ItemStack stack : this.inventory.main) {
                dropItem(stack, ModConfig.randomSpread, false);
            }
            for (ItemStack stack : this.inventory.armor) {
                dropItem(stack, ModConfig.randomSpread, false);
            }
            for (ItemStack stack : this.inventory.offHand) {
                dropItem(stack, ModConfig.randomSpread, false);
            }
            return;
        }

        // Keep hotbar
        for (int i = 0; i < this.inventory.main.size(); i++) {
            ItemStack stack = this.inventory.main.get(i);
            if (!stack.isEmpty()) {
                if (!(PlayerInventory.isValidHotbarIndex(i) && ModConfig.saveHotbar) || inventory.player.getRandom().nextFloat() < getRandomDropChance(stack.getRarity())) {
                    mainDrop.add(this.inventory.main.get(i));
                    mainDropIDs.add(i);
                    this.inventory.main.set(i, ItemStack.EMPTY);
                }
            }
        }

        // Keep armor
        if (!ModConfig.saveArmor || ModConfig.randomDropChance != 0) {
            for (int i = 0; i < this.inventory.armor.size(); i++) {
                ItemStack stack = this.inventory.armor.get(i);
                if (!stack.isEmpty()) {
                    if (!ModConfig.saveArmor || inventory.player.getRandom().nextFloat() < getRandomDropChance(stack.getRarity())) {
                        armorDrop.add(this.inventory.armor.get(i));
                        armorDropIDs.add(i);
                        this.inventory.armor.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }

        // Keep second hand
        if (!ModConfig.saveSecondHand || ModConfig.randomDropChance != 0) {
            for (int i = 0; i < this.inventory.offHand.size(); i++) {
                ItemStack stack = this.inventory.offHand.get(i);
                if (!stack.isEmpty()) {
                    if (!ModConfig.saveSecondHand || inventory.player.getRandom().nextFloat() < getRandomDropChance(stack.getRarity())) {
                        secondHandDrop.add(this.inventory.offHand.get(i));
                        secondHandDropIDs.add(i);
                        this.inventory.offHand.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }

        // Manage no-kept items
        ArrayList<ItemStack> drop = mainDrop;
        drop.addAll(armorDrop);
        drop.addAll(secondHandDrop);

        if (!mainDrop.isEmpty() || !armorDrop.isEmpty() || !secondHandDrop.isEmpty()) {
            if (ModConfig.containDrop)  {
                Block head = null;
                switch (ModConfig.containDropMode) {
                    case SACK:
                        LOGGER.info("Saving inventory in a Sack");
                        if (!drop.isEmpty()) {
                            PlayerEntity player = this.inventory.player;
                            InternalContainersHandler.spawn_sack(player.getWorld(), player.getBlockPos(), drop, player);
                        }
                        return;
                    case SKELETON_HEAD:
                        LOGGER.info("Saving inventory in a Skeleton Head");
                        head = SaveTheHotbar.SKELETON_HEAD_GRAVE;
                    case ZOMBIE_HEAD:
                        if (head == null) {
                            LOGGER.info("Saving inventory in a Zombie Head");
                            head = SaveTheHotbar.ZOMBIE_HEAD_GRAVE;
                        }
                    case RANDOM_HEAD:
                        if (head == null) {
                            LOGGER.info("Saving inventory in a Random Head");
                            if (inventory.player.getRandom().nextFloat() < 0.5) {
                                head = SaveTheHotbar.SKELETON_HEAD_GRAVE;
                            } else {
                                head = SaveTheHotbar.ZOMBIE_HEAD_GRAVE;
                            }
                        }
                        if (!drop.isEmpty()) {
                            PlayerEntity player = this.inventory.player;
                            InternalContainersHandler.spawn_head_grave(head, player.getWorld(), player.getBlockPos(), drop, player);
                        }
                        return;
                    case GRAVE:
                        if (DetectedMods.PNEUMONO_GRAVESTONES) {
                            LOGGER.info("Saving inventory in a Grave");
                            if (ModConfig.logGraveCoordinatesInChat) {
                                this.inventory.player.sendMessage(Text.of("Grave coordinates: " + this.inventory.player.getBlockPos().getX() + ", " + this.inventory.player.getBlockPos().getY() + ", " + this.inventory.player.getBlockPos().getZ()));
                            }
                            GravestoneHandler.handleGravestones(inventory.player, mainDrop, mainDropIDs, armorDrop, armorDropIDs, secondHandDrop, secondHandDropIDs);
                        } else {
                            String message = "ERROR: Gravestones mod by Pneumono_ is not installed or disabled. Please download it from https://modrinth.com/mod/pneumono_gravestones; Spawning a Sack instead.";
                            LOGGER.error(message);
                            inventory.player.sendMessage(Text.of(message));
                            LOGGER.info("Saving inventory in a Sack");
                            if (!drop.isEmpty()) {
                                PlayerEntity player = this.inventory.player;
                                InternalContainersHandler.spawn_sack(player.getWorld(), player.getBlockPos(), drop, player);
                            }
                        }
                        return;
                }
            }
            // !containDrop or unknown containDropMode
            String message = "Dropping inventory at " + this.inventory.player.getBlockPos();
            LOGGER.info(message);
            if (ModConfig.logDeathCoordinatesInChat) {
                this.inventory.player.sendMessage(Text.of(message));
            }
            for (ItemStack stack : drop) {
                dropItem(stack, ModConfig.randomSpread, false);
            }

            // Manage EXPERIENCE
            if (!ModConfig.keepExperience) {
                int experienceToDrop = experienceKeptCalculation.calculateExperienceKept(this.inventory.player);
                if (experienceToDrop > 0) {
                    this.inventory.player.experienceProgress = 0.0F;
                    this.inventory.player.experienceLevel = 0;
                    this.inventory.player.totalExperience = 0;

                    World world = this.inventory.player.getWorld();
                    if (!world.isClient()) {
                        int expEntitiesCount = world.getRandom().nextInt(7) + 1;

                        int[] extEntitiesWights = new int[expEntitiesCount];
                        int totalWeight = 0;
                        for (int i = 0; i < expEntitiesCount; i++) {
                            extEntitiesWights[i] = world.getRandom().nextInt(9) + 1;
                            totalWeight += extEntitiesWights[i];
                        }

                        int[] expEntitiesExperience = new int[expEntitiesCount];
                        for (int i = 0; i < expEntitiesCount; i++) {
                            expEntitiesExperience[i] = extEntitiesWights[i] * experienceToDrop / totalWeight;
                        }

                        for (int i = 0; i < expEntitiesCount; i++) {
                            world.spawnEntity(new ExperienceOrbEntity(world, this.inventory.player.getX(), this.inventory.player.getY(), this.inventory.player.getZ(), expEntitiesExperience[i]));
                        }
                    }
                }
            }
        }
    }
}
