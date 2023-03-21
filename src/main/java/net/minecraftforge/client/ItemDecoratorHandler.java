/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;

@ApiStatus.Internal
public final class ItemDecoratorHandler
{
    private final List<IItemDecorator> itemDecorators;

    private static Map<Item, ItemDecoratorHandler> DECORATOR_LOOKUP = ImmutableMap.of();

    private static final ItemDecoratorHandler EMPTY = new ItemDecoratorHandler();
    private ItemDecoratorHandler()
    {
        this.itemDecorators = ImmutableList.of();
    }
    private ItemDecoratorHandler(List<IItemDecorator> itemDecorators)
    {
        this.itemDecorators = ImmutableList.copyOf(itemDecorators);
    }

    public static void init()
    {
        var decorators = new HashMap<Item, List<IItemDecorator>>();
        var event = new RegisterItemDecorationsEvent(decorators);
        ModLoader.get().postEventWithWrapInModOrder(event, (mc, e) -> ModLoadingContext.get().setActiveContainer(mc), (mc, e) -> ModLoadingContext.get().setActiveContainer(null));
        var builder = new ImmutableMap.Builder<Item, ItemDecoratorHandler>();
        decorators.forEach((item, itemDecorators) -> builder.put(item, new ItemDecoratorHandler(itemDecorators)));
        DECORATOR_LOOKUP = builder.build();
    }

    public static ItemDecoratorHandler of(ItemStack stack)
    {
        return DECORATOR_LOOKUP.getOrDefault(stack.getItem(), EMPTY);
    }

    public void render(PoseStack poseStack, Font font, ItemStack stack, int xOffset, int yOffset)
    {
        resetRenderState();
        for (IItemDecorator itemDecorator : itemDecorators)
        {
            if (itemDecorator.render(poseStack, font, stack, xOffset, yOffset))
                resetRenderState();
        }
    }

    private void resetRenderState()
    {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }
}
