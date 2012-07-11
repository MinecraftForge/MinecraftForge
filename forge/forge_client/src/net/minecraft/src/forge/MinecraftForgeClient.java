/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import org.lwjgl.opengl.Display;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.World;
import net.minecraft.src.forge.IItemRenderer.ItemRenderType;

public class MinecraftForgeClient
{
    /**
     * Registers a new block highlight handler.
     */
    public static void registerHighlightHandler(IHighlightHandler handler)
    {
        ForgeHooksClient.highlightHandlers.add(handler);
    }

    /** Register a new render context handler.  A render context is a block
     * of rendering performed with similar OpenGL modes, for example,
     * texture name.
     * @param texture The name of the texture for this render context.
     * @param subid The subid of this render context.  0 is the default pass
     * for normal rendering, higher subids render later.  All subids of 0
     * will render before all subids of 1, etc.
     * @param handler The handler to register.
     */
    public static void registerRenderContextHandler(String texture, int subid, IRenderContextHandler handler)
    {
        ForgeHooksClient.registerRenderContextHandler(texture, subid, handler);
    }
    
    /**
     * Registers a Texture Load Handler
     * @param handler The handler
     */
    public static void registerTextureLoadHandler(ITextureLoadHandler handler)
    {
        ForgeHooksClient.textureLoadHandlers.add(handler);
    }
    
    /**
     * Registers a Render Last Handler
     * @param handler The handler
     */
    public static void registerRenderLastHandler(IRenderWorldLastHandler handler)
    {
        ForgeHooksClient.renderWorldLastHandlers.add(handler);
    }
    
    /**
     * Registers a Sound Handler
     * @param handler The handler
     */
    public static void registerSoundHandler(ISoundHandler handler)
    {
        ForgeHooksClient.soundHandlers.add(handler);
        checkMinecraftVersion("Minecraft Minecraft 1.2.5", "Interface check in registerSoundHandler, remove it Mods should be updated");
        try
        {
            if (handler.getClass().getDeclaredMethod("onPlaySoundAtEntity", Entity.class, String.class, float.class, float.class) != null)
            {
                ForgeHooksClient.soundHandlers2.add(handler);
            }
        }
        catch (Exception e) 
        {
            if (World.class.getName().contains("World"))
            {
                e.printStackTrace();
            }
        }
    }

    /** Bind a texture.  This is used to bind a texture file when
     * performing your own rendering, rather than using ITextureProvider.
     *
     * This variation is reserved for future expansion.
     */
    public static void bindTexture(String texture, int subid)
    {
        ForgeHooksClient.bindTexture(texture, subid);
    }

    /** Bind a texture.  This is used to bind a texture file when
     * performing your own rendering, rather than using ITextureProvider.
     */
    public static void bindTexture(String texture)
    {
        ForgeHooksClient.bindTexture(texture, 0);
    }

    /** Unbind a texture.  This binds the default texture, when you are
     * finished performing custom rendering.
     */
    public static void unbindTexture()
    {
        ForgeHooksClient.unbindTexture();
    }

    /** Preload a texture.  Textures must be preloaded before the first
     * use, or they will cause visual anomalies.
     */
    public static void preloadTexture(String texture)
    {
        ModLoader.getMinecraftInstance().renderEngine.getTexture(texture);
    }

    /** Render a block.  Render a block which may have a custom texture.
     */
    public static void renderBlock(RenderBlocks render, Block block, int X, int Y, int Z)
    {
        ForgeHooksClient.beforeBlockRender(block, render);
        render.renderBlockByRenderType(block, X, Y, Z);
        ForgeHooksClient.afterBlockRender(block, render);
    }

    /** Get the current render pass.
     */
    public static int getRenderPass()
    {
        return ForgeHooksClient.renderPass;
    }
    
    private static IItemRenderer[] customItemRenderers = new IItemRenderer[Item.itemsList.length];

    /** Register a custom renderer for a specific item. This can be used to
     * render the item in-world as an EntityItem, when the item is equipped, or
     * when the item is in an inventory slot.
     * @param itemID The item ID (shifted index) to handle rendering.
     * @param renderer The IItemRenderer interface that handles rendering for
     * this item.
     */
    public static void registerItemRenderer(int itemID, IItemRenderer renderer)
    {
        customItemRenderers[itemID] = renderer;
    }

    public static IItemRenderer getItemRenderer(ItemStack item, ItemRenderType type)
    {
        IItemRenderer renderer = customItemRenderers[item.itemID];
        if (renderer != null && renderer.handleRenderType(item, type)) 
        {
            return customItemRenderers[item.itemID];
        }
        return null;
    }

    private static boolean hasInit = false;
    public static void init()
    {
        if (hasInit)
        {
            return;
        }
        hasInit = true;
        ForgeHooks.setPacketHandler(new PacketHandlerClient());
    }

    static
    {
        init();
    }
    
    /***
     * This is a function that is used to enforce deprecation of code.
     * It checks the current Display's title against the passed in argument.
     * If they do not match (such is the case in different versionf of MC) it exits the process with a error
     * 
     * @param version The version to find, usually "Minecraft Minecraft 1.2.3"
     * @param message The error message to display in the crash log
     */
    public static void checkMinecraftVersion(String version, String message)
    {
        if (!Display.getTitle().equals(version))
        {
            MinecraftForge.killMinecraft("Minecraft Forge", message.replaceAll("%version%", Display.getTitle()));
        }
    }
}
