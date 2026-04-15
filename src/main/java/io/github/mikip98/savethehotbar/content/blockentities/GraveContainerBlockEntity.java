package io.github.mikip98.savethehotbar.content.blockentities;

import io.github.mikip98.savethehotbar.SaveTheHotbar;
import io.github.mikip98.savethehotbar.modDetection.SupportedSlotMods;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class GraveContainerBlockEntity extends BlockEntity implements GraveContainerInventory, WorldlyContainer {
    protected final NonNullList<ItemStack> items = NonNullList.withSize(41, ItemStack.EMPTY);
    protected final EnumMap<SupportedSlotMods, NonNullList<ItemStack>> moddedItems = createEnumMap();
    @Setter
    @Getter
    protected int exp = 0;

    protected EnumMap<SupportedSlotMods, NonNullList<ItemStack>> createEnumMap() {
        EnumMap<SupportedSlotMods, NonNullList<ItemStack>> moddedItems = new EnumMap<>(SupportedSlotMods.class);
        for (SupportedSlotMods mod : SupportedSlotMods.values()) {
            if (mod.isLoaded()) moddedItems.put(mod, NonNullList.withSize(mod.slotAmount, ItemStack.EMPTY));
        }
        return moddedItems;
    }

    public GraveContainerBlockEntity(BlockPos pos, BlockState state) {
        super(SaveTheHotbar.GRAVE_CONTAINER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        final List<ItemStack> allItems = Stream.concat(this.items.stream(), this.moddedItems.values().stream().flatMap(List::stream)).toList();
        NonNullList<ItemStack> defaultedList = NonNullList.withSize(allItems.size(), ItemStack.EMPTY);
        for (int i = 0; i < allItems.size(); i++) { defaultedList.set(i, allItems.get(i)); }
        return defaultedList;
    }

    public void setItems(List<ItemStack> vanillaItems, Map<SupportedSlotMods, List<ItemStack>> moddedItems) {
        this.items.clear();
        for (int i = 0; i < vanillaItems.size(); i++) {
            this.items.set(i, vanillaItems.get(i));
        }
        for (SupportedSlotMods mod : SupportedSlotMods.values()) {
            if (mod.isLoaded()) {
                final List<ItemStack> items = moddedItems.get(mod);
                NonNullList<ItemStack> defaultedList = NonNullList.withSize(items.size(), ItemStack.EMPTY);
                for (int i = 0; i < items.size(); i++) {
                    defaultedList.set(i, items.get(i));
                }
                this.moddedItems.put(mod, defaultedList);
            }
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        ContainerHelper.loadAllItems(nbt, items);
        for (Map.Entry<SupportedSlotMods, NonNullList<ItemStack>> entry : moddedItems.entrySet()) {
            tryReadModdedItemNbt(nbt, entry.getValue(), entry.getKey());
        }
        this.exp = nbt.getInt("Experience");
    }

    /**
     * Code taken from 'Inventories.readNBT()'
     * Located in package 'net.minecraft.inventory'
     * Modified to accept a mod
     */
    public static void tryReadModdedItemNbt(CompoundTag nbt, NonNullList<ItemStack> stacks, SupportedSlotMods mod) {
        if (!mod.isLoaded()) return;

        final String nbtId = "Items" + mod.modName;
        if (!nbt.contains(nbtId)) return;

        ListTag nbtList = nbt.getList(nbtId, 10);

        for (int i = 0; i < nbtList.size(); i++) {
            CompoundTag nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            if (j < stacks.size()) {
                stacks.set(j, ItemStack.of(nbtCompound));
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        ContainerHelper.saveAllItems(nbt, items);
        for (Map.Entry<SupportedSlotMods, NonNullList<ItemStack>> entry : moddedItems.entrySet()) {
            writeNbt(nbt, entry.getValue(), entry.getKey());
        }
        nbt.putInt("Experience", this.exp);
        super.saveAdditional(nbt);
    }

    /**
     * Code taken from 'Inventories.writeNbt()'
     * Located in package 'net.minecraft.inventory'
     * Modified to accept a mod
     */
    public static void writeNbt(CompoundTag nbt, NonNullList<ItemStack> stacks, SupportedSlotMods mod) {
        if (!mod.isLoaded() || stacks.isEmpty() || stacks.stream().allMatch((stack) -> stack == ItemStack.EMPTY)) return;
        ListTag nbtList = new ListTag();

        for (int i = 0; i < stacks.size(); i++) {
            ItemStack itemStack = stacks.get(i);
            if (!itemStack.isEmpty()) {
                CompoundTag nbtCompound = new CompoundTag();
                nbtCompound.putByte("Slot", (byte) i);
                itemStack.save(nbtCompound);
                nbtList.add(nbtCompound);
            }
        }

        nbt.put("Items" + mod.modName, nbtList);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }


    @Override
    public int[] getSlotsForFace(Direction side) { return new int[0]; }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        // No input
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        // Output from the bottom
        return dir == Direction.DOWN;
    }
}
