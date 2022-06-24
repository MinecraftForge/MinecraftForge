/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items;

import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;

import java.util.ArrayList;
import java.util.List;

public class ItemDecoratorHandler implements IItemDecoratorHandler
{
    private final List<IItemDecorator> itemDecorators = new ArrayList<>();
    /**
     * used to make it possible to add/remove itemDecorators during item decorator rendering, so no {@link java.util.ConcurrentModificationException ConcurrentModificationException} is thrown
     */
    private final List<IItemDecorator> toAdd = new ArrayList<>();
    private final List<IItemDecorator> toRemove = new ArrayList<>();

    @Override
    public void addDecorator(IItemDecorator itemDecorator)
    {
        toAdd.add(itemDecorator);
    }
    @Override
    public void removeDecorator(IItemDecorator itemDecorator)
    {
        toRemove.add(itemDecorator);
    }

    @Override
    public void render(Font font, ItemStack stack, int xOffset, int yOffset, float blitOffset)
    {
        itemDecorators.addAll(toAdd);
        toAdd.clear();
        itemDecorators.removeAll(toRemove);
        toRemove.clear();
        for (IItemDecorator itemDecorator : itemDecorators)
        {
            itemDecorator.render(font, stack, xOffset, yOffset, blitOffset);
        }
    }
}
