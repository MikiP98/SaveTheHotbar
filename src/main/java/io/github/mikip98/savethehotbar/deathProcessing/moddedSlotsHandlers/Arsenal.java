package io.github.mikip98.savethehotbar.deathProcessing.moddedSlotsHandlers;

import dev.doctor4t.arsenal.cca.BackWeaponComponent;
import io.github.mikip98.savethehotbar.config.ModConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Arsenal implements SlotSupport {
    public static void destroyCursed(PlayerEntity player) {
        if (EnchantmentHelper.hasVanishingCurse(BackWeaponComponent.getBackWeapon(player)))
            BackWeaponComponent.setBackWeapon(player, ItemStack.EMPTY);
    }

    public static List<ItemStack> getItemsToDrop(PlayerEntity player, ShouldDrop shouldDrop) {
        List<ItemStack> itemsToDrop = new ArrayList<>();
        if (!ModConfig.saveArsenal || ModConfig.randomDropChance != 0) {
            final ItemStack backWeapon = BackWeaponComponent.getBackWeapon(player);
            if (shouldDrop.apply(backWeapon, ModConfig.saveArsenal)) {
                itemsToDrop.add(backWeapon);
                BackWeaponComponent.setBackWeapon(player, ItemStack.EMPTY);
            }
        }
        return itemsToDrop;
    }
}
