#if MC_VERSION < 260000
package io.github.mikip98.savethehotbar;

import net.fabricmc.api.ClientModInitializer;
#if MC_VERSION < 12105
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
#else
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
#endif
import net.minecraft.world.level.block.Block;

public class SaveTheHotbarClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		final Block[] blocks = new Block[]{SaveTheHotbar.SKELETON_HEAD_GRAVE, SaveTheHotbar.ZOMBIE_HEAD_GRAVE};

		#if MC_VERSION < 12106
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutoutMipped(), blocks);
		#elif MC_VERSION < 12111
		BlockRenderLayerMap.putBlocks(ChunkSectionLayer.CUTOUT_MIPPED, blocks);
		#else
		BlockRenderLayerMap.putBlocks(ChunkSectionLayer.CUTOUT, blocks);
		#endif
	}
}
#endif