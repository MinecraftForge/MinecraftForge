/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manager for custom {@link ColorResolver} instances, collected via {@link RegisterColorHandlersEvent.ColorResolvers}.
 */
public final class ColorResolverManager {
    private ColorResolverManager() {}

    private static List<ColorResolver> colorResolvers;

    @ApiStatus.Internal
    public static void init() {
        var builder = new ArrayList<ColorResolver>();
        RegisterColorHandlersEvent.ColorResolvers.BUS.post(new RegisterColorHandlersEvent.ColorResolvers(builder));
        colorResolvers = List.copyOf(builder);
    }

    /**
     * Register a {@link BlockTintCache} for every registered {@link ColorResolver} into the given target map.
     *
     * @param level the level to use
     * @param target the map to populate
     */
    public static void registerBlockTintCaches(ClientLevel level, Map<ColorResolver, BlockTintCache> target) {
        for (var resolver : colorResolvers) {
            target.put(resolver, new BlockTintCache(pos -> level.calculateBlockTint(pos, resolver)));
        }
    }
}
