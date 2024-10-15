package io.github.mikip98.savethehotbar.mixin;

import io.github.mikip98.savethehotbar.config.ModConfig;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.ArrayList;

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

    /**
     * @author mikip98
     * @reason Keep hotbar
     */
    @Overwrite
    public void dropInventory() {
        ArrayList<ItemStack> drop = new ArrayList<>();

        // Keep hotbar
        for (int i = 0; i < this.inventory.main.size(); i++) {
            ItemStack stack = this.inventory.main.get(i);
            if (!stack.isEmpty()) {
                if (!(PlayerInventory.isValidHotbarIndex(i) && ModConfig.saveHotbar) || inventory.player.getRandom().nextFloat() < getRandomDropChance(stack.getRarity())) {
                    drop.add(this.inventory.main.get(i));
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
                        drop.add(this.inventory.main.get(i));
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
                        drop.add(this.inventory.main.get(i));
                        this.inventory.offHand.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }

        // Manage no-kept items
        if (!drop.isEmpty()) {
            if (ModConfig.containDrop)  {
                switch (ModConfig.containDropMode) {
                    case SACK:
                    case GRAVE:
                        for (ItemStack stack : drop) {
                            dropItem(stack, ModConfig.randomSpread, true);
                        }
                        return;
                }
            }
            // !containDrop or unknown containDropMode
            for (ItemStack stack : drop) {
                dropItem(stack, ModConfig.randomSpread, false);
            }
        }
    }
}
