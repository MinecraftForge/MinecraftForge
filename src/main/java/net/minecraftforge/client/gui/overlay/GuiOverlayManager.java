/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.overlay;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Manager for {@linkplain IGuiOverlay HUD overlays}.
 * <p>
 * Provides a lookup by ID, as well as all registered {@link IGuiOverlay overlays}.
 */
public final class GuiOverlayManager
{
    private static ImmutableList<NamedGuiOverlay> OVERLAYS;
    private static ImmutableMap<ResourceLocation, NamedGuiOverlay> OVERLAYS_BY_NAME;

    /**
     * Retrieves an ordered list of all registered overlays.
     */
    public static ImmutableList<NamedGuiOverlay> getOverlays()
    {
        return OVERLAYS;
    }

    /**
     * Finds the overlay corresponding to a given ID.
     * Do not call this before {@link RegisterGuiOverlaysEvent} has finished firing.
     */
    @Nullable
    public static NamedGuiOverlay findOverlay(ResourceLocation id)
    {
        return OVERLAYS_BY_NAME.get(id);
    }

    @ApiStatus.Internal
    public static void init()
    {
        var overlays = new HashMap<ResourceLocation, IGuiOverlay>();
        var orderedOverlays = new ArrayList<ResourceLocation>();
        preRegisterVanillaOverlays(overlays, orderedOverlays);
        var event = new RegisterGuiOverlaysEvent(overlays, orderedOverlays);
        ModLoader.get().postEventWithWrapInModOrder(event, (mc, e) -> ModLoadingContext.get().setActiveContainer(mc), (mc, e) -> ModLoadingContext.get().setActiveContainer(null));
        OVERLAYS = orderedOverlays.stream()
                .map(id -> new NamedGuiOverlay(id, overlays.get(id)))
                .collect(ImmutableList.toImmutableList());
        OVERLAYS_BY_NAME = OVERLAYS.stream()
                .collect(ImmutableMap.toImmutableMap(NamedGuiOverlay::id, Function.identity()));
        assignVanillaOverlayTypes();
    }

    /**
     * Pre-registers vanilla overlays so they are available for ordering.
     */
    private static void preRegisterVanillaOverlays(HashMap<ResourceLocation, IGuiOverlay> overlays, ArrayList<ResourceLocation> orderedOverlays)
    {
        for (var entry : VanillaGuiOverlay.values())
        {
            overlays.put(entry.id(), entry.overlay);
            orderedOverlays.add(entry.id());
        }
    }

    private static void assignVanillaOverlayTypes()
    {
        for (var entry : VanillaGuiOverlay.values())
            entry.type = OVERLAYS_BY_NAME.get(entry.id());
    }

    private GuiOverlayManager()
    {
    }
}
