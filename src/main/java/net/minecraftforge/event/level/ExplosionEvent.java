/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.level;

import java.util.List;

import net.minecraftforge.common.MinecraftForge;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;

/** ExplosionEvent triggers when an explosion happens in the level.<br>
 * <br>
 * ExplosionEvent.Start is fired before the explosion actually occurs.<br>
 * ExplosionEvent.Detonate is fired once the explosion has a list of affected blocks and entities.<br>
 * <br>
 * ExplosionEvent.Start is {@link Cancelable}.<br>
 * ExplosionEvent.Detonate can modify the affected blocks and entities.<br>
 * Children do not use {@link HasResult}.<br>
 * Children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 */
public sealed interface ExplosionEvent {
    Level level();
    Explosion explosion();

    /** ExplosionEvent.Start is fired before the explosion actually occurs.  Canceling this event will stop the explosion.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * This event does not use {@link HasResult}.<br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     */
    record Start(Level level, Explosion explosion) implements Cancellable, ExplosionEvent, RecordEvent {
        public static final CancellableEventBus<ExplosionEvent.Start> BUS = CancellableEventBus.create(ExplosionEvent.Start.class);
    }

    /** ExplosionEvent.Detonate is fired once the explosion has a list of affected blocks and entities.  These lists can be modified to change the outcome.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * This event does not use {@link HasResult}.<br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     *
     * @param affectedBlocks the list of blocks affected by the explosion.
     * @param affectedEntities the list of entities affected by the explosion.
     */
    record Detonate(Level level, Explosion explosion, List<BlockPos> affectedBlocks, List<Entity> affectedEntities)
            implements ExplosionEvent, RecordEvent {
        public static final EventBus<Detonate> BUS = EventBus.create(ExplosionEvent.Detonate.class);
    }
}
