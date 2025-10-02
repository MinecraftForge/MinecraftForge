/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Manager for {@link ClientTooltipComponent} factories.
 * <p>
 * Provides a lookup.
 */
public final class ClientTooltipComponentManager {
    private static Map<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>> FACTORIES;

    /**
     * Creates a client component for the given argument
     */
    public static @NonNull ClientTooltipComponent createClientTooltipComponent(@NonNull TooltipComponent component) {
        var factory = FACTORIES.get(component.getClass());
        var ret = factory != null ? factory.apply(component) : null;
        if (ret == null)
            throw new IllegalArgumentException("Unknown TooltipComponent");
        return ret;
    }

    @ApiStatus.Internal
    public static void init() {
        var factories = new HashMap<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>>();
        var event = new RegisterClientTooltipComponentFactoriesEvent(factories);
        RegisterClientTooltipComponentFactoriesEvent.BUS.post(event);
        FACTORIES = Map.copyOf(factories);
    }

    private ClientTooltipComponentManager() {}
}
