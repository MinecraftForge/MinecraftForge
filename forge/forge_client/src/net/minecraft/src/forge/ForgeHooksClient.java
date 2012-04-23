/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.ModLoader;
import net.minecraft.src.Packet100OpenWindow;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.SoundPoolEntry;
import net.minecraft.src.Tessellator;
import net.minecraft.src.RenderGlobal;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.ItemStack;
import net.minecraft.src.WorldClient;

import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL12;

import static net.minecraft.src.forge.IItemRenderer.ItemRenderType.*;
import static net.minecraft.src.forge.IItemRenderer.ItemRendererHelper.*;

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
    
    public static void onTextureLoad(String textureName, int textureID)
    {
        for (ITextureLoadHandler handler: textureLoadHandlers)
        {
            handler.onTextureLoad(textureName, textureID);
        }
    }
    public static LinkedList<ITextureLoadHandler> textureLoadHandlers = new LinkedList<ITextureLoadHandler>();

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
            tess.setTranslation(defaultTessellator.xOffset, defaultTessellator.yOffset, defaultTessellator.zOffset);
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
    
    public static void renderEquippedItem(IItemRenderer customRenderer, RenderBlocks renderBlocks, EntityLiving entity, ItemStack item)
    {
        if (customRenderer.shouldUseRenderHelper(EQUIPPED, item, EQUIPPED_BLOCK))
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            customRenderer.renderItem(EQUIPPED, item, renderBlocks, entity);
            GL11.glPopMatrix();
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
            customRenderer.renderItem(EQUIPPED, item, renderBlocks, entity);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
        }
    }
    
    /**
     * Trys to get the class for the specified name, will also try the 
     * net.minecraft.src package in case we are in MCP
     * Returns null if not found.
     * 
     * @param name The class name
     * @return The Class, or null if not found
     */
    private static Class getClass(String name)
    {
        try 
        {
            return Class.forName(name);
        }
        catch (Exception e)
        {
            try
            {
                return Class.forName("net.minecraft.src." + name);
            }
            catch (Exception e2)
            {
                return null;
            }
        }
    }

    /**
     * Helper function to try and invoke ModLoaderMp's NetClientHandler.handleVehicleSpawn modifications
     * if ModLoaderMp is install along side Forge.
     */
    public static Object tryMLMPVehicleSpawn(int type, World world, double x, double y, double z, Entity thrower) throws Exception
    {
        Class mlmp = getClass("ModLoaderMp"); 
        if (mlmp == null)
        {
            return null;
        }
        
        Object entry = mlmp.getDeclaredMethod("handleNetClientHandlerEntities", int.class).invoke(null, type);
        if (entry == null)
        {
            return null;
        }
        
        Class entityClass = (Class)entry.getClass().getDeclaredField("entityClass").get(entry);
        Object ret = (Entity)entityClass.getConstructor(World.class, Double.TYPE, Double.TYPE, Double.TYPE).newInstance(world, x, y, z);

        if (entry.getClass().getDeclaredField("entityHasOwner").getBoolean(entry))
        {
            Field owner = entityClass.getField("owner");

            if (!Entity.class.isAssignableFrom(owner.getType()))
            {
                throw new Exception(String.format("Entity\'s owner field must be of type Entity, but it is of type %s.", owner.getType()));
            }

            if (thrower == null)
            {
                System.out.println("Received spawn packet for entity with owner, but owner was not found.");
                ModLoader.getLogger().fine("Received spawn packet for entity with owner, but owner was not found.");
            }
            else
            {
                if (!owner.getType().isAssignableFrom(thrower.getClass()))
                {
                    throw new Exception(String.format("Tried to assign an entity of type %s to entity owner, which is of type %s.", thrower.getClass(), owner.getType()));
                }

                owner.set(ret, thrower);
            }
        }
        return ret;
    }
    
    /**
     * helper function to try and call ModLoaderMp's handleGUI function
     * if ModLoaderMp is installed client side along with Forge.
     * 
     * @param pkt The open window packet
     */
    public static void tryMLMPOpenWindow(Packet100OpenWindow pkt) 
    {
        Class mlmp = getClass("ModLoaderMp"); 
        if (mlmp == null)
        {
            return;
        }
        
        try 
        {
            mlmp.getDeclaredMethod("handleGUI", Packet100OpenWindow.class).invoke(null, pkt);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    public static LinkedList<ISoundHandler> soundHandlers = new LinkedList<ISoundHandler>();
    public static void onSetupAudio(SoundManager soundManager) 
    {
        for (ISoundHandler handler : soundHandlers)
        {
            handler.onSetupAudio(soundManager);
        }
    }

    public static void onLoadSoundSettings(SoundManager soundManager) 
    {
        for (ISoundHandler handler : soundHandlers)
        {
            handler.onLoadSoundSettings(soundManager);
        }
    }

    public static SoundPoolEntry onPlayBackgroundMusic(SoundManager soundManager, SoundPoolEntry entry) 
    {
        for (ISoundHandler handler : soundHandlers)
        {
            entry = handler.onPlayBackgroundMusic(soundManager, entry);
            if (entry == null)
            {
                return null;
            }
        }
        return entry;
    }

    public static SoundPoolEntry onPlayStreaming(SoundManager soundManager, SoundPoolEntry entry, String soundName, float x, float y, float z)
    {
        for (ISoundHandler handler : soundHandlers)
        {
            entry = handler.onPlayStreaming(soundManager, entry, soundName, x, y, z);
            if (entry == null)
            {
                return null;
            }
        }
        return entry;
    }

    public static SoundPoolEntry onPlaySound(SoundManager soundManager, SoundPoolEntry entry, String soundName, float x, float y, float z, float volume, float pitch)
    {
        for (ISoundHandler handler : soundHandlers)
        {
            entry = handler.onPlaySound(soundManager, entry, soundName, x, y, z, volume, pitch);
            if (entry == null)
            {
                return null;
            }
        }
        return entry;
    }

    public static SoundPoolEntry onPlaySoundEffect(SoundManager soundManager, SoundPoolEntry entry, String soundName, float volume, float pitch) 
    {
        for (ISoundHandler handler : soundHandlers)
        {
            entry = handler.onPlaySoundEffect(soundManager, entry, soundName,volume, pitch);
            if (entry == null)
            {
                return null;
            }
        }
        return entry;
    }
}
