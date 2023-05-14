/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Manager for {@link ClientTooltipComponent} factories.
 * <p>
 * Provides a lookup.
 */
public final class ClientTooltipComponentManager
{
    private static ImmutableMap<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>> FACTORIES;

    /**
     * Creates a client component for the given argument, or null if unsupported.
     */
    @Nullable
    public static ClientTooltipComponent createClientTooltipComponent(TooltipComponent component)
    {
        var factory = FACTORIES.get(component.getClass());
        return factory != null ? factory.apply(component) : null;
    }

    @ApiStatus.Internal
    public static void init()
    {
        var factories = new HashMap<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>>();
        var event = new RegisterClientTooltipComponentFactoriesEvent(factories);
        ModLoader.get().postEventWrapContainerInModOrder(event);
        FACTORIES = ImmutableMap.copyOf(factories);
    }

    private ClientTooltipComponentManager()
    {
    }
}
