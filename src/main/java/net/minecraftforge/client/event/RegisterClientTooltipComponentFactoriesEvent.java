/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.function.Function;

/**
 * Allows users to register custom {@link net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent}
 * factories for their {@link net.minecraft.world.inventory.tooltip.TooltipComponent} types.
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
@NullMarked
public final class RegisterClientTooltipComponentFactoriesEvent extends MutableEvent implements SelfDestructing {
    public static final EventBus<RegisterClientTooltipComponentFactoriesEvent> BUS = EventBus.create(RegisterClientTooltipComponentFactoriesEvent.class);

    @Deprecated(forRemoval = true, since = "1.21.9")
    public static EventBus<RegisterClientTooltipComponentFactoriesEvent> getBus(BusGroup modBusGroup) {
        return BUS;
    }

    private final Map<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>> factories;

    @ApiStatus.Internal
    public RegisterClientTooltipComponentFactoriesEvent(Map<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>> factories) {
        this.factories = factories;
    }

    /**
     * Registers a {@link ClientTooltipComponent} factory for a {@link TooltipComponent}.
     */
    @SuppressWarnings("unchecked")
    public <T extends TooltipComponent> void register(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory) {
        factories.put(type, (Function<TooltipComponent, ClientTooltipComponent>) factory);
    }
}
