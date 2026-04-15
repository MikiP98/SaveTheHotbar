package io.github.mikip98.savethehotbar.content.blocks;

import io.github.mikip98.savethehotbar.content.blockentities.GraveContainerBlockEntity;
import io.github.mikip98.savethehotbar.deathProcessing.DeathManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class GraveContainer extends Block implements EntityBlock, SimpleWaterloggedBlock {
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    public GraveContainer(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
                .setValue(BlockStateProperties.WATERLOGGED, false)
        );
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(
                BlockStateProperties.WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        LOGGER.info("Grave state change detected!");
        if (state.getBlock() != newState.getBlock()) {
            LOGGER.info("Grave has been destroyed!");
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GraveContainerBlockEntity graveContainerBlockEntity) {
                LOGGER.info("Dropping '{}' items", graveContainerBlockEntity.itemCount());
                for (ItemStack stack : graveContainerBlockEntity.getItems()) {
                    if (!stack.isEmpty()) Block.popResource(world, pos, stack);
                }
                final int exp = graveContainerBlockEntity.getExp();
                LOGGER.info("Dropping '{}' exp", exp);
                if (exp > 0) {
                    DeathManager.dropEXP(exp, world, world.getRandom(), pos);
                }
            }
        }
        super.onRemove(state, world, pos, newState, moved);
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GraveContainerBlockEntity(pos, state);
    }
}
