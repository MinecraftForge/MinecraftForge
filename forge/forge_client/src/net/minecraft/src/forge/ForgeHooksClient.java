/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.Block;
import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import net.minecraft.src.RenderGlobal;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class ForgeHooksClient {

	public static boolean onBlockHighlight(RenderGlobal renderglobal,
		    EntityPlayer player, MovingObjectPosition mop, int i,
		    ItemStack itemstack, float f) {
		for (IHighlightHandler handler : highlightHandlers) {
			if(handler.onBlockHighlight(renderglobal,player,mop,
					i,itemstack,f))
				return true;
		}
		return false;
	}

	static LinkedList<IHighlightHandler> highlightHandlers = new LinkedList<IHighlightHandler>();

	public static boolean canRenderInPass(Block block, int pass) {
		if(block instanceof IMultipassRender) {
			IMultipassRender impr = (IMultipassRender) block;
			return impr.canRenderInPass(pass);
		}
		if(pass==block.getRenderBlockPass()) return true;
		return false;
	}

	static HashMap tessellators=new HashMap();
	static HashMap textures=new HashMap();
	static boolean inWorld=false;
	static HashSet renderTextureTest=new HashSet();
	static ArrayList<List> renderTextureList=new ArrayList();
	static Tessellator defaultTessellator=null;

	protected static void bindTessellator(int tex, int sub) {
		List key=Arrays.asList(tex,sub);
		Tessellator t;
		if(!tessellators.containsKey(key)) {
			t=new Tessellator();
			tessellators.put(key,t);
		} else {
			t=(Tessellator)tessellators.get(key);
		}
		if(inWorld && !renderTextureTest.contains(key)) {
			renderTextureTest.add(key);
			renderTextureList.add(key);
			t.startDrawingQuads();
			t.setTranslationD(defaultTessellator.xOffset,
				defaultTessellator.yOffset,
				defaultTessellator.zOffset);
		}
		Tessellator.instance=t;
	}

	protected static void bindTexture(String name, int sub) {
		int n;
		if(!textures.containsKey(name)) {
			n=ModLoader.getMinecraftInstance().renderEngine
				.getTexture(name);
			textures.put(name,n);
		} else {
			n=(Integer)textures.get(name);
		}
		if(!inWorld) {
			if(Tessellator.instance.isDrawing) {
				int mode=Tessellator.instance.drawMode;
				Tessellator.instance.draw();
				Tessellator.instance.startDrawing(mode);
			}
			GL11.glBindTexture(3553 /* GL_TEXTURE_2D */,n);
			return;
		}
		bindTessellator(n,sub);
	}

	protected static void unbindTexture() {
		if(inWorld) {
			Tessellator.instance=defaultTessellator;
		} else {
			if(Tessellator.instance.isDrawing) {
				int mode=Tessellator.instance.drawMode;
				Tessellator.instance.draw();
				Tessellator.instance.startDrawing(mode);
			}
			GL11.glBindTexture(3553 /* GL_TEXTURE_2D */, ModLoader
				.getMinecraftInstance().renderEngine
				.getTexture("/terrain.png"));
			return;
		}
	}

	static int renderPass=-1;
	public static void beforeRenderPass(int pass) {
		renderPass=pass;
		defaultTessellator=Tessellator.instance;
		Tessellator.renderingWorldRenderer=true;
		GL11.glBindTexture(3553 /* GL_TEXTURE_2D */, ModLoader
			.getMinecraftInstance().renderEngine
			.getTexture("/terrain.png"));
		renderTextureTest.clear();
		renderTextureList.clear();
		inWorld=true;
	}

	public static void afterRenderPass(int pass) {
		renderPass=-1;
		inWorld=false;
		for(List l : renderTextureList) {
			// TODO: call appropriate client hooks
			Integer[] tn=(Integer[])l.toArray();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D,tn[0]);
			Tessellator t=(Tessellator)tessellators.get(l);
			t.draw();
		}
		GL11.glBindTexture(3553 /* GL_TEXTURE_2D */, ModLoader
			.getMinecraftInstance().renderEngine
			.getTexture("/terrain.png"));
		Tessellator.renderingWorldRenderer=false;
	}

	public static void beforeBlockRender(Block block,
			RenderBlocks renderblocks) {
		if (block instanceof ITextureProvider
				&& renderblocks.overrideBlockTexture == -1) {
			ITextureProvider itp=(ITextureProvider)block;
			bindTexture(itp.getTextureFile(),0);
		}
	}

	public static void afterBlockRender(Block block,
			RenderBlocks renderblocks) {
		if (block instanceof ITextureProvider
				&& renderblocks.overrideBlockTexture == -1) {
			unbindTexture();
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
	
	public static void renderCustomItem(ICustomItemRenderer customRenderer, RenderBlocks renderBlocks,int itemID, int meta, float f) {
		Tessellator tessellator = Tessellator.instance;
		if (renderBlocks.useInventoryTint) {
			int j = 0xffffff;//block.getRenderColor(i);
			float f1 = (float) (j >> 16 & 0xff) / 255F;
			float f3 = (float) (j >> 8 & 0xff) / 255F;
			float f5 = (float) (j & 0xff) / 255F;
			GL11.glColor4f(f1 * f, f3 * f, f5 * f, 1.0F);
		}

//		ModLoader.RenderInvBlock(this, block, i, k);
		customRenderer.renderInventory(renderBlocks, itemID, meta);
	}
}

