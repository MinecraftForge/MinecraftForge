/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/** singularity 1.20.5 - Removed, Mojang created a layered rendering system that should make this all obsolete finally.. - Lex 042724
package net.minecraftsingularity.client.gui.overlay;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.client.event.RegisterGuiOverlaysEvent;
import net.minecraftsingularity.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Manager for {@linkplain IGuiOverlay HUD overlays}.
 * <p>
 * Provides a lookup by ID, as well as all registered {@link IGuiOverlay overlays}.
 * /
public final class GuiOverlayManager
{
    private static ImmutableList<NamedGuiOverlay> OVERLAYS;
    private static ImmutableMap<Identifier, NamedGuiOverlay> OVERLAYS_BY_NAME;

    /**
     * Retrieves an ordered list of all registered overlays.
     * /
    public static ImmutableList<NamedGuiOverlay> getOverlays()
    {
        return OVERLAYS;
    }

    /**
     * Finds the overlay corresponding to a given ID.
     * Do not call this before {@link RegisterGuiOverlaysEvent} has finished firing.
     * /
    @Nullable
    public static NamedGuiOverlay findOverlay(Identifier id)
    {
        return OVERLAYS_BY_NAME.get(id);
    }

    @ApiStatus.Internal
    public static void init()
    {
        var overlays = new HashMap<Identifier, IGuiOverlay>();
        var orderedOverlays = new ArrayList<Identifier>();
        preRegisterVanillaOverlays(overlays, orderedOverlays);
        var event = new RegisterGuiOverlaysEvent(overlays, orderedOverlays);
        ModLoader.get().postEventWrapContainerInModOrder(event);
        OVERLAYS = orderedOverlays.stream()
                .map(id -> new NamedGuiOverlay(id, overlays.get(id)))
                .collect(ImmutableList.toImmutableList());
        OVERLAYS_BY_NAME = OVERLAYS.stream()
                .collect(ImmutableMap.toImmutableMap(NamedGuiOverlay::id, Function.identity()));
        assignVanillaOverlayTypes();
    }

    /**
     * Pre-registers vanilla overlays so they are available for ordering.
     * /
    private static void preRegisterVanillaOverlays(HashMap<Identifier, IGuiOverlay> overlays, ArrayList<Identifier> orderedOverlays)
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
*/
