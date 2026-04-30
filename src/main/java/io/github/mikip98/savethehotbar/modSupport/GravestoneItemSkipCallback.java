package io.github.mikip98.savethehotbar.modSupport;

import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.deathProcessing.SlotHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.pneumono.gravestones.api.SkipItemCallback;
import org.jetbrains.annotations.Nullable;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class GravestoneItemSkipCallback implements SkipItemCallback {
    @Override
    public boolean insertItem(Player playerEntity, ItemStack itemStack, @Nullable ResourceLocation identifier) {
        LOGGER.info("Gravestone handler called");
        LOGGER.info("identifier -> {}", identifier);

        if (identifier != null && identifier.getNamespace().equals("minecraft")) {
            int slotId = Integer.parseInt(identifier.getPath());
            final VanillaSubInventory.SubInventoryAndIdOffset subInventoryAndIdOffset = VanillaSubInventory.getSubInventory(slotId);

            final VanillaSubInventory subInventory = subInventoryAndIdOffset.subInventory;
            slotId -= subInventoryAndIdOffset.offset;

            return !switch (subInventory) {
                case MAIN ->
                        !itemStack.isEmpty() && (!Inventory.isHotbarSlot(slotId) || !ModConfig.INSTANCE.saveHotbar || SlotHandler.shouldDropRandomly(itemStack, playerEntity));
                case ARMOR -> SlotHandler.shouldDrop(itemStack, ModConfig.INSTANCE.saveArmor, playerEntity);
                case OFF_HAND -> SlotHandler.shouldDrop(itemStack, ModConfig.INSTANCE.saveSecondHand, playerEntity);
            };
        }
        return true;
    }

    protected enum VanillaSubInventory {
        MAIN(36),
        ARMOR(4),
        OFF_HAND(1);

        public final int slotAmount;

        VanillaSubInventory(int slotAmount) {
            this.slotAmount = slotAmount;
        }

        public static SubInventoryAndIdOffset getSubInventory(int i) {
            int offset = 0;
            if (i < MAIN.slotAmount) return new SubInventoryAndIdOffset(MAIN, offset);
            i -= MAIN.slotAmount;
            offset = MAIN.slotAmount;

            if (i < ARMOR.slotAmount) return new SubInventoryAndIdOffset(ARMOR, offset);
            i -= ARMOR.slotAmount;
            offset = ARMOR.slotAmount;

            if (i < OFF_HAND.slotAmount) return new SubInventoryAndIdOffset(OFF_HAND, offset);

            throw new IndexOutOfBoundsException("Slot Id '" + i + "' is out of bounds");
        }
        public record SubInventoryAndIdOffset(VanillaSubInventory subInventory, int offset) {}
    }
}
