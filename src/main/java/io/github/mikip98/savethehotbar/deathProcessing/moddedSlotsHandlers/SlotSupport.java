package io.github.mikip98.savethehotbar.deathProcessing.moddedSlotsHandlers;

import net.minecraft.item.ItemStack;

public interface SlotSupport {
    // TODO: Consider creating a universal API and running mod support in a loop

    @FunctionalInterface
    interface ShouldDrop {
        boolean apply(ItemStack stack, boolean shouldAlwaysDrop);
    }
}
