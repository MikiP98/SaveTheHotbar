package io.github.mikip98.savethehotbar;

import io.github.mikip98.savethehotbar.blockentities.GraveContainerBlockEntity;
import io.github.mikip98.savethehotbar.blocks.MobHeadGrave;
import io.github.mikip98.savethehotbar.blocks.Sack;
import io.github.mikip98.savethehotbar.config.io.ConfigReader;
import io.github.mikip98.savethehotbar.modDetection.SupportedGraveMods;
import io.github.mikip98.savethehotbar.registries.PneumonoGravestonesCallbackRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

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
		final AbstractBlock.Settings universalSettings = AbstractBlock.Settings.create().strength(0.333F, Float.MAX_VALUE).nonOpaque();

		SACK = registerWithItem("sack", Sack::new, universalSettings);
		SKELETON_HEAD_GRAVE = registerWithItem("skeleton_head_grave", MobHeadGrave::new, universalSettings);
		ZOMBIE_HEAD_GRAVE = registerWithItem("zombie_head_grave", MobHeadGrave::new, universalSettings);

		// Register Sack Block Entity
		GRAVE_CONTAINER_BLOCK_ENTITY = Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				getId("sack_block_entity"),
				FabricBlockEntityTypeBuilder.create(
						GraveContainerBlockEntity::new,
						SACK, SKELETON_HEAD_GRAVE, ZOMBIE_HEAD_GRAVE
				).build()
		);


		// Register Pneumono Gravestones Callbacks
		if (SupportedGraveMods.PNEUMONO_GRAVESTONES.isLoaded())
			PneumonoGravestonesCallbackRegistry.register();
	}

	protected static <T extends Block> T registerWithItem(
			String name, Function<AbstractBlock.Settings, T> blockFactory, AbstractBlock.Settings settings
	) {
		T block = registerBlock(name, blockFactory, settings);
		registerBlockItem(name, block);
		return block;
	}

	#if MC_VERSION >= 12104 @SuppressWarnings("unchecked") #endif
	public static <T extends Block> T registerBlock(String name, Function<AbstractBlock.Settings, T> blockFactory, AbstractBlock.Settings settings) {
        #if MC_VERSION < 12104
        return Registry.register(Registries.BLOCK, getId(name), blockFactory.apply(settings));
        #else
		final AbstractBlock.Settings settingsCopy = SettingsDuplicator.copy(settings);
		return (T) Blocks.register(keyOfBlock(name), (Function<AbstractBlock.Settings, Block>) blockFactory, settingsCopy);
        #endif
	}

	#if MC_VERSION < 12104
    public static void registerBlockItem(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
        Registry.register(Registries.ITEM, getId(name), factory.apply(settings));
    }
    #else
	public static void registerBlockItem(String name, Block block) {
		final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, getId(name));
		Items.register(registryKey, (settings) -> new BlockItem(block, settings), new Item.Settings());
	}
    #endif

	#if MC_VERSION >= 12104
	public static RegistryKey<Block> keyOfBlock(String name) {
		return RegistryKey.of(RegistryKeys.BLOCK, getId(name));
	}
    #endif

	public static Identifier getId(String name) {
		return Identifier.of(MOD_ID, name);
	}
}