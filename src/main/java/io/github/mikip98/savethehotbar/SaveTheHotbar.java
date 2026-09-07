package io.github.mikip98.savethehotbar;

import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.content.blockentities.GraveContainerBlockEntity;
import io.github.mikip98.savethehotbar.modDetection.SupportedGraveMods;
import io.github.mikip98.savethehotbar.registries.BlockRegistry;
import io.github.mikip98.savethehotbar.registries.EventRegistry;
import io.github.mikip98.savethehotbar.registries.PneumonoGravestonesCallbackRegistry;
import io.github.mikip98.savethehotbar.registries.itemTypeRegistry.ItemTypesConfiguration;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
#if MC_VERSION < 12111
import net.minecraft.resources.ResourceLocation;
#else
import net.minecraft.resources.Identifier;
#endif
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveTheHotbar implements ModInitializer {
	public static final String MOD_ID = "savethehotbar";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static BlockEntityType<GraveContainerBlockEntity> GRAVE_CONTAINER_BLOCK_ENTITY;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("SaveTheHotbar! has been initialized!");

		// Register and load the configuration
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
		ModConfig.INSTANCE = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

		// Event registry
		EventRegistry.register();

		// Block Registration
		BlockRegistry.init();

		// Register Sack Block Entity
		GRAVE_CONTAINER_BLOCK_ENTITY = Registry.register(
				BuiltInRegistries.BLOCK_ENTITY_TYPE,
				getId("sack_block_entity"),
				FabricBlockEntityTypeBuilder.create(
						GraveContainerBlockEntity::new,
						BlockRegistry.SACK, BlockRegistry.SKELETON_HEAD_GRAVE, BlockRegistry.ZOMBIE_HEAD_GRAVE
				).build()
		);

		ItemTypesConfiguration.registerConfiguration();

		// Register Pneumono Gravestones Callbacks
		if (SupportedGraveMods.PNEUMONO_GRAVESTONES.isLoaded()) {
			PneumonoGravestonesCallbackRegistry.register();
		}
	}

	public static #if MC_VERSION < 12111 ResourceLocation #else Identifier #endif getId(String name) {
		final #if MC_VERSION < 12111 ResourceLocation #else Identifier #endif id = #if MC_VERSION < 12111 ResourceLocation #else Identifier #endif .tryBuild(MOD_ID, name);
		if (id == null) throw new IllegalArgumentException("Broken block id: " + MOD_ID + ":" + name);
		return id;
	}
}