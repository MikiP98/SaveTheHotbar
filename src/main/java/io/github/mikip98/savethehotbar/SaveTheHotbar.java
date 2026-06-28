package io.github.mikip98.savethehotbar;

import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.content.blockentities.GraveContainerBlockEntity;
import io.github.mikip98.savethehotbar.content.blocks.MobHeadGrave;
import io.github.mikip98.savethehotbar.content.blocks.Sack;
#if MC_VERSION >= 12104
import io.github.mikip98.savethehotbar.mcVersionAgnosticUtils.SettingsDuplicator;
#endif
import io.github.mikip98.savethehotbar.modDetection.SupportedGraveMods;
import io.github.mikip98.savethehotbar.registries.EventRegistry;
import io.github.mikip98.savethehotbar.registries.PneumonoGravestonesCallbackRegistry;
import io.github.mikip98.savethehotbar.registries.itemTypeRegistry.ItemTypesConfiguration;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
#if MC_VERSION >= 12104
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
#endif
import net.minecraft.world.item.Item;
#if MC_VERSION >= 12104
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
#endif
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
#if MC_VERSION < 12111
import net.minecraft.resources.ResourceLocation;
#else
import net.minecraft.resources.Identifier;
#endif
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class SaveTheHotbar implements ModInitializer {
	public static final String MOD_ID = "savethehotbar";
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

		// Register and load the configuration
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
		ModConfig.INSTANCE = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

		// Event registry
		EventRegistry.register();

		// Block Registration
		final BlockBehaviour.Properties universalSettings = BlockBehaviour.Properties.of().strength(0.333F, Float.MAX_VALUE).noOcclusion();

		SACK = registerWithItem("sack", Sack::new, universalSettings);
		SKELETON_HEAD_GRAVE = registerWithItem("skeleton_head_grave", MobHeadGrave::new, universalSettings);
		ZOMBIE_HEAD_GRAVE = registerWithItem("zombie_head_grave", MobHeadGrave::new, universalSettings);

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
		if (SupportedGraveMods.PNEUMONO_GRAVESTONES.isLoaded()) {
			PneumonoGravestonesCallbackRegistry.register();
		}
	}


	public static Item registerItem(String name, Function<Item.Properties, Item> factory) {
		return registerItem(name, factory, new Item.Properties());
	}
	#if MC_VERSION < 12104
    public static Item registerItem(String name, Function<Item.Properties, Item> factory, Item.Properties settings) {
        return Registry.register(BuiltInRegistries.ITEM, getId(name), factory.apply(settings));
    }
    #else
	public static Item registerItem(String name, Function<Item.Properties, Item> factory, Item.Properties settings) {
		final ResourceKey<Item> registryKey = ResourceKey.create(Registries.ITEM, getId(name));
		return #if MC_VERSION < 260000 Items. #endif registerItem(registryKey, factory, settings);
	}
    #endif
    #if MC_VERSION > 260000
    protected static Item registerItem(final ResourceKey<Item> key, final Function<Item.Properties, Item> itemFactory, final Item.Properties properties) {
        Item item = (Item) itemFactory.apply(properties.setId(key));
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }

        return (Item) Registry.register(BuiltInRegistries.ITEM, key, item);
    }
    #endif


	#if MC_VERSION < 260000
	public static <T extends Block> T registerWithItem(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties settings) {
		T block = register(name, blockFactory, settings);
		registerItem(name, (itemSettings) -> new BlockItem(block, itemSettings));
		return block;
	}
	public static Block register(String name, BlockBehaviour.Properties settings) {
		return register(name, Block::new, settings);
	}
	#if MC_VERSION >= 12104 @SuppressWarnings("unchecked") #endif
	public static <T extends Block> T register(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties settings) {
        #if MC_VERSION < 12104
        return Registry.register(BuiltInRegistries.BLOCK, getId(name), blockFactory.apply(settings));
        #else
		final BlockBehaviour.Properties settingsCopy = SettingsDuplicator.copy(settings);
		return (T) Blocks.register(keyOfBlock(name), (Function<BlockBehaviour.Properties, Block>) blockFactory, settingsCopy);
        #endif
	}
    #else
    public static <T extends Block> T registerWithItem(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties settings) {
        T block = register(name, blockFactory, settings);
        registerItem(name, (itemSettings) -> new BlockItem(block, itemSettings));
        return block;
    }
    @SuppressWarnings("unchecked")
    public static <T extends Block> T register(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties settings) {
        final BlockBehaviour.Properties settingsCopy = SettingsDuplicator.copy(settings);
        return (T) Blocks.register(keyOfBlock(name), (Function<BlockBehaviour.Properties, Block>) blockFactory, settingsCopy);
    }
    #endif


	#if MC_VERSION >= 12104
	public static ResourceKey<Block> keyOfBlock(String name) {
		return ResourceKey.create(Registries.BLOCK, getId(name));
	}
    #endif



//	protected static Block registerWithItem(Block block, String id) {
//		Registry.register(BuiltInRegistries.BLOCK, getId(id), block);
//		Registry.register(BuiltInRegistries.ITEM, getId(id), new BlockItem(block, new Item.Properties()));
//		return block;
//	}

	public static #if MC_VERSION < 12111 ResourceLocation #else Identifier #endif getId(String name) {
		final #if MC_VERSION < 12111 ResourceLocation #else Identifier #endif id = #if MC_VERSION < 12111 ResourceLocation #else Identifier #endif .tryBuild(MOD_ID, name);
		if (id == null) throw new IllegalArgumentException("Broken block id: " + MOD_ID + ":" + name);
		return id;
	}
}