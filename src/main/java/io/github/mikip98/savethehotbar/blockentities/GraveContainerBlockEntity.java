package io.github.mikip98.savethehotbar.blockentities;

import io.github.mikip98.savethehotbar.SaveTheHotbar;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class GraveContainerBlockEntity extends BlockEntity implements GraveContainerInventory, SidedInventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(41, ItemStack.EMPTY);

    public GraveContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }
    public GraveContainerBlockEntity(BlockPos pos, BlockState state) { super(SaveTheHotbar.GRAVE_CONTAINER_BLOCK_ENTITY, pos, state); }


    @Override
    public DefaultedList<ItemStack> getItems() { return this.items; }

    public void setItems(ArrayList<ItemStack> items) {
        this.items.clear();
        for (int i = 0; i < items.size(); i++) {
            this.items.set(i, items.get(i));
        }
    }


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);
        super.writeNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }


    @Override
    public int[] getAvailableSlots(Direction side) { return new int[0]; }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        // No input
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        // Output from the bottom
        return dir == Direction.DOWN;
    }
}
