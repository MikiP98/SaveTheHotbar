package io.github.mikip98.savethehotbar.content.blockentities;

import io.github.mikip98.savethehotbar.SaveTheHotbar;
import io.github.mikip98.savethehotbar.deathProcessing.DeathManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
#if MC_VERSION >= 12105
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
#endif
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class GraveContainerBlockEntity extends BlockEntity implements GraveContainerInventory, WorldlyContainer {
    protected NonNullList<ItemStack> items = NonNullList.create();
    @Getter @Setter
    protected int exp = 0;

    public GraveContainerBlockEntity(BlockPos pos, BlockState state) {
        super(SaveTheHotbar.GRAVE_CONTAINER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = NonNullList.withSize(items.size(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            this.items.set(i, items.get(i));
        }
        LOGGER.info(this.items.toString());

        this.setChanged();
    }

    #if MC_VERSION < 12006
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        final int size = tag.getInt("Size");
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.exp = tag.getInt("Experience");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putInt("Size", this.items.size());
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("Experience", this.exp);
        super.saveAdditional(tag);
    }

    #elif MC_VERSION < 12105
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        final int size = tag.getInt("Size");
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.exp = tag.getInt("Experience");
    }
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("Size", this.items.size());
        tag.putInt("Experience", this.exp);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        super.saveAdditional(tag, registries);
    }

    #else
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        final int size = input.getIntOr("Size", 0);
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.exp = input.getIntOr("Experience", 0);
    }
    @Override
    protected void saveAdditional(ValueOutput output) {
        output.putInt("Size", this.items.size());
        output.putInt("Experience", this.exp);
        ContainerHelper.saveAllItems(output, this.items, false);
        super.saveAdditional(output);
    }
    #endif

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    #if MC_VERSION < 12006
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
    #else
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }
    #endif


    @Override
    public int @NotNull [] getSlotsForFace(Direction side) { return new int[0]; }

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

    #if MC_VERSION >= 12105
    // Before 1.21.5 this is handled by the 'onRemove(...)' method in 'GraveContainer'
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        LOGGER.info("Dropping items:");
        LOGGER.info(this.items.toString());
        super.preRemoveSideEffects(pos, state);

        Level world = this.getLevel();
        if (world != null) {
            final int exp = this.getExp();
            LOGGER.info("Dropping '{}' exp", exp);

            if (exp > 0) {
                DeathManager.dropEXP(exp, world, world.getRandom(), pos);
            }
        }
    }
    #endif
}
