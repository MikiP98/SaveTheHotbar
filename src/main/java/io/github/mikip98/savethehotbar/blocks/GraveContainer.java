package io.github.mikip98.savethehotbar.blocks;

import io.github.mikip98.savethehotbar.blockentities.GraveContainerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GraveContainer extends Block implements BlockEntityProvider, Waterloggable {
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.WATERLOGGED);
    }

    public GraveContainer(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(Properties.WATERLOGGED, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : Fluids.EMPTY.getDefaultState();
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GraveContainerBlockEntity cabinetEntity) {
                // Handle item drops and block entity cleanup here
                DefaultedList<ItemStack> inventory = cabinetEntity.getItems();
                for (ItemStack stack : inventory) {
                    if (!stack.isEmpty()) {
                        Block.dropStack(world, pos, stack);
                    }
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }


    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) { return new GraveContainerBlockEntity(pos, state); }
}
