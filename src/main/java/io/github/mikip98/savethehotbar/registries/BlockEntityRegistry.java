package io.github.mikip98.savethehotbar.registries;

import io.github.mikip98.savethehotbar.content.blockentities.GraveContainerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static io.github.mikip98.savethehotbar.SaveTheHotbar.getId;

public class BlockEntityRegistry {
    public static final BlockEntityType<GraveContainerBlockEntity> GRAVE_CONTAINER_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            getId("sack_block_entity"),
            FabricBlockEntityTypeBuilder.create(
                    GraveContainerBlockEntity::new,
                    BlockRegistry.SACK,
                    BlockRegistry.SKELETON_HEAD_GRAVE,
                    BlockRegistry.ZOMBIE_HEAD_GRAVE
            ).build()
    );

    public static void init() {}
}
