package io.github.mikip98.savethehotbar.blocks;

import io.github.mikip98.savethehotbar.blockentities.GraveContainerBlockEntity;
import io.github.mikip98.savethehotbar.deathProcessing.DeathManager;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class GraveContainer extends Block implements BlockEntityProvider, Waterloggable {
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.WATERLOGGED);
    }

    public GraveContainer(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(Properties.WATERLOGGED, false)
        );
    }

    @Override
    public @NotNull BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(
                Properties.WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : Fluids.EMPTY.getDefaultState();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        LOGGER.info("Grave state change detected!");
        if (state.getBlock() != newState.getBlock()) {
            LOGGER.info("Grave has been destroyed!");
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GraveContainerBlockEntity graveContainerBlockEntity) {
                LOGGER.info("Dropping '{}' items", graveContainerBlockEntity.itemCount());
                for (ItemStack stack : graveContainerBlockEntity.getItems()) {
                    if (!stack.isEmpty()) Block.dropStack(world, pos, stack);
                }
                final int exp = graveContainerBlockEntity.getExp();
                LOGGER.info("Dropping '{}' exp", exp);
                if (exp > 0) {
                    DeathManager.dropEXP(exp, world, world.getRandom(), pos);
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }


    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GraveContainerBlockEntity(pos, state);
    }
}
