package io.github.mikip98.savethehotbar;

import io.github.mikip98.savethehotbar.blocks.Sack;
import io.github.mikip98.savethehotbar.config.ConfigReader;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
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

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("SaveTheHotbar! has been initialized!");

		// Load the configuration
		ConfigReader.loadConfigFromFile();

		// Register Sack
		Block SACK = new Sack(FabricBlockSettings.create().strength(0.5F, Float.MAX_VALUE));
		net.minecraft.registry.Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "sack"), SACK);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "sack"), new BlockItem(SACK, new FabricItemSettings()));
	}
}