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
import net.minecraft.src.*;
import org.lwjgl.opengl.GL12;

public class ForgeHooksClient
{

    public static boolean onBlockHighlight(RenderGlobal render, EntityPlayer player, MovingObjectPosition target, int i, ItemStack itemstack, float partialTicks)
    {
        for (IHighlightHandler handler : highlightHandlers)
        {
            if (handler.onBlockHighlight(render, player, target, i, itemstack, partialTicks))
            {
                return true;
            }
        }
        return false;
    }

    public static void onRenderWorldLast(RenderGlobal render, float partialTicks)
    {
        for (IRenderWorldLastHandler handler : renderWorldLastHandlers)
        {
            handler.onRenderWorldLast(render, partialTicks);
        }
    }

    public static LinkedList<IHighlightHandler> highlightHandlers = new LinkedList<IHighlightHandler>();
    public static LinkedList<IRenderWorldLastHandler> renderWorldLastHandlers = new LinkedList<IRenderWorldLastHandler>();

    public static boolean canRenderInPass(Block block, int pass)
    {
        if (block instanceof IMultipassRender)
        {
            return ((IMultipassRender)block).canRenderInPass(pass);
        }
        if (pass == block.getRenderBlockPass())
        {
            return true;
        }
        return false;
    }

    private static class TesKey implements Comparable<TesKey>
    {
        public TesKey(int textureID, int subID)
        {
            tex = textureID;
            sub = subID;
        }
        public int compareTo(TesKey key)
        {
            if (sub == key.sub)
            {
                return tex - key.tex;
            }
            return sub - key.sub;
        }
        public boolean equals(Object obj)
        {
            return compareTo((TesKey)obj) == 0;
        }
        public int hashCode()
        {
            int c1 = Integer.valueOf(tex).hashCode();
            int c2 = Integer.valueOf(sub).hashCode();
            return c1 + 31 * c2;
        }
        public int tex, sub;
    }

    public static HashMap<TesKey, Tessellator> tessellators = new HashMap<TesKey, Tessellator>();
    public static HashMap<String, Integer> textures = new HashMap<String, Integer>();
    public static boolean inWorld = false;
    public static TreeSet<TesKey> renderTextures = new TreeSet<TesKey>();
    public static Tessellator defaultTessellator = null;
    public static HashMap<TesKey, IRenderContextHandler> renderHandlers = new HashMap<TesKey, IRenderContextHandler>();

    protected static void registerRenderContextHandler(String texture, int subID, IRenderContextHandler handler)
    {
        Integer texID = textures.get(texture);
        if (texID == null)
        {
            texID = ModLoader.getMinecraftInstance().renderEngine.getTexture(texture);
            textures.put(texture, texID);
        }
        renderHandlers.put(new TesKey(texID, subID), handler);
    }

    protected static void bindTessellator(int texture, int subID)
    {
        TesKey key = new TesKey(texture, subID);
        Tessellator tess = tessellators.get(key);
        if (tess == null)
        {
            tess = new Tessellator();
            tessellators.put(key, tess);
        }
        if (inWorld && !renderTextures.contains(key))
        {
            renderTextures.add(key);
            tess.startDrawingQuads();
            tess.setTranslationD(defaultTessellator.xOffset, defaultTessellator.yOffset, defaultTessellator.zOffset);
        }
        Tessellator.instance = tess;
    }

    public static IRenderContextHandler unbindContext = null;
    protected static void bindTexture(String texture, int subID)
    {
        Integer texID = textures.get(texture);
        if (texID == null)
        {
            texID = ModLoader.getMinecraftInstance().renderEngine.getTexture(texture);
            textures.put(texture, texID);
        }
        if (!inWorld)
        {
            if (unbindContext != null)
            {
                unbindContext.afterRenderContext();
                unbindContext = null;
            }
            if (Tessellator.instance.isDrawing)
            {
                int mode = Tessellator.instance.drawMode;
                Tessellator.instance.draw();
                Tessellator.instance.startDrawing(mode);
            }
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
            unbindContext = renderHandlers.get(new TesKey(texID, subID));
            if (unbindContext != null)
            {
                unbindContext.beforeRenderContext();
            }
            return;
        }
        bindTessellator(texID, subID);
    }

    protected static void unbindTexture()
    {
        if (inWorld)
        {
            Tessellator.instance = defaultTessellator;
        }
        else
        {
            if (Tessellator.instance.isDrawing)
            {
                int mode = Tessellator.instance.drawMode;
                Tessellator.instance.draw();
                if (unbindContext != null)
                {
                    unbindContext.afterRenderContext();
                    unbindContext = null;
                }
                Tessellator.instance.startDrawing(mode);
            }
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModLoader.getMinecraftInstance().renderEngine.getTexture("/terrain.png"));
            return;
        }
    }

    static int renderPass = -1;
    public static void beforeRenderPass(int pass)
    {
        renderPass = pass;
        defaultTessellator = Tessellator.instance;
        Tessellator.renderingWorldRenderer = true;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModLoader.getMinecraftInstance().renderEngine.getTexture("/terrain.png"));
        renderTextures.clear();
        inWorld = true;
    }

    public static void afterRenderPass(int pass)
    {
        renderPass = -1;
        inWorld = false;
        for (TesKey info : renderTextures)
        {
            IRenderContextHandler handler = renderHandlers.get(info);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, info.tex);
            Tessellator tess = tessellators.get(info);
            if (handler == null)
            {
                tess.draw();
            }
            else
            {
                Tessellator.instance = tess;
                handler.beforeRenderContext();
                tess.draw();
                handler.afterRenderContext();
            }
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModLoader.getMinecraftInstance().renderEngine.getTexture("/terrain.png"));
        Tessellator.renderingWorldRenderer = false;
        Tessellator.instance = defaultTessellator;
    }

    public static void beforeBlockRender(Block block, RenderBlocks render)
    {
        if (block instanceof ITextureProvider && render.overrideBlockTexture == -1)
        {
            bindTexture(((ITextureProvider)block).getTextureFile(), 0);
        }
    }

    public static void afterBlockRender(Block block, RenderBlocks render)
    {
        if (block instanceof ITextureProvider && render.overrideBlockTexture == -1)
        {
            unbindTexture();
        }
    }

    public static void overrideTexture (Object obj)
    {
        if (obj instanceof ITextureProvider)
        {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModLoader.getMinecraftInstance().renderEngine.getTexture(((ITextureProvider)obj).getTextureFile()));
        }
    }

    public static String getTexture(String def, Object obj)
    {
        if (obj instanceof ITextureProvider)
        {
            return ((ITextureProvider)obj).getTextureFile();
        }
        else
        {
            return def;
        }
    }
    
    public static void renderEntityItem(IEntityItemRenderer customRenderer, RenderBlocks renderBlocks, EntityItem item, int itemID, int metadata)
    {
        customRenderer.renderEntityItem(renderBlocks, item, itemID, metadata);
    }
    
    public static void renderEquippedItem(IEquippedItemRenderer customRenderer, RenderBlocks renderBlocks, EntityLiving entity, int itemID, int metadata)
    {
        if (MinecraftForgeClient.renderEquippedItemAsBlock(itemID))
        {
            customRenderer.renderEquippedItem(renderBlocks, entity, itemID, metadata);
        }
        else
        {
            GL11.glPushMatrix();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glTranslatef(0.0F, -0.3F, 0.0F);
            GL11.glScalef(1.5F, 1.5F, 1.5F);
            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
            customRenderer.renderEquippedItem(renderBlocks, entity, itemID, metadata);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
        }
    }
    
    public static void renderInventoryItem(IInventoryItemRenderer customRenderer, RenderBlocks renderBlocks, int itemID, int metadata)
    {
        customRenderer.renderInventoryItem(renderBlocks, itemID, metadata);
    }
}
