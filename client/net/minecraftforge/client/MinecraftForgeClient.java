/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.client;

import java.util.BitSet;

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

    public static int getRenderPass()
    {
        return ForgeHooksClient.renderPass;
    }

    public static int getStencilBits()
    {
        return ForgeHooksClient.stencilBits;
    }


    private static BitSet stencilBits = new BitSet(getStencilBits());
    static
    {
        stencilBits.set(0,getStencilBits());
    }

    /**
     * Reserve a stencil bit for use in rendering
     *
     * @return A bit or -1 if no further stencil bits are available
     */
    public static int reserveStencilBit()
    {
        int bit = stencilBits.nextSetBit(0);
        if (bit >= 0)
        {
            stencilBits.clear(bit);
        }
        return bit;
    }

    /**
     * Release the stencil bit for other use
     *
     * @param bit The bit from {@link #reserveStencilBit()}
     */
    public static void releaseStencilBit(int bit)
    {
        if (bit >= 0 && bit < getStencilBits())
        {
            stencilBits.set(bit);
        }
    }
}
