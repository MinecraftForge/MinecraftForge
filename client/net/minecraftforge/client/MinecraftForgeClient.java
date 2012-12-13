/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.client;

import org.lwjgl.opengl.Display;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.common.MinecraftForge;

public class MinecraftForgeClient
{
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
     * Preload a texture.  Textures must be preloaded before the first
     * use, or they will cause visual anomalies.
     */
    public static void preloadTexture(String texture)
    {
        ForgeHooksClient.engine().getTexture(texture);
    }

    /** Render a block.  Render a block which may have a custom texture.
     */
    public static void renderBlock(RenderBlocks render, Block block, int x, int y, int z)
    {
        ForgeHooksClient.beforeBlockRender(block, render);
        render.renderBlockByRenderType(block, x, y, z);
        ForgeHooksClient.afterBlockRender(block, render);
    }

    /**
     * Get the current render pass.
     */
    public static int getRenderPass()
    {
        return ForgeHooksClient.renderPass;
    }

    private static IItemRenderer[] customItemRenderers = new IItemRenderer[Item.itemsList.length];

    /**
     * Register a custom renderer for a specific item. This can be used to
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
}
