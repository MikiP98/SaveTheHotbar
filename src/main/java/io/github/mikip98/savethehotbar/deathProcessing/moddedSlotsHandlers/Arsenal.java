#if MC_VERSION == 12001
package io.github.mikip98.savethehotbar.deathProcessing.moddedSlotsHandlers;

import dev.doctor4t.arsenal.cca.BackWeaponComponent;
import io.github.mikip98.savethehotbar.config.ModConfig;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Arsenal implements SlotSupport {
    public static void destroyCursed(Player player) {
        if (EnchantmentHelper.hasVanishingCurse(BackWeaponComponent.getBackWeapon(player)))
            BackWeaponComponent.setBackWeapon(player, ItemStack.EMPTY);
    }

    public static List<ItemStack> getItemsToDrop(Player player, ShouldDrop shouldDrop) {
        List<ItemStack> itemsToDrop = new ArrayList<>();
        if (!ModConfig.INSTANCE.saveArsenal || ModConfig.INSTANCE.randomDropChance != 0) {
            final ItemStack backWeapon = BackWeaponComponent.getBackWeapon(player);
            if (shouldDrop.apply(backWeapon, ModConfig.INSTANCE.saveArsenal)) {
                itemsToDrop.add(backWeapon);
                BackWeaponComponent.setBackWeapon(player, ItemStack.EMPTY);
            }
        }
        return itemsToDrop;
    }
}
#endif