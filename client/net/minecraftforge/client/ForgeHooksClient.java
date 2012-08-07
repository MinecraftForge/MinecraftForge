package net.minecraftforge.client;

import java.util.HashMap;
import java.util.TreeSet;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import net.minecraftforge.common.IArmorTextureProvider;

public class ForgeHooksClient
{
    private static class TesKey implements Comparable<TesKey>
    {
        public final int texture, subid;
        public TesKey(int textureID, int subID)
        {
            texture = textureID;
            subid = subID;
        }
        
        public int compareTo(TesKey key)
        {
            if (subid == key.subid)
            {
                return texture - key.texture;
            }
            return subid - key.subid;
        }
        
        public boolean equals(Object obj)
        {
            return compareTo((TesKey)obj) == 0;
        }
        
        public int hashCode()
        {
            return texture + 31 * subid;
        }
    }
    
    public static HashMap<TesKey, Tessellator> tessellators = new HashMap<TesKey, Tessellator>();
    public static HashMap<String, Integer> textures = new HashMap<String, Integer>();
    public static TreeSet<TesKey> renderTextures = new TreeSet<TesKey>();
    public static Tessellator defaultTessellator = null;
    public static boolean inWorld = false;
    public static HashMap<TesKey, IRenderContextHandler> renderHandlers = new HashMap<TesKey, IRenderContextHandler>();
    public static IRenderContextHandler unbindContext = null;
    
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

    public static void bindTexture(String texture, int subID)
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

    public static void unbindTexture()
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
    
    protected static void bindTessellator(int texture, int subID)
    {
        TesKey key = new TesKey(texture, subID);
        Tessellator tess = tessellators.get(key);

        if (tess == null)
        {
            tess = new Tessellator();
            tess.textureID = texture;
            tessellators.put(key, tess);
        }

        if (inWorld && !renderTextures.contains(key))
        {
            renderTextures.add(key);
            tess.startDrawingQuads();
            tess.setTranslation(defaultTessellator.xOffset, defaultTessellator.yOffset, defaultTessellator.zOffset);
        }

        Tessellator.instance = tess;
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
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, info.texture);
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
        if (!block.isDefaultTexture && render.overrideBlockTexture == -1)
        {
            bindTexture(block.getTextureFile(), 0);
        }
    }

    public static void afterBlockRender(Block block, RenderBlocks render)
    {
        if (!block.isDefaultTexture && render.overrideBlockTexture == -1)
        {
            unbindTexture();
        }
    }
    
    public static String getArmorTexture(ItemStack armor, String _default)
    {
        if (armor.getItem() instanceof IArmorTextureProvider)
        {
            return ((IArmorTextureProvider)armor.getItem()).getArmorTextureFile(armor);
        }
        return _default;
    }
}
