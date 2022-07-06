/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class ForgeItemBlockRenderTypes
{
    private static final Predicate<RenderType> SOLID_PREDICATE = type -> type == RenderType.solid();
    private static final Object LOCK = new Object();

    private static final Map<Holder.Reference<Block>, Predicate<RenderType>> blockRenderChecks = createMap();
    private static final Map<Holder.Reference<Fluid>, Predicate<RenderType>> fluidRenderChecks = createMap();

    @Nullable
    private static volatile Map<Holder.Reference<Block>, Predicate<RenderType>> blockRenderChecksReadOnly = null;
    @Nullable
    private static volatile Map<Holder.Reference<Fluid>, Predicate<RenderType>> fluidRenderChecksReadOnly = null;

    public static void setRenderLayer(Block block, Predicate<RenderType> predicate)
    {
        Objects.requireNonNull(predicate);
        synchronized (LOCK)
        {
            blockRenderChecks.put(ForgeRegistries.BLOCKS.getDelegateOrThrow(block), predicate);
            blockRenderChecksReadOnly = null;
        }
    }

    public static void setRenderLayer(Fluid fluid, Predicate<RenderType> predicate)
    {
        Objects.requireNonNull(predicate);
        synchronized (LOCK)
        {
            fluidRenderChecks.put(ForgeRegistries.FLUIDS.getDelegateOrThrow(fluid), predicate);
            fluidRenderChecksReadOnly = null;
        }
    }

    protected static Map<Holder.Reference<Block>, Predicate<RenderType>> getBlockLayerPredicates()
    {
        Map<Holder.Reference<Block>, Predicate<RenderType>> map = blockRenderChecksReadOnly;
        if (map == null)
        {
            synchronized (LOCK)
            {
                blockRenderChecksReadOnly = map = copy(blockRenderChecks);
            }
        }
        return map;
    }

    protected static Map<Holder.Reference<Fluid>, Predicate<RenderType>> getFluidLayerPredicates()
    {
        Map<Holder.Reference<Fluid>, Predicate<RenderType>> map = fluidRenderChecksReadOnly;
        if (map == null)
        {
            synchronized (LOCK)
            {
                fluidRenderChecksReadOnly = map = copy(fluidRenderChecks);
            }
        }
        return map;
    }

    private static <T> Map<Holder.Reference<T>, Predicate<RenderType>> copy(Map<Holder.Reference<T>, Predicate<RenderType>> map)
    {
        var newMap = new Object2ObjectOpenHashMap<>(map);
        newMap.defaultReturnValue(SOLID_PREDICATE);
        return newMap;
    }

    private static <T> Map<Holder.Reference<T>, Predicate<RenderType>> createMap()
    {
        Object2ObjectMap<Holder.Reference<T>, Predicate<RenderType>> map = new Object2ObjectOpenHashMap<>(
                Object2ObjectOpenHashMap.DEFAULT_INITIAL_SIZE, .75F
        );
        map.defaultReturnValue(SOLID_PREDICATE);
        return map;
    }

    protected static void initFromVanillaMaps(Map<Block, RenderType> typeByBlock, Map<Fluid, RenderType> typeByFluid)
    {
        for (Map.Entry<Block, RenderType> entry : typeByBlock.entrySet())
        {
            RenderType type = entry.getValue();
            blockRenderChecks.put(ForgeRegistries.BLOCKS.getDelegateOrThrow(entry.getKey()), t -> t == type);
        }

        for (Map.Entry<Fluid, RenderType> entry : typeByFluid.entrySet())
        {
            RenderType type = entry.getValue();
            fluidRenderChecks.put(ForgeRegistries.FLUIDS.getDelegateOrThrow(entry.getKey()), t -> t == type);
        }
    }
}
