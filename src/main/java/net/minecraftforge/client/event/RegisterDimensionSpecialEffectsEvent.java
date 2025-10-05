/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

/**
 * Allows users to register custom {@link DimensionSpecialEffects} for their dimensions.
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
@NullMarked
public final class RegisterDimensionSpecialEffectsEvent extends MutableEvent implements SelfDestructing {
    public static final EventBus<RegisterDimensionSpecialEffectsEvent> BUS = EventBus.create(RegisterDimensionSpecialEffectsEvent.class);

    @Deprecated(forRemoval = true, since = "1.21.9")
    public static EventBus<RegisterDimensionSpecialEffectsEvent> getBus(BusGroup modBusGroup) {
        return BUS;
    }

    private final Map<ResourceLocation, DimensionSpecialEffects> effects;

    @ApiStatus.Internal
    public RegisterDimensionSpecialEffectsEvent(Map<ResourceLocation, DimensionSpecialEffects> effects) {
        this.effects = effects;
    }

    /**
     * Registers the effects for a given dimension type.
     */
    public void register(ResourceLocation dimensionType, DimensionSpecialEffects effects) {
        this.effects.put(dimensionType, effects);
    }
}
