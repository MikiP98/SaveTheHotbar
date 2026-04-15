package io.github.mikip98.savethehotbar;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public class SaveTheHotbarClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(SaveTheHotbar.SKELETON_HEAD_GRAVE, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SaveTheHotbar.ZOMBIE_HEAD_GRAVE, RenderType.cutout());
	}
}