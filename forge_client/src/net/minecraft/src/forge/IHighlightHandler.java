/*
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */
package net.minecraft.src.forge;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumStatus;
import net.minecraft.src.RenderGlobal;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.ItemStack;

public interface IHighlightHandler
{
    /**
     * Allow custom handling of highlights.  Return true if the highlight has
     * been handled.
     */
    public boolean onBlockHighlight(RenderGlobal render, EntityPlayer player, MovingObjectPosition target, int i, ItemStack currentItem, float partialTicks);
}

