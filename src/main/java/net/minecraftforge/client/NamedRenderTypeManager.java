/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.client.event.RegisterNamedRenderTypesEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for named {@link RenderType render types}.
 * <p>
 * Provides a lookup.
 */
public final class NamedRenderTypeManager {
    private static Map<Identifier, RenderTypeGroup> RENDER_TYPES;

    /**
     * Finds the {@link RenderTypeGroup} for a given name, or the {@link RenderTypeGroup#EMPTY empty group} if not found.
     */
    public static RenderTypeGroup get(Identifier name) {
        return RENDER_TYPES.getOrDefault(name, RenderTypeGroup.EMPTY);
    }

    @ApiStatus.Internal
    public static void init() {
        var renderTypes = new HashMap<Identifier, RenderTypeGroup>();
        preRegisterVanillaRenderTypes(renderTypes);
        RegisterNamedRenderTypesEvent.BUS.post(new RegisterNamedRenderTypesEvent(renderTypes));
        RENDER_TYPES = Map.copyOf(renderTypes);
    }

    /**
     * Pre-registers vanilla render types.
     */
    private static void preRegisterVanillaRenderTypes(Map<Identifier, RenderTypeGroup> blockRenderTypes) {
        blockRenderTypes.put(rl("solid"), new RenderTypeGroup(ChunkSectionLayer.SOLID, singularityRenderTypes.ITEM_LAYERED_SOLID.get()));
        blockRenderTypes.put(rl("cutout"), new RenderTypeGroup(ChunkSectionLayer.CUTOUT, singularityRenderTypes.ITEM_LAYERED_CUTOUT.get()));
    }

    private static Identifier rl(String path) {
        return Identifier.withDefaultNamespace(path);
    }

    private NamedRenderTypeManager() {}
}
