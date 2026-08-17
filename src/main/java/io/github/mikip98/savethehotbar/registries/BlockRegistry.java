package io.github.mikip98.savethehotbar.registries;

import io.github.mikip98.savethehotbar.content.blocks.MobHeadGrave;
import io.github.mikip98.savethehotbar.content.blocks.Sack;
import io.mikip98.humilityval.registries.BlockRegistrar;
import io.mikip98.humilityval.registries.ItemRegistrar;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.MOD_ID;

public final class BlockRegistry {
    public static final Block SACK;
    public static final Block SKELETON_HEAD_GRAVE;
    public static final Block ZOMBIE_HEAD_GRAVE;

    static {
        final BlockRegistrar registrar = new BlockRegistrar(MOD_ID);

        final Supplier<BlockBehaviour.Properties> universalSettingsSupplier = () ->
                BlockBehaviour.Properties.of().strength(0.333F, Float.MAX_VALUE).noOcclusion();

        SACK = registrar.registerWithItem("sack", Sack::new, universalSettingsSupplier);
        SKELETON_HEAD_GRAVE = registrar.registerWithItem("skeleton_head_grave", MobHeadGrave::new, universalSettingsSupplier);
        ZOMBIE_HEAD_GRAVE = registrar.registerWithItem("zombie_head_grave", MobHeadGrave::new, universalSettingsSupplier);
    }

    public static void init() {}
}
