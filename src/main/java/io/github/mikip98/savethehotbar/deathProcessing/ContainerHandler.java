package io.github.mikip98.savethehotbar.deathProcessing;

import io.github.mikip98.savethehotbar.SaveTheHotbar;
import io.github.mikip98.savethehotbar.content.blockentities.GraveContainerBlockEntity;
import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.modDetection.SupportedGraveMods;
import io.github.mikip98.savethehotbar.modDetection.SupportedSlotMods;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class ContainerHandler {
    protected final Level world;
    protected final Player player;
    protected final BlockPos position;

    protected final List<ItemStack> vanillaDrop;
    protected final List<Integer> vanillaSlotIds;
    protected final Map<SupportedSlotMods, List<ItemStack>> moddedDrop;
    protected final int exp;

    protected final DeathManager.ItemDropper rawItemDropFunction;

    protected void dropItem(ItemStack stack) {
        rawItemDropFunction.dropItem(stack, ModConfig.randomSpread, false);
    }


    public ContainerHandler(
            Player player,
            SlotHandler.NonKeptItems nonKeptItems,
            int exp,
            DeathManager.ItemDropper rawItemDropFunction
    ) {
        this.world = player.level();
        this.player = player;
        this.position = validatePositionHeight(this.world, player.blockPosition());

        this.vanillaDrop = nonKeptItems.vanillaDrop();
        this.vanillaSlotIds = nonKeptItems.vanillaSlotIds();
        this.moddedDrop = nonKeptItems.moddedDrop();
        this.exp = exp;
        this.rawItemDropFunction = rawItemDropFunction;
    }


    public void handleDrop() {
        if (vanillaDrop.isEmpty() && moddedDrop.values().stream().allMatch(List::isEmpty) && exp == 0) {
            LOGGER.info("No items nor exp to store or drop, not spawning any graves nor dropping anything");
            return;
        }
        LOGGER.info("Handling drop...");
        if (ModConfig.containDrop) spawnGrave();
        else {
            // Drop all items
            final String message = "Dropping inventory at " + position;
            LOGGER.info(message);
            if (ModConfig.logDeathCoordinatesInChat) player.sendSystemMessage(Component.nullToEmpty(message));

            for (ItemStack stack : Stream.concat(vanillaDrop.stream(), moddedDrop.values().stream().flatMap(List::stream)).toList()) {
                dropItem(stack);
            }
        }
        LOGGER.info("Drop has been handled");
    }

    protected void spawnGrave() {
        switch (ModConfig.containDropMode) {
            case SACK -> spawnSack();
            case SKELETON_HEAD -> spawnHeadGrave(SaveTheHotbar.SKELETON_HEAD_GRAVE);
            case ZOMBIE_HEAD -> spawnHeadGrave(SaveTheHotbar.ZOMBIE_HEAD_GRAVE);
            case RANDOM_HEAD -> {
                Block head = player.getRandom().nextFloat() < 0.5 ? SaveTheHotbar.SKELETON_HEAD_GRAVE : SaveTheHotbar.ZOMBIE_HEAD_GRAVE;
                spawnHeadGrave(head);
            }
            case GRAVE -> {
                if (!SupportedGraveMods.PNEUMONO_GRAVESTONES.isLoaded()) {
                    final String message = "ERROR: Gravestones mod by 'Pneumono_' is not installed or is disabled. Please download it from https://modrinth.com/mod/pneumono_gravestones; Spawning a Sack instead.";
                    LOGGER.error(message);
                    player.displayClientMessage(Component.literal(message).withStyle(ChatFormatting.RED), false);
                    spawnSack();
                }
            }
        }
    }

    protected void spawnSack() {
        // First -> Replaceable block in radius
        // Second -> Non-indestructible block in radius
        // Third -> The exact death position
        final BlockPos sackPos = findSafestSackLocation();
        world.setBlock(sackPos, SaveTheHotbar.SACK.defaultBlockState(), 3);
        LOGGER.info("Spawned a sack at {}", sackPos);
        if (ModConfig.logGraveCoordinatesInChat) {
            player.sendSystemMessage(Component.literal("Grave coordinates: " + sackPos).withStyle(ChatFormatting.AQUA));
        }
        fillGrave(sackPos);
    }

    protected void spawnHeadGrave(Block head) {
        final BlockPos gravePos = findValidHeadGraveLocation();
        if (gravePos == null) {
            LOGGER.error("Couldn't find a valid position for the head grave! Spawning a sack in the place of death instead!");
            spawnSack();
        } else {
            final Direction facing = Direction.from2DDataValue(world.getRandom().nextIntBetweenInclusive(0, 3));
            world.setBlock(gravePos, head.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing), 3);
            LOGGER.info("Spawned a mob head grave at {}", gravePos);
            if (ModConfig.logGraveCoordinatesInChat) {
                player.sendSystemMessage(Component.literal("Grave coordinates: " + gravePos).withStyle(ChatFormatting.AQUA));
            }
            fillGrave(gravePos);
        }
    }

    protected void fillGrave(BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof GraveContainerBlockEntity graveContainerBlockEntity) {
            graveContainerBlockEntity.setItems(vanillaDrop, moddedDrop);
            graveContainerBlockEntity.setExp(exp);
        }
        else handleNoItemContainerError(pos);
    }

    protected void handleNoItemContainerError(BlockPos position) {
        String message = "Couldn't find the spawned item container! Items will be dropped :(  EMERGENCY HOPPERS SPAWN ATTEMPT!!!";
        player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.RED));
        LOGGER.error(message);

        // Spawn emergency hoppers (3x3 as 1x1 was too small to catch all the items)
        final BlockPos[] posArray = new BlockPos[]{
                position.below(),
                position.below().north(),
                position.below().south(),
                position.below().east(),
                position.below().west(),
                position.below().north().east(),
                position.below().north().west(),
                position.below().south().east(),
                position.below().south().west()
        };
        for (BlockPos pos : posArray) {
            // If a block is indestructible, don't replace it
            // Couple missing items are most likely better than a broken minecraft world
            final Block block = world.getBlockState(pos).getBlock();
            if (block.defaultDestroyTime() == -1) continue;
            world.setBlock(pos, Blocks.HOPPER.defaultBlockState(), 3);
        }

        // Drop items
        message = "Dropping " + vanillaDrop.size() + moddedDrop.values().stream().mapToInt(List::size).sum() + " items at " + position;
        player.sendSystemMessage(Component.literal(message));
        LOGGER.info(message);
        for (ItemStack item : Stream.concat(vanillaDrop.stream(), moddedDrop.values().stream().flatMap(List::stream)).toList()) {
            world.addFreshEntity(new ItemEntity(world, position.getX(), position.getY(), position.getZ(), item));
        }
    }

    /**
     * Valid Head Grave location is a solid full block with a replaceable block on top
     */
    protected @Nullable BlockPos findValidHeadGraveLocation() {
        return findValidSpawnPosition(
                position, ModConfig.mobGraveMaxSpawnRadius,
                (position1) -> {
                    if (fitsInHeight(world, position1) && world.getBlockState(position1).canBeReplaced()) {
                        BlockPos downPos = position1.below();
                        // fitsInHeight(...) check has a 1 block offset so that the items don't all into the void after the grave is destroyed
                        // Because of that the check should not be run on the block pos below the grave as the block is guaranteed to exist,
                        // and the lowest block in the world should be a valid spawn location
                        BlockState blockState = world.getBlockState(downPos);
                        return blockState != null && (blockState.isCollisionShapeFullBlock(world, downPos) || isTop(blockState, downPos)) && blockState.canOcclude();
                    }
                    return false;
                }
        );
    }
    protected boolean isTop(BlockState blockState, BlockPos pos) {
        // TODO: If configured, allow the grave to spawn if block is a top half block
        if (ModConfig.allowGravesToSpawnOnSlabs && blockState.getProperties().contains(BlockStateProperties.HALF)) {
            final VoxelShape collisionShape = blockState.getCollisionShape(world, pos);
            return blockState.getValue(BlockStateProperties.HALF) == Half.TOP
                    && collisionShape.max(Direction.Axis.X) == 1.0d
                    && collisionShape.min(Direction.Axis.X) == 0.0d
                    && collisionShape.max(Direction.Axis.Z) == 1.0d
                    && collisionShape.min(Direction.Axis.Z) == 1.0d
                    && collisionShape.min(Direction.Axis.Y) <= .5d;
        }
        return false;
    }

    /**
     * Finds the safest Sack spawning location around the initial position with a radius of 'ModConfig.sackMaxSpawnRadius'
     * 1. The safest location is one with a replaceable block (e.g. Air, Short Grass, Tall Grass)
     * 2. The Second-safest location will be one with a non-instructive block in it (a.k.a. not Bedrock, not End Portal Frame)
     * 3. The Third and last location, if the 2 above fail, will just be the given starting position
     * All positions also have to fit inside the building limit
     * @return the safest possible sack location
     */
    protected @NotNull BlockPos findSafestSackLocation() {
        BlockPos pos = findValidSpawnPosition(
                position, ModConfig.sackMaxSpawnRadius,
                (position1 -> fitsInHeight(world, position1) && world.getBlockState(position1).canBeReplaced())
        );
        if (pos == null) pos = findValidSpawnPosition(
                position, ModConfig.sackMaxSpawnRadius,
                (position1 -> fitsInHeight(world, position1) && world.getBlockState(position1).getBlock().defaultDestroyTime() != -1)
        );
        return pos != null ? pos : position;
    }

    protected static @Nullable BlockPos findValidSpawnPosition(BlockPos startingPosition, int range, PositionValidator positionValidator) {
        if (positionValidator.validate(startingPosition)) {
            return startingPosition;
        }

        int xOffset = 1;
        int yOffset = 0;
        int zOffset = 0;

        BlockPos temporalPosition;

        while (true) {
            temporalPosition = startingPosition.offset(xOffset, yOffset, zOffset);

            if (positionValidator.validate(temporalPosition)) {
                return temporalPosition;
            }

            if (xOffset > 0) {
                xOffset = -xOffset;
            } else if (yOffset > 0) {
                yOffset = -yOffset;
                xOffset = -xOffset;
            } else if (zOffset > 0) {
                zOffset = -zOffset;
                yOffset = -yOffset;
                xOffset = -xOffset;
            } else {
                xOffset = -xOffset;
                yOffset = -yOffset;
                zOffset = -zOffset;

                if (xOffset <= yOffset && xOffset <= zOffset) {
                    ++xOffset;
                    if (xOffset > range) return null;
                } else {
                    if (yOffset <= zOffset) {
                        ++yOffset;
                    } else {
                        ++zOffset;
                        yOffset = 0;
                    }
                    xOffset = 0;
                }
            }
        }
    }
    @FunctionalInterface
    protected interface PositionValidator {
        boolean validate(BlockPos position);
    }

    protected static BlockPos validatePositionHeight(Level world, BlockPos position) {
        if (position.getY() < world.getMinBuildHeight()) {
            // +1 so that the recovered items won't just fall to the void
            position = new BlockPos(position.getX(), world.getMinBuildHeight() + 1, position.getZ());
        } else if (position.getY() > world.getMaxBuildHeight()) {
            position = new BlockPos(position.getX(), world.getMaxBuildHeight() - 1, position.getZ());
        }
        return position;
    }
    protected static boolean fitsInHeight(Level world, BlockPos position) {
        final int y = position.getY();
        return y > world.getMinBuildHeight() && y < world.getMaxBuildHeight();
    }
}
