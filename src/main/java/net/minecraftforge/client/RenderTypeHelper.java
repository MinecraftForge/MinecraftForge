/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

/**
 * Provides helper functions replacing those in {@link ItemBlockRenderTypes}.
 */
public final class RenderTypeHelper
{
    /**
     * Provides a {@link RenderType} using {@link DefaultVertexFormat#NEW_ENTITY} for the given {@link DefaultVertexFormat#BLOCK} format.
     * This should be called for each {@link RenderType} returned by {@link BakedModel#getRenderTypes(BlockState, RandomSource, ModelData)}.
     * <p>
     * Mimics the behavior of vanilla's {@link ItemBlockRenderTypes#getRenderType(BlockState, boolean)}.
     */
    @NotNull
    public static RenderType getEntityRenderType(RenderType chunkRenderType, boolean cull)
    {
        if (chunkRenderType != RenderType.translucent())
            return Sheets.cutoutBlockSheet();
        return cull || !Minecraft.useShaderTransparency() ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet();
    }

    /**
     * Provides a {@link RenderType} fit for rendering moving blocks given the specified chunk render type.
     * This should be called for each {@link RenderType} returned by {@link BakedModel#getRenderTypes(BlockState, RandomSource, ModelData)}.
     * <p>
     * Mimics the behavior of vanilla's {@link ItemBlockRenderTypes#getMovingBlockRenderType(BlockState)}.
     */
    @NotNull
    public static RenderType getMovingBlockRenderType(RenderType renderType)
    {
        if (renderType == RenderType.translucent())
            return RenderType.translucentMovingBlock();
        return renderType;
    }

    /**
     * Provides a fallback {@link RenderType} for the given {@link ItemStack} in the case that none is explicitly specified.
     * <p>
     * Mimics the behavior of vanilla's {@link ItemBlockRenderTypes#getRenderType(ItemStack, boolean)}
     * but removes the need to query the model again if the item is a {@link BlockItem}.
     */
    @NotNull
    public static RenderType getFallbackItemRenderType(ItemStack stack, BakedModel model, boolean cull)
    {
        if (stack.getItem() instanceof BlockItem blockItem)
        {
            var renderTypes = model.getRenderTypes(blockItem.getBlock().defaultBlockState(), RandomSource.create(42), ModelData.EMPTY);
            if (renderTypes.contains(RenderType.translucent()))
                return getEntityRenderType(RenderType.translucent(), cull);
            return Sheets.cutoutBlockSheet();
        }
        return cull ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet();
    }

    private RenderTypeHelper()
    {
    }
}
