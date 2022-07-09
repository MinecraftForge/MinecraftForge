/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
 */
public class RegisterGuiOverlaysEvent extends Event implements IModBusEvent
{
    private final Map<ResourceLocation, IGuiOverlay> overlays;
    private final List<ResourceLocation> orderedOverlays;

    @ApiStatus.Internal
    public RegisterGuiOverlaysEvent(Map<ResourceLocation, IGuiOverlay> overlays, List<ResourceLocation> orderedOverlays)
    {
        this.overlays = overlays;
        this.orderedOverlays = orderedOverlays;
    }

    /**
     * Registers an overlay that renders below all others.
     *
     * @param id      A unique resource id for this overlay
     * @param overlay The overlay
     */
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
     */
    public void registerBelow(@NotNull ResourceLocation other, @NotNull String id, @NotNull IGuiOverlay overlay)
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
     */
    public void registerAbove(@NotNull ResourceLocation other, @NotNull String id, @NotNull IGuiOverlay overlay)
    {
        register(Ordering.AFTER, other, id, overlay);
    }

    /**
     * Registers an overlay that renders above all others.
     *
     * @param id      A unique resource id for this overlay
     * @param overlay The overlay
     */
    public void registerAboveAll(@NotNull String id, @NotNull IGuiOverlay overlay)
    {
        register(Ordering.AFTER, null, id, overlay);
    }

    private void register(@NotNull Ordering ordering, @Nullable ResourceLocation other, @NotNull String id, @NotNull IGuiOverlay overlay)
    {
        var key = new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), id);
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
