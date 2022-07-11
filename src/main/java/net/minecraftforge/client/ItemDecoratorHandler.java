/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.client.gui.Font;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ItemDecoratorHandler
{
    private final List<IItemDecorator> itemDecorators = new ArrayList<>();

    public static ItemDecoratorHandler of(ItemStack stack)
    {
        return of(stack.getItem());
    }

    public static ItemDecoratorHandler of(Item item)
    {
        if (item.getItemDecoratorHandler() instanceof ItemDecoratorHandler e)
        {
            return e;
        }
        throw new IllegalStateException("No ItemDecoratorHandler was added to: " + ForgeRegistries.ITEMS.getKey(item).toString());
    }

    /**
     *
     * Add a decorator during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent} with {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent#enqueueWork(Runnable)}.
     */
    public void addDecorator(IItemDecorator itemDecorator)
    {
        itemDecorators.add(itemDecorator);
    }

    public void render(Font font, ItemStack stack, int xOffset, int yOffset, float blitOffset)
    {
        for (IItemDecorator itemDecorator : itemDecorators)
        {
            itemDecorator.render(font, stack, xOffset, yOffset, blitOffset);
        }
    }
}
