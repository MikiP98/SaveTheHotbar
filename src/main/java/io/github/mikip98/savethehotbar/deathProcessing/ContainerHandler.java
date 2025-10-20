package io.github.mikip98.savethehotbar.deathProcessing;

import io.github.mikip98.savethehotbar.SaveTheHotbar;
import io.github.mikip98.savethehotbar.blockentities.GraveContainerBlockEntity;
import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.deathProcessing.moddedGraveHandlers.GravestoneHandler;
import io.github.mikip98.savethehotbar.modDetection.SupportedGraveMods;
import io.github.mikip98.savethehotbar.modDetection.SupportedSlotMods;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.LOGGER;

public class ContainerHandler {
    protected final World world;
    protected final PlayerEntity player;
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
            PlayerEntity player,
            SlotHandler.NonKeptItems nonKeptItems,
            int exp,
            DeathManager.ItemDropper rawItemDropFunction
    ) {
        this.world = player.getWorld();
        this.player = player;
        this.position = validatePositionHeight(this.world, player.getBlockPos());

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
            if (ModConfig.logDeathCoordinatesInChat) player.sendMessage(Text.of(message));

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
                if (SupportedGraveMods.PNEUMONO_GRAVESTONES.isLoaded()) {
                    if (ModConfig.logGraveCoordinatesInChat)
                        player.sendMessage(Text.of("Grave coordinates: " + position));
                    GravestoneHandler.handleGravestones(player, vanillaDrop, vanillaSlotIds, moddedDrop, this::dropItem);
                } else {
                    String message = "ERROR: Gravestones mod by 'Pneumono_' is not installed or is disabled. Please download it from https://modrinth.com/mod/pneumono_gravestones; Spawning a Sack instead.";
                    LOGGER.error(message);
                    player.sendMessage(Text.literal(message).formatted(Formatting.RED));
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
        world.setBlockState(sackPos, SaveTheHotbar.SACK.getDefaultState(), 3);
        LOGGER.info("Spawned a sack at {}", sackPos);
        if (ModConfig.logGraveCoordinatesInChat) {
            player.sendMessage(Text.literal("Grave coordinates: " + sackPos).formatted(Formatting.AQUA));
        }
        fillGrave(sackPos);
    }

    protected void spawnHeadGrave(Block head) {
        final BlockPos gravePos = findValidHeadGraveLocation();
        if (gravePos == null) {
            LOGGER.error("Couldn't find a valid position for the head grave! Spawning a sack in the place of death instead!");
            spawnSack();
        } else {
            final Direction facing = Direction.fromHorizontal(world.getRandom().nextBetween(0, 3));
            world.setBlockState(gravePos, head.getDefaultState().with(Properties.HORIZONTAL_FACING, facing), 3);
            LOGGER.info("Spawned a mob head grave at {}", gravePos);
            if (ModConfig.logGraveCoordinatesInChat) {
                player.sendMessage(Text.literal("Grave coordinates: " + gravePos).formatted(Formatting.AQUA));
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
        player.sendMessage(Text.literal(message).formatted(Formatting.RED));
        LOGGER.error(message);

        // Spawn emergency hoppers (3x3 as 1x1 was too small to catch all the items)
        final BlockPos[] posArray = new BlockPos[]{
                position.down(),
                position.down().north(),
                position.down().south(),
                position.down().east(),
                position.down().west(),
                position.down().north().east(),
                position.down().north().west(),
                position.down().south().east(),
                position.down().south().west()
        };
        for (BlockPos pos : posArray) {
            // If a block is indestructible, don't replace it
            // Couple missing items are most likely better than a broken minecraft world
            final Block block = world.getBlockState(pos).getBlock();
            if (block.getHardness() == -1) continue;
            world.setBlockState(pos, Blocks.HOPPER.getDefaultState(), 3);
        }

        // Drop items
        message = "Dropping " + vanillaDrop.size() + moddedDrop.values().stream().mapToInt(List::size).sum() + " items at " + position;
        player.sendMessage(Text.literal(message));
        LOGGER.info(message);
        for (ItemStack item : Stream.concat(vanillaDrop.stream(), moddedDrop.values().stream().flatMap(List::stream)).toList()) {
            world.spawnEntity(new ItemEntity(world, position.getX(), position.getY(), position.getZ(), item));
        }
    }

    /**
     * Valid Head Grave location is a solid full block with a replaceable block on top
     */
    protected @Nullable BlockPos findValidHeadGraveLocation() {
        return findValidSpawnPosition(
                position, ModConfig.mobGraveMaxSpawnRadius,
                (position1) -> {
                    if (fitsInHeight(world, position1) && world.getBlockState(position1).isReplaceable()) {
                        BlockPos downPos = position1.down();
                        // fitsInHeight(...) check has a 1 block offset so that the items don't all into the void after the grave is destroyed
                        // Because of that the check should not be run on the block pos below the grave as the block is guaranteed to exist,
                        // and the lowest block in the world should be a valid spawn location
                        BlockState blockState = world.getBlockState(downPos);
                        return blockState != null && (blockState.isFullCube(world, downPos) || isTop(blockState, downPos)) && blockState.isOpaque();
                    }
                    return false;
                }
        );
    }
    protected boolean isTop(BlockState blockState, BlockPos pos) {
        // TODO: If configured, allow the grave to spawn if block is a top half block
        if (ModConfig.allowGravesToSpawnOnSlabs && blockState.getProperties().contains(Properties.BLOCK_HALF)) {
            final VoxelShape collisionShape = blockState.getCollisionShape(world, pos);
            return blockState.get(Properties.BLOCK_HALF) == BlockHalf.TOP
                    && collisionShape.getMax(Direction.Axis.X) == 1.0d
                    && collisionShape.getMin(Direction.Axis.X) == 0.0d
                    && collisionShape.getMax(Direction.Axis.Z) == 1.0d
                    && collisionShape.getMin(Direction.Axis.Z) == 1.0d
                    && collisionShape.getMin(Direction.Axis.Y) <= .5d;
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
                (position1 -> fitsInHeight(world, position1) && world.getBlockState(position1).isReplaceable())
        );
        if (pos == null) pos = findValidSpawnPosition(
                position, ModConfig.sackMaxSpawnRadius,
                (position1 -> fitsInHeight(world, position1) && world.getBlockState(position1).getBlock().getHardness() != -1)
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
            temporalPosition = startingPosition.add(xOffset, yOffset, zOffset);

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

    protected static BlockPos validatePositionHeight(World world, BlockPos position) {
        if (position.getY() < world.getBottomY()) {
            // +1 so that the recovered items won't just fall to the void
            position = new BlockPos(position.getX(), world.getBottomY() + 1, position.getZ());
        } else if (position.getY() > world.getTopY()) {
            position = new BlockPos(position.getX(), world.getTopY() - 1, position.getZ());
        }
        return position;
    }
    protected static boolean fitsInHeight(World world, BlockPos position) {
        final int y = position.getY();
        return y > world.getBottomY() && y < world.getTopY();
    }
}
