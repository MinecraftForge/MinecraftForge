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
import net.minecraftforge.eventbus.api.event.InheritableEvent;
import net.minecraftforge.eventbus.api.event.MutableEvent;
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
public sealed class ExplosionEvent extends MutableEvent implements InheritableEvent {
    public static final EventBus<ExplosionEvent> BUS = EventBus.create(ExplosionEvent.class);

    private final Level level;
    private final Explosion explosion;

    public ExplosionEvent(Level level, Explosion explosion) {
        this.level = level;
        this.explosion = explosion;
    }

    public Level getLevel() {
        return level;
    }

    public Explosion getExplosion() {
        return explosion;
    }

    /** ExplosionEvent.Start is fired before the explosion actually occurs.  Canceling this event will stop the explosion.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * This event does not use {@link HasResult}.<br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     */
    public static final class Start extends ExplosionEvent implements Cancellable {
        public static final CancellableEventBus<Start> BUS = CancellableEventBus.create(Start.class);

        public Start(Level level, Explosion explosion) {
            super(level, explosion);
        }
    }

    /** ExplosionEvent.Detonate is fired once the explosion has a list of affected blocks and entities.  These lists can be modified to change the outcome.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * This event does not use {@link HasResult}.<br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     */
    public static final class Detonate extends ExplosionEvent {
        public static final EventBus<Detonate> BUS = EventBus.create(Detonate.class);

        private final List<BlockPos> blocks;
        private final List<Entity> entityList;

        public Detonate(Level level, Explosion explosion, List<BlockPos> blocks, List<Entity> entityList) {
            super(level, explosion);
            this.blocks = blocks;
            this.entityList = entityList;
        }

        /** return the list of blocks affected by the explosion. */
        public List<BlockPos> getAffectedBlocks() {
            return blocks;
        }

        /** return the list of entities affected by the explosion. */
        public List<Entity> getAffectedEntities() {
            return entityList;
        }
    }
}
