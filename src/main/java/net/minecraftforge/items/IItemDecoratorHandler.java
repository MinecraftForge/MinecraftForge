/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items;

import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;

public interface IItemDecoratorHandler
{
    void render(Font font, ItemStack stack, int xOffset, int yOffset, float blitOffset);
    void addDecorator(IItemDecorator itemDecorator);
    void removeDecorator(IItemDecorator itemDecorator);
}
