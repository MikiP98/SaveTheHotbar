package io.github.mikip98.savethehotbar;

import io.github.mikip98.savethehotbar.blockentities.GraveContainerBlockEntity;
import io.github.mikip98.savethehotbar.blocks.MobHeadGrave;
import io.github.mikip98.savethehotbar.blocks.Sack;
import io.github.mikip98.savethehotbar.config.io.ConfigReader;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
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
		final AbstractBlock.Settings universalSettings = FabricBlockSettings.create().strength(0.333F, Float.MAX_VALUE).nonOpaque();

		SACK = registerWithItem(new Sack(universalSettings), "sack");
		SKELETON_HEAD_GRAVE = registerWithItem(new MobHeadGrave(universalSettings), "skeleton_head_grave");
		ZOMBIE_HEAD_GRAVE = registerWithItem(new MobHeadGrave(universalSettings), "zombie_head_grave");

		// Register Sack Block Entity
		GRAVE_CONTAINER_BLOCK_ENTITY = Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				getId("sack_block_entity"),
				FabricBlockEntityTypeBuilder.create(
						GraveContainerBlockEntity::new,
						SACK, SKELETON_HEAD_GRAVE, ZOMBIE_HEAD_GRAVE
				).build()
		);
	}

	protected static Block registerWithItem(Block block, String id) {
		Registry.register(Registries.BLOCK, getId(id), block);
		Registry.register(Registries.ITEM, getId(id), new BlockItem(block, new FabricItemSettings()));
		return block;
	}

	public static Identifier getId(String name) {
		return new Identifier(MOD_ID, name);
	}
}