/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.level;

import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.InheritableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;

/** ExplosionEvent triggers when an explosion happens in the level.<br>
 * <br>
 * ExplosionEvent.Start is fired before the explosion actually occurs.<br>
 * ExplosionEvent.Detonate is fired once the explosion has a list of affected blocks and entities.<br>
 * <br>
 * ExplosionEvent.Start is {@link Cancellable}.<br>
 * ExplosionEvent.Detonate can modify the affected blocks and entities.
 */
public sealed interface ExplosionEvent extends InheritableEvent {
    EventBus<ExplosionEvent> BUS = EventBus.create(ExplosionEvent.class);

    Level getLevel();

    Explosion getExplosion();

    /**
     * ExplosionEvent.Start is fired before the explosion actually occurs.<br>
     * This event is {@linkplain Cancellable cancellable}. Cancelling this event will stop the explosion.
     */
    record Start(Level getLevel, Explosion getExplosion) implements Cancellable, ExplosionEvent {
        public static final CancellableEventBus<Start> BUS = CancellableEventBus.create(Start.class);
    }

    /**
     * ExplosionEvent.Detonate is fired once the explosion has a list of affected blocks and entities.
     * These lists can be modified to change the outcome.
     *
     * @param getAffectedBlocks the modifiable list of blocks affected by the explosion
     * @param getAffectedEntities the modifiable list of entities affected by the explosion
     */
    record Detonate(
            Level getLevel,
            Explosion getExplosion,
            List<BlockPos> getAffectedBlocks,
            List<Entity> getAffectedEntities
    ) implements ExplosionEvent {
        public static final EventBus<Detonate> BUS = EventBus.create(Detonate.class);
    }
}
