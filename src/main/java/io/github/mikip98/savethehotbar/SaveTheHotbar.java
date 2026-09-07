package io.github.mikip98.savethehotbar;

import io.github.mikip98.savethehotbar.config.ModConfig;
import io.github.mikip98.savethehotbar.content.blockentities.GraveContainerBlockEntity;
import io.github.mikip98.savethehotbar.modDetection.SupportedGraveMods;
import io.github.mikip98.savethehotbar.registries.BlockEntityRegistry;
import io.github.mikip98.savethehotbar.registries.BlockRegistry;
import io.github.mikip98.savethehotbar.registries.EventRegistry;
import io.github.mikip98.savethehotbar.registries.PneumonoGravestonesCallbackRegistry;
import io.github.mikip98.savethehotbar.registries.itemTypeRegistry.ItemTypesConfiguration;
import io.mikip98.humilityval.Util;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.entity.BlockEntity;
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

	@Override
	public void onInitialize() {
		LOGGER.info("SaveTheHotbar! has been initialized!");

		// Config
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
		ModConfig.INSTANCE = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

		EventRegistry.register();
		BlockRegistry.init();
		BlockEntityRegistry.init();

		ItemTypesConfiguration.registerConfiguration();

		// Pneumono Gravestones Callbacks
		if (SupportedGraveMods.PNEUMONO_GRAVESTONES.isLoaded()) {
			PneumonoGravestonesCallbackRegistry.register();
		}
	}

	public static #if MC_VERSION < 12111 ResourceLocation #else Identifier #endif getId(String name) {
		return Util.getId(MOD_ID, name);
	}
}