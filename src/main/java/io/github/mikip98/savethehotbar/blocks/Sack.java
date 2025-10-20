package io.github.mikip98.savethehotbar.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class Sack extends GraveContainer {
    public Sack(Settings settings) { super(settings); }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.3125, 0.0, 0.3125, 0.6875, 0.375, 0.6875);
    }
}
