/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.function.Function;

/**
 * Allows users to register custom {@link net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent}
 * factories for their {@link net.minecraft.world.inventory.tooltip.TooltipComponent} types.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public class RegisterClientTooltipComponentFactoriesEvent extends Event implements IModBusEvent
{
    private final Map<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>> factories;

    @ApiStatus.Internal
    public RegisterClientTooltipComponentFactoriesEvent(Map<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>> factories)
    {
        this.factories = factories;
    }

    /**
     * Registers a {@link ClientTooltipComponent} factory for a {@link TooltipComponent}.
     */
    @SuppressWarnings("unchecked")
    public <T extends TooltipComponent> void register(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory)
    {
        factories.put(type, (Function<TooltipComponent, ClientTooltipComponent>) factory);
    }
}
