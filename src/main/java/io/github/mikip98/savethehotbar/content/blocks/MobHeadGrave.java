package io.github.mikip98.savethehotbar.content.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.NotNull;

public class MobHeadGrave extends GraveContainer {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

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
        voxelShapeNorth = Block.box(widthMin, heightMin, lengthMin + directionOffset, widthMax, heightMax, lengthMax);
        voxelShapeSouth = Block.box(widthMin, heightMin, lengthMin, widthMax, heightMax, lengthMax - directionOffset);
        voxelShapeWest = Block.box(lengthMin + directionOffset, heightMin, widthMin, lengthMax, heightMax, widthMax);
        voxelShapeEast = Block.box(lengthMin, heightMin, widthMin, lengthMax - directionOffset, heightMax, widthMax);
        final float collisionHeightMax = 4f;
        final float collisionHeightMin = -3f;
        final float collisionSizeDelta = 3f;
        collisionShapeNorth = Block.box(
                widthMin + collisionSizeDelta, collisionHeightMin, lengthMin + directionOffset + collisionSizeDelta,
                widthMax - collisionSizeDelta, collisionHeightMax, lengthMax - collisionSizeDelta
        );
        collisionShapeSouth = Block.box(
                widthMin + collisionSizeDelta, collisionHeightMin, lengthMin + collisionSizeDelta,
                widthMax - collisionSizeDelta, collisionHeightMax, lengthMax - directionOffset - collisionSizeDelta
        );
        collisionShapeWest = Block.box(
                lengthMin + directionOffset + collisionSizeDelta, collisionHeightMin, widthMin + collisionSizeDelta,
                lengthMax - collisionSizeDelta, collisionHeightMax, widthMax - collisionSizeDelta
        );
        collisionShapeEast = Block.box(
                lengthMin + collisionSizeDelta, collisionHeightMin, widthMin + collisionSizeDelta,
                lengthMax - directionOffset - collisionSizeDelta, collisionHeightMax, widthMax - collisionSizeDelta
        );
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    public MobHeadGrave(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> voxelShapeNorth;
            case SOUTH -> voxelShapeSouth;
            case WEST -> voxelShapeWest;
            case EAST -> voxelShapeEast;
            default -> super.getShape(state, world, pos, context);
        };
    }
    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> collisionShapeNorth;
            case SOUTH -> collisionShapeSouth;
            case WEST -> collisionShapeWest;
            case EAST -> collisionShapeEast;
            default -> super.getCollisionShape(state, world, pos, context);
        };
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return super.getStateForPlacement(ctx)
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
