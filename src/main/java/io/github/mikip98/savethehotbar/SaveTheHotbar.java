package io.github.mikip98.savethehotbar;

import io.github.mikip98.savethehotbar.content.blockentities.GraveContainerBlockEntity;
import io.github.mikip98.savethehotbar.content.blocks.MobHeadGrave;
import io.github.mikip98.savethehotbar.content.blocks.Sack;
import io.github.mikip98.savethehotbar.config.io.ConfigReader;
import io.github.mikip98.savethehotbar.modDetection.SupportedGraveMods;
import io.github.mikip98.savethehotbar.registries.PneumonoGravestonesCallbackRegistry;
import io.github.mikip98.savethehotbar.registries.itemTypeRegistry.ItemTypesConfiguration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveTheHotbar implements ModInitializer {
	public static final String MOD_ID = "savethehotbar";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Block SACK;
	public static Block SKELETON_HEAD_GRAVE;
	public static Block ZOMBIE_HEAD_GRAVE;

	public static BlockEntityType<GraveContainerBlockEntity> GRAVE_CONTAINER_BLOCK_ENTITY;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("SaveTheHotbar! has been initialized!");

		// Load the configuration
		ConfigReader.loadConfigFromFile();

		// Block Registration
		final BlockBehaviour.Properties universalSettings = BlockBehaviour.Properties.of().strength(0.333F, Float.MAX_VALUE).noOcclusion();

		SACK = registerWithItem(new Sack(universalSettings), "sack");
		SKELETON_HEAD_GRAVE = registerWithItem(new MobHeadGrave(universalSettings), "skeleton_head_grave");
		ZOMBIE_HEAD_GRAVE = registerWithItem(new MobHeadGrave(universalSettings), "zombie_head_grave");

		// Register Sack Block Entity
		GRAVE_CONTAINER_BLOCK_ENTITY = Registry.register(
				BuiltInRegistries.BLOCK_ENTITY_TYPE,
				getId("sack_block_entity"),
				FabricBlockEntityTypeBuilder.create(
						GraveContainerBlockEntity::new,
						SACK, SKELETON_HEAD_GRAVE, ZOMBIE_HEAD_GRAVE
				).build()
		);


		ItemTypesConfiguration.registerConfiguration();


		// Register Pneumono Gravestones Callbacks
		if (SupportedGraveMods.PNEUMONO_GRAVESTONES.isLoaded())
			PneumonoGravestonesCallbackRegistry.register();
	}

	protected static Block registerWithItem(Block block, String id) {
		Registry.register(BuiltInRegistries.BLOCK, getId(id), block);
		Registry.register(BuiltInRegistries.ITEM, getId(id), new BlockItem(block, new Item.Properties()));
		return block;
	}

	public static ResourceLocation getId(String name) {
		return new ResourceLocation(MOD_ID, name);
	}
}