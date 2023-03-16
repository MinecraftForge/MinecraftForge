/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterNamedRenderTypesEvent;
import net.minecraftforge.client.renderer.BufferManager;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @deprecated Use {@link BufferManager} instead.
 * <p>
 * Manager for named {@link RenderType render types}.
 * <p>
 * Provides a lookup.
 */
@Deprecated
public final class NamedRenderTypeManager
{
    private static BufferManager bufferManager;

    /**
     * Finds the {@link RenderTypeGroup} for a given name, or the {@link RenderTypeGroup#EMPTY empty group} if not found.
     */
    public static RenderTypeGroup get(ResourceLocation name)
    {
        return bufferManager.getNamedRenderType(name);
    }

    @ApiStatus.Internal
    public static void init(BufferManager bufferManager)
    {
        NamedRenderTypeManager.bufferManager = bufferManager; 
    }

    private NamedRenderTypeManager()
    {
    }
}
