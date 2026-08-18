package io.github.mikip98.savethehotbar.content.blockentities;

import io.github.mikip98.savethehotbar.SaveTheHotbar;
#if MC_VERSION >= 12105 import io.github.mikip98.savethehotbar.deathProcessing.DeathManager; #endif
import io.mikip98.humilityval.content.block.entity.AVLBlockEntity;
import io.mikip98.humilityval.content.block.entity.AVLDataInput;
import io.mikip98.humilityval.content.block.entity.AVLDataOutput;
import lombok.Getter;
import lombok.Setter;
#if MC_VERSION >= 12006 import net.minecraft.core.HolderLookup; #endif
#if MC_VERSION >= 12105 import net.minecraft.world.level.Level; #endif
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
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

public class GraveContainerBlockEntity extends AVLBlockEntity implements GraveContainerInventory, WorldlyContainer {
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

    @Override
    protected void avlSaveAdditional(AVLDataOutput out) {
        out.putInt("size", this.items.size());
        out.saveItems(this.items);
        out.putInt("experience", this.exp);
    }

    @Override
    protected void avlLoadAdditional(AVLDataInput in) {
        final int size = in.getIntOr("size", 0);
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        in.loadItems(this.items);
        this.exp = in.getIntOr("experience", 0);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // TODO: Found the below is not needed on 1.20.1 and 1.21.8, double check it is also not required upstream
//    @Override
//    #if MC_VERSION < 12006
//    public @NotNull CompoundTag getUpdateTag() {
//        return saveWithoutMetadata();
//    }
//    #else
//    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
//        return saveWithoutMetadata(provider);
//    }
//    #endif


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
