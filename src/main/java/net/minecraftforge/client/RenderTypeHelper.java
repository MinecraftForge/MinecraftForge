/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

/**
 * Provides helper functions replacing those in {@link ItemBlockRenderTypes}.
 */
public final class RenderTypeHelper {
    /**
     * Provides a {@link RenderType} using {@link DefaultVertexFormat#NEW_ENTITY} for the given {@link DefaultVertexFormat#BLOCK} format.
     * This should be called for each {@link RenderType} returned by {@link BakedModel#getRenderTypes(BlockState, RandomSource, ModelData)}.
     * <p>
     * Mimics the behavior of vanilla's {@link ItemBlockRenderTypes#getRenderType(BlockState)}.
     */
    @NotNull
    public static RenderType getEntityRenderType(ChunkSectionLayer layer) {
        return layer == ChunkSectionLayer.TRANSLUCENT ? Sheets.translucentItemSheet() : Sheets.cutoutBlockSheet();
    }

    /**
     * Provides a {@link RenderType} fit for rendering moving blocks given the specified chunk render type.
     * This should be called for each {@link RenderType} returned by {@link BakedModel#getRenderTypes(BlockState, RandomSource, ModelData)}.
     * <p>
     * Mimics the behavior of vanilla's {@link ItemBlockRenderTypes#getMovingBlockRenderType(BlockState)}.
     */
    @NotNull
    public static RenderType getMovingBlockRenderType(ChunkSectionLayer layer) {
        if (layer == null)
            return RenderType.solid();

        return switch (layer) {
            case SOLID -> RenderType.solid();
            case CUTOUT_MIPPED -> RenderType.cutoutMipped();
            case CUTOUT -> RenderType.cutout();
            case TRANSLUCENT -> RenderType.translucentMovingBlock();
            case TRIPWIRE -> RenderType.tripwire();
        };
    }

    /**
     * Provides a fallback {@link RenderType} for the given {@link ItemStack} in the case that none is explicitly specified.
     * <p>
     * Mimics the behavior of vanilla's {@link ItemBlockRenderTypes#getRenderType(ItemStack, boolean)}
     * but removes the need to query the model again if the item is a {@link BlockItem}.
     */
    @NotNull
    public static RenderType getFallbackItemRenderType(ItemStack stack, BlockStateModel model) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            var renderTypes = model.getRenderTypes(blockItem.getBlock().defaultBlockState(), RandomSource.create(42), ModelData.EMPTY);
            if (renderTypes.contains(ChunkSectionLayer.TRANSLUCENT))
                return getEntityRenderType(ChunkSectionLayer.TRANSLUCENT);
            return Sheets.cutoutBlockSheet();
        }
        return Sheets.translucentItemSheet();
    }

    private RenderTypeHelper() {}
}
