package io.github.mikip98.savethehotbar.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;

public class MobHeadGrave extends GraveContainer {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    protected static final VoxelShape voxelShapeNorth;
    protected static final VoxelShape voxelShapeSouth;
    protected static final VoxelShape voxelShapeWest;
    protected static final VoxelShape voxelShapeEast;
    protected static final VoxelShape collisionShapeNorth;
    protected static final VoxelShape collisionShapeSouth;
    protected static final VoxelShape collisionShapeWest;
    protected static final VoxelShape collisionShapeEast;
    static {
        final float heightMax = 6f;
        final float heightMin = -4f;
        final float lengthMax = 14f;
        final float lengthMin = 2f;
        final float widthMax = 13f;
        final float widthMin = 3f;
        final float directionOffset = 2f;
        voxelShapeNorth = Block.createCuboidShape(widthMin, heightMin, lengthMin + directionOffset, widthMax, heightMax, lengthMax);
        voxelShapeSouth = Block.createCuboidShape(widthMin, heightMin, lengthMin, widthMax, heightMax, lengthMax - directionOffset);
        voxelShapeWest = Block.createCuboidShape(lengthMin + directionOffset, heightMin, widthMin, lengthMax, heightMax, widthMax);
        voxelShapeEast = Block.createCuboidShape(lengthMin, heightMin, widthMin, lengthMax - directionOffset, heightMax, widthMax);
        final float collisionHeightMax = 4f;
        final float collisionHeightMin = -3f;
        final float collisionSizeDelta = 3f;
        collisionShapeNorth = Block.createCuboidShape(
                widthMin + collisionSizeDelta, collisionHeightMin, lengthMin + directionOffset + collisionSizeDelta,
                widthMax - collisionSizeDelta, collisionHeightMax, lengthMax - collisionSizeDelta
        );
        collisionShapeSouth = Block.createCuboidShape(
                widthMin + collisionSizeDelta, collisionHeightMin, lengthMin + collisionSizeDelta,
                widthMax - collisionSizeDelta, collisionHeightMax, lengthMax - directionOffset - collisionSizeDelta
        );
        collisionShapeWest = Block.createCuboidShape(
                lengthMin + directionOffset + collisionSizeDelta, collisionHeightMin, widthMin + collisionSizeDelta,
                lengthMax - collisionSizeDelta, collisionHeightMax, widthMax - collisionSizeDelta
        );
        collisionShapeEast = Block.createCuboidShape(
                lengthMin + collisionSizeDelta, collisionHeightMin, widthMin + collisionSizeDelta,
                lengthMax - directionOffset - collisionSizeDelta, collisionHeightMax, widthMax - collisionSizeDelta
        );
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    public MobHeadGrave(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case NORTH -> voxelShapeNorth;
            case SOUTH -> voxelShapeSouth;
            case WEST -> voxelShapeWest;
            case EAST -> voxelShapeEast;
            default -> super.getOutlineShape(state, world, pos, context);
        };
    }
    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case NORTH -> collisionShapeNorth;
            case SOUTH -> collisionShapeSouth;
            case WEST -> collisionShapeWest;
            case EAST -> collisionShapeEast;
            default -> super.getCollisionShape(state, world, pos, context);
        };
    }

    @Override
    public @NotNull BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx)
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}
