/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.jetbrains.annotations.Nullable;

/**
 * VanillaGameEvent is fired on the server whenever one of Vanilla's {@link GameEvent GameEvents} fire. <br>
 * <br>
 * This allows for listening to Vanilla's events in a more structured and global way that is not tied to needing a block entity listener. <br>
 * <br>
 * Cancel this event to prevent Vanilla from posting the {@link GameEvent} to all nearby {@link net.minecraft.world.level.gameevent.GameEventListener GameEventListeners}.
 *
 * @param getLevel The level the Vanilla {@link GameEvent} occurred in
 * @param getVanillaEvent The Vanilla event
 * @param getEventPosition The position the event took place at
 * @param getContext The context of the Vanilla event
 **/
public record VanillaGameEvent(
        Level getLevel,
        GameEvent getVanillaEvent,
        Vec3 getEventPosition,
        GameEvent.Context getContext
) implements Cancellable, RecordEvent {
    public static final CancellableEventBus<VanillaGameEvent> BUS = CancellableEventBus.create(VanillaGameEvent.class);

    /**
     * @return The entity that was the source or "cause" of the {@link GameEvent}.
     */
    @Nullable
    public Entity getCause() {
        return getContext.sourceEntity();
    }
}
