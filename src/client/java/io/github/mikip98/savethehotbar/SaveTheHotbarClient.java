package io.github.mikip98.savethehotbar;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class SaveTheHotbarClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockRenderLayerMap.INSTANCE.putBlock(SaveTheHotbar.SKELETON_HEAD_GRAVE, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SaveTheHotbar.ZOMBIE_HEAD_GRAVE, RenderLayer.getCutout());
	}
}