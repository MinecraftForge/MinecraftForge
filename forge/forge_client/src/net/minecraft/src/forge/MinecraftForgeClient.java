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
	public static void bindTexture(String name, int sub) {
		ForgeHooksClient.bindTexture(name,sub);
	}

	public static void bindTexture(String name) {
		ForgeHooksClient.bindTexture(name,0);
	}

	public static void unbindTexture() {
		ForgeHooksClient.unbindTexture();
	}

	public static void preloadTexture(String texture) {
		ModLoader.getMinecraftInstance().renderEngine
			.getTexture(texture);
	}
}
