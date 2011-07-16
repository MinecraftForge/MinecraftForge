/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.Block;
import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;

public class MinecraftForgeClient {
    
    public static void beforeBlockRender(Block block, RenderBlocks renderblocks) {
        if (block instanceof ITextureProvider
                && renderblocks.overrideBlockTexture == -1) {
            Tessellator tessellator = Tessellator.instance;
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1F, 0.0F);

            GL11.glBindTexture(3553 /* GL_TEXTURE_2D */, ModLoader
                    .getMinecraftInstance().renderEngine
                    .getTexture(((ITextureProvider) (block)).getTextureFile()));

        }
    }
    
    public static void afterBlockRender(Block block, RenderBlocks renderblocks) {
        if (block instanceof ITextureProvider
                && renderblocks.overrideBlockTexture == -1) {
            Tessellator tessellator = Tessellator.instance;
            tessellator.draw();
            tessellator.startDrawingQuads();

            GL11.glBindTexture(3553 /* GL_TEXTURE_2D */, ModLoader
                    .getMinecraftInstance().renderEngine
                    .getTexture("/terrain.png"));
        }
    }
    
    public static void overrideTexture (Object o) {
        if (o instanceof ITextureProvider) {
            GL11.glBindTexture(3553 /* GL_TEXTURE_2D */, ModLoader
                    .getMinecraftInstance().renderEngine
                    .getTexture(((ITextureProvider) (o))
                            .getTextureFile()));
        }
    }
	
	public static void onGameStart() {
		System.out.println("OnGameHook");
		ModLoader.getMinecraftInstance().effectRenderer = new MinecraftForgeEffectRenderer(ModLoader.getMinecraftInstance().theWorld, ModLoader.getMinecraftInstance().renderEngine);
	}
}
