/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderBlocks;

public class MinecraftForgeClient {
	/**
	 * Registers a new block highlight handler.
	 */
	public static void registerHighlightHandler(IHighlightHandler handler) {
		ForgeHooksClient.highlightHandlers.add(handler);
	}

	/** Bind a texture.  This is used to bind a texture file when
	 * performing your own rendering, rather than using ITextureProvider.
	 *
	 * This variation is reserved for future expansion.
	 */
	public static void bindTexture(String name, int sub) {
		ForgeHooksClient.bindTexture(name,sub);
	}

	/** Bind a texture.  This is used to bind a texture file when
	 * performing your own rendering, rather than using ITextureProvider.
	 */
	public static void bindTexture(String name) {
		ForgeHooksClient.bindTexture(name,0);
	}

	/** Unbind a texture.  This binds the default texture, when you are
	 * finished performing custom rendering.
	 */
	public static void unbindTexture() {
		ForgeHooksClient.unbindTexture();
	}

	/** Preload a texture.  Textures must be preloaded before the first
	 * use, or they will cause visual anomalies.
	 */
	public static void preloadTexture(String texture) {
		ModLoader.getMinecraftInstance().renderEngine
			.getTexture(texture);
	}

	/** Render a block.  Render a block which may have a custom texture.
	 */
	public static void renderBlock(RenderBlocks rb, Block bl, int i, int
			j, int k) {
		ForgeHooksClient.beforeBlockRender(bl,rb);
        	rb.renderBlockByRenderType(bl,i,j,k);
		ForgeHooksClient.afterBlockRender(bl,rb);
	}

	/** Get the current render pass.
	 */
	public static int getRenderPass() {
		return ForgeHooksClient.renderPass;
	}
	
	private static ICustomItemRenderer [] customItemRenderers = new ICustomItemRenderer [Item.itemsList.length];
	
	public static void registerCustomItemRenderer (int itemID, ICustomItemRenderer renderer) {
		customItemRenderers [itemID] = renderer;
	}
	
	public static ICustomItemRenderer getCustomItemRenderer (int itemID) {
		return customItemRenderers [itemID];
	}
}
