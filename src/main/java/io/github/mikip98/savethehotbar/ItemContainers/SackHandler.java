package io.github.mikip98.savethehotbar.ItemContainers;

import io.github.mikip98.savethehotbar.SaveTheHotbar;
import io.github.mikip98.savethehotbar.blockentities.SackBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class SackHandler {

    public static void spawn_sack(World world, BlockPos position, ArrayList<ItemStack> drop) {
        world.setBlockState(position, SaveTheHotbar.SACK.getDefaultState(), 3);
        BlockEntity blockEntity = world.getBlockEntity(position);
        if (blockEntity instanceof SackBlockEntity) {
            ((SackBlockEntity) blockEntity).setItems(drop);
        }
    }
}
