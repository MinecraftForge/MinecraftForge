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

	public static void onRenderWorldLast(RenderGlobal rg, float f) {
		for (IRenderWorldLastHandler handler : renderWorldLastHandlers) {
			handler.onRenderWorldLast(rg,f);
		}
	}

	static LinkedList<IHighlightHandler> highlightHandlers =
		new LinkedList<IHighlightHandler>();
	static LinkedList<IRenderWorldLastHandler> renderWorldLastHandlers =
		new LinkedList<IRenderWorldLastHandler>();

	public static boolean canRenderInPass(Block block, int pass) {
		if(block instanceof IMultipassRender) {
			IMultipassRender impr = (IMultipassRender) block;
			return impr.canRenderInPass(pass);
		}
		if(pass==block.getRenderBlockPass()) return true;
		return false;
	}

	private static class TesKey implements Comparable<TesKey> {
		public TesKey(int t, int s) { tex=t; sub=s; }
		public int compareTo(TesKey key) {
			if(sub==key.sub) return tex-key.tex;
			return sub-key.sub;
		}
		public boolean equals(Object obj) {
			return compareTo((TesKey)obj)==0;
		}
		public int hashCode() {
			int c1=Integer.valueOf(tex).hashCode();
			int c2=Integer.valueOf(sub).hashCode();
			return c1+31*c2;
		}
		public int tex, sub;
	}

	static HashMap<TesKey,Tessellator> tessellators=
		new HashMap<TesKey,Tessellator>();
	static HashMap<String,Integer> textures=new HashMap<String,Integer>();
	static boolean inWorld=false;
	static TreeSet<TesKey> renderTextures=new TreeSet<TesKey>();
	static Tessellator defaultTessellator=null;
	static HashMap<TesKey,IRenderContextHandler> renderHandlers=new
		HashMap<TesKey,IRenderContextHandler>();

	protected static void registerRenderContextHandler(String tex, int sub,
			IRenderContextHandler handler) {
		Integer n;
		n=textures.get(tex);
		if(n==null) {
			n=ModLoader.getMinecraftInstance().renderEngine
				.getTexture(tex);
			textures.put(tex,n);
		}
		renderHandlers.put(new TesKey(n,sub),handler);
	}

	protected static void bindTessellator(int tex, int sub) {
		TesKey key=new TesKey(tex,sub);
		Tessellator t;
		t=tessellators.get(key);
		if(t==null) {
			t=new Tessellator();
			tessellators.put(key,t);
		}
		if(inWorld && !renderTextures.contains(key)) {
			renderTextures.add(key);
			t.startDrawingQuads();
			t.setTranslationD(defaultTessellator.xOffset,
				defaultTessellator.yOffset,
				defaultTessellator.zOffset);
		}
		Tessellator.instance=t;
	}

	static IRenderContextHandler unbindContext=null;
	protected static void bindTexture(String name, int sub) {
		Integer n;
		n=textures.get(name);
		if(n==null) {
			n=ModLoader.getMinecraftInstance().renderEngine
				.getTexture(name);
			textures.put(name,n);
		}
		if(!inWorld) {
			if(unbindContext!=null) {
				unbindContext.afterRenderContext();
				unbindContext=null;
			}
			if(Tessellator.instance.isDrawing) {
				int mode=Tessellator.instance.drawMode;
				Tessellator.instance.draw();
				Tessellator.instance.startDrawing(mode);
			}
			GL11.glBindTexture(3553 /* GL_TEXTURE_2D */,n);
			unbindContext=renderHandlers.get(new TesKey(n,sub));
			if(unbindContext!=null) {
				unbindContext.beforeRenderContext();
			}
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
				if(unbindContext!=null) {
					unbindContext.afterRenderContext();
					unbindContext=null;
				}
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
		renderTextures.clear();
		inWorld=true;
	}

	public static void afterRenderPass(int pass) {
		renderPass=-1;
		inWorld=false;
		for(TesKey tk : renderTextures) {
			IRenderContextHandler irch=renderHandlers.get(tk);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D,tk.tex);
			Tessellator t=tessellators.get(tk);
			if(irch==null) {
				t.draw();
			} else {
				Tessellator.instance=t;
				irch.beforeRenderContext();
				t.draw();
				irch.afterRenderContext();
			}
		}
		GL11.glBindTexture(3553 /* GL_TEXTURE_2D */, ModLoader
			.getMinecraftInstance().renderEngine
			.getTexture("/terrain.png"));
		Tessellator.renderingWorldRenderer=false;
		Tessellator.instance=defaultTessellator;
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
	
	public static String getTexture(String def, Object item) 
	{
		if (item instanceof ITextureProvider)
		{
			return ((ITextureProvider)item).getTextureFile();
		}
		else
		{
			return def;
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

