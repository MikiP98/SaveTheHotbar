package io.github.mikip98.savethehotbar.ItemContainers;

import com.terraformersmc.modmenu.util.mod.Mod;
import io.github.mikip98.savethehotbar.SaveTheHotbar;
import io.github.mikip98.savethehotbar.blockentities.GraveContainerBlockEntity;
import io.github.mikip98.savethehotbar.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class InternalContainersHandler {

    public static void spawn_sack(World world, BlockPos position, ArrayList<ItemStack> drop, PlayerEntity player) {
        position = validate_position_height(world, position);

        world.setBlockState(position, SaveTheHotbar.SACK.getDefaultState(), 3);
        LOGGER.info("Spawned a sack at " + position);
        if (ModConfig.logGraveCoordinatesInChat) {
            player.sendMessage(Text.literal("Grave coordinates: " + position.getX() + ", " + position.getY() + ", " + position.getZ()));
        }
        BlockEntity blockEntity = world.getBlockEntity(position);
        if (blockEntity instanceof GraveContainerBlockEntity) {
            ((GraveContainerBlockEntity) blockEntity).setItems(drop);
        } else handleNoItemContainerError(world, position, drop);
    }

    public static void spawn_skeleton_head_grave(World world, BlockPos position, ArrayList<ItemStack> drop, PlayerEntity player) {
        spawn_head_grave(SaveTheHotbar.SKELETON_HEAD_GRAVE, world, position, drop, player);
    }
    public static void spawn_zombie_head_grave(World world, BlockPos position, ArrayList<ItemStack> drop, PlayerEntity player) {
        spawn_head_grave(SaveTheHotbar.ZOMBIE_HEAD_GRAVE, world, position, drop, player);
    }

    public static void spawn_head_grave(Block head, World world, BlockPos position, ArrayList<ItemStack> drop, PlayerEntity player) {
        position = validate_position_height(world, position);

        BlockPos gravePos = find_closest_block_with_air_on_top(world, position);
        if (gravePos == null) {
            LOGGER.error("Couldn't find a valid position for the head grave! Spawning a sack in the place of death instead!");
            spawn_sack(world, position, drop, player);
        } else {
            Direction facing = Direction.random(world.random);

            world.setBlockState(gravePos, head.getDefaultState().with(Properties.HORIZONTAL_FACING, facing), 3);
            LOGGER.info("Spawned a mob head grave at " + gravePos);
            if (ModConfig.logGraveCoordinatesInChat) {
                player.sendMessage(Text.literal("Grave coordinates: " + gravePos.getX() + ", " + gravePos.getY() + ", " + gravePos.getZ()));
            }
            BlockEntity blockEntity = world.getBlockEntity(gravePos);
            if (blockEntity instanceof GraveContainerBlockEntity) {
                ((GraveContainerBlockEntity) blockEntity).setItems(drop);
            } else handleNoItemContainerError(world, gravePos, drop);
        }
    }

    protected static void handleNoItemContainerError(World world, BlockPos position, ArrayList<ItemStack> drop) {
        LOGGER.error("Couldn't find the spawned item container! Items will be dropped :(  EMERGENCY HOPPERS SPAWN ATTEMPT!!!");

        // Spawn emergency hoppers (3x3 as 1x1 was too small to catch all the items)
        world.setBlockState(position.down(), Blocks.HOPPER.getDefaultState(), 3);
        world.setBlockState(position.down().north(), Blocks.HOPPER.getDefaultState(), 3);
        world.setBlockState(position.down().south(), Blocks.HOPPER.getDefaultState(), 3);
        world.setBlockState(position.down().east(), Blocks.HOPPER.getDefaultState(), 3);
        world.setBlockState(position.down().west(), Blocks.HOPPER.getDefaultState(), 3);
        world.setBlockState(position.down().north().east(), Blocks.HOPPER.getDefaultState(), 3);
        world.setBlockState(position.down().north().west(), Blocks.HOPPER.getDefaultState(), 3);
        world.setBlockState(position.down().south().east(), Blocks.HOPPER.getDefaultState(), 3);
        world.setBlockState(position.down().south().west(), Blocks.HOPPER.getDefaultState(), 3);

        // Drop items
        LOGGER.info("Dropping " + drop.size() + " items at " + position);
        for (ItemStack item : drop) {
            world.spawnEntity(new ItemEntity(world, position.getX(), position.getY(), position.getZ(), item));
        }
    }

    protected static BlockPos validate_position_height(World world, BlockPos position) {
        if (position.getY() < world.getBottomY()) {
            // +1 so that the recovered items won't just fall to the void
            position = new BlockPos(position.getX(), world.getBottomY() + 1, position.getZ());
        } else if (position.getY() > world.getTopY()) {
            position = new BlockPos(position.getX(), world.getTopY() - 1, position.getZ());
        }

        return position;
    }

    protected static BlockPos find_closest_block_with_air_on_top(World world, BlockPos position) {
        if (world.getBlockState(position).isReplaceable()) {
            BlockState blockState = world.getBlockState(position.down());
            if (blockState.isFullCube(world, position) && blockState.isOpaque()) {
                return position;
            }
        }

        int
                x_offset = 1,
                y_offset = 0,
                z_offset = 0,
                limit = ModConfig.mobGraveMaxSpawnRadius;

        BlockPos temp_position;

        while (true) {
            temp_position = position.add(x_offset, y_offset, z_offset);

            if (world.getBlockState(temp_position).isReplaceable()) {
                BlockState blockState = world.getBlockState(temp_position.down());
                if (blockState != null) {
                    if ((blockState.isFullCube(world, temp_position) || is_top(blockState)) && blockState.isOpaque()) {
                        return temp_position;
                    }
                }
            }

            if (x_offset > 0) {
                x_offset = -x_offset;
            } else if (y_offset > 0) {
                y_offset = -y_offset;
                x_offset = -x_offset;
            } else if (z_offset > 0) {
                z_offset = -z_offset;
                y_offset = -y_offset;
                x_offset = -x_offset;
            } else {
                x_offset = -x_offset;
                y_offset = -y_offset;
                z_offset = -z_offset;

                if (x_offset <= y_offset && x_offset <= z_offset) {
                    ++x_offset;
                    if (x_offset > limit) {
                        return null;
                    }
                } else {
                    if (y_offset <= z_offset) {
                        ++y_offset;
                        x_offset = 0;
                    } else {
                        ++z_offset;
                        y_offset = 0;
                        x_offset = 0;
                    }
                }
            }
        }
    }

    protected static boolean is_top(BlockState blockState) {
        // If configured, allow the grave to spawn if block is a top half block
        return false;
    }
}
