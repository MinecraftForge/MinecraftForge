/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;

/**
 * An ItemDecorator that is used to render something on specific items, when the DurabilityBar and StackCount is rendered.
 * Add it to an item using {@linkplain RegisterItemDecorationsEvent#register(ItemLike, IItemDecorator)}.
 */
public interface IItemDecorator
{

    /**
     * Is called after {@linkplain ItemRenderer#renderGuiItemDecorations(Font, ItemStack, int, int, String)} is done rendering.
     * The StackCount is rendered at blitOffset+200 so use the blitOffset with caution.
     * <p>
     * The RenderState during this call will be: enableTexture, enableDepthTest, enableBlend and defaultBlendFunc
     * @return true if you have modified the RenderState and it has to be reset for other ItemDecorators
     */
    boolean render(Font font, ItemStack stack, int xOffset, int yOffset, float blitOffset);
}
