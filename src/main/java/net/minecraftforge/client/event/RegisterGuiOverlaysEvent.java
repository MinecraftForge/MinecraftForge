/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/** singularity 1.20.5 - Removed, Mojang created a layered rendering system that should make this all obsolete finally.. - Lex 042724
package net.minecraftsingularity.client.event;

import com.google.common.base.Preconditions;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.client.gui.overlay.IGuiOverlay;
import net.minecraftsingularity.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftsingularity.eventbus.api.Cancelable;
import net.minecraftsingularity.eventbus.api.Event;
import net.minecraftsingularity.fml.LogicalSide;
import net.minecraftsingularity.fml.ModLoadingContext;
import net.minecraftsingularity.fml.event.IModBusEvent;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Allows users to register custom {@link IGuiOverlay GUI overlays}.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 * /
public class RegisterGuiOverlaysEvent extends Event implements IModBusEvent
{
    private final Map<Identifier, IGuiOverlay> overlays;
    private final List<Identifier> orderedOverlays;

    @ApiStatus.Internal
    public RegisterGuiOverlaysEvent(Map<Identifier, IGuiOverlay> overlays, List<Identifier> orderedOverlays)
    {
        this.overlays = overlays;
        this.orderedOverlays = orderedOverlays;
    }

    /**
     * Registers an overlay that renders below all others.
     *
     * @param id      A unique resource id for this overlay
     * @param overlay The overlay
     * /
    public void registerBelowAll(@NotNull String id, @NotNull IGuiOverlay overlay)
    {
        register(Ordering.BEFORE, null, id, overlay);
    }

    /**
     * Registers an overlay that renders below another.
     *
     * @param other   The id of the overlay to render below. This must be an overlay you have already registered or a
     *                {@link VanillaGuiOverlay vanilla overlay}. Do not use other mods' overlays.
     * @param id      A unique resource id for this overlay
     * @param overlay The overlay
     * /
    public void registerBelow(@NotNull Identifier other, @NotNull String id, @NotNull IGuiOverlay overlay)
    {
        register(Ordering.BEFORE, other, id, overlay);
    }

    /**
     * Registers an overlay that renders above another.
     *
     * @param other   The id of the overlay to render above. This must be an overlay you have already registered or a
     *                {@link VanillaGuiOverlay vanilla overlay}. Do not use other mods' overlays.
     * @param id      A unique resource id for this overlay
     * @param overlay The overlay
     * /
    public void registerAbove(@NotNull Identifier other, @NotNull String id, @NotNull IGuiOverlay overlay)
    {
        register(Ordering.AFTER, other, id, overlay);
    }

    /**
     * Registers an overlay that renders above all others.
     *
     * @param id      A unique resource id for this overlay
     * @param overlay The overlay
     * /
    public void registerAboveAll(@NotNull String id, @NotNull IGuiOverlay overlay)
    {
        register(Ordering.AFTER, null, id, overlay);
    }

    private void register(@NotNull Ordering ordering, @Nullable Identifier other, @NotNull String id, @NotNull IGuiOverlay overlay)
    {
        var key = new Identifier(ModLoadingContext.get().getActiveNamespace(), id);
        Preconditions.checkArgument(!overlays.containsKey(key), "Overlay already registered: " + key);

        int insertPosition;
        if (other == null)
        {
            insertPosition = ordering == Ordering.BEFORE ? 0 : overlays.size();
        }
        else
        {
            int otherIndex = orderedOverlays.indexOf(other);
            Preconditions.checkState(otherIndex >= 0, "Attempted to order against an unregistered overlay. Only order against vanilla's and your own.");
            insertPosition = otherIndex + (ordering == Ordering.BEFORE ? 0 : 1);
        }

        overlays.put(key, overlay);
        orderedOverlays.add(insertPosition, key);
    }

    private enum Ordering
    {
        BEFORE, AFTER
    }
}
*/
