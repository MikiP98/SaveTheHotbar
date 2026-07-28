package io.github.mikip98.savethehotbar;

import io.github.mikip98.savethehotbar.config.ClothConfigGUIRegistry;
import io.github.mikip98.savethehotbar.registries.BlockRegistry;
import io.mikip98.humilityval.client.registries.render_layer.BlockRenderLayerRegistryUtil;
import net.fabricmc.api.ClientModInitializer;

public class SaveTheHotbarClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClothConfigGUIRegistry.register();
		BlockRenderLayerRegistryUtil.putBlocksInCutoutMipped(
				BlockRegistry.SKELETON_HEAD_GRAVE, BlockRegistry.ZOMBIE_HEAD_GRAVE
		);
	}
}
