/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import com.mojang.serialization.Dynamic;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.BrainBuilder;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;

/**
 * LivingMakeBrainEvent is fired whenever a new {@link net.minecraft.world.entity.ai.Brain} instance is created using {@link LivingEntity#makeBrain(Dynamic)}.<br>
 * <br>
 * To access the internal {@link BrainBuilder}, call {@link LivingMakeBrainEvent#getTypedBrainBuilder(LivingEntity)} using the downcasted {@link LivingEntity} obtained from {@link LivingEvent#getEntity()}.<br>
 * <br>
 * The BrainBuilder will initially contain all the state found in the original Brain instance.<br>
 * <br>
 * After this event is posted, a fresh Brain instance will be created using the encapsulated state found in the BrainBuilder
 * and replace the previously created Brain instance for the entity.<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingMakeBrain(LivingEntity, Brain, Dynamic)}.<br>
 *
 * @deprecated This event is scheduled for removal in 26.4 due to a lack of adoption by mods, in order to ease porting.
 *             Please reach out to the Forge team if you are relying on this and would like to see it remain in Forge.
 *             We may keep it or add it back in a future version if there is enough demand.
 **/
@Deprecated(forRemoval = true, since = "26.4")
public final class LivingMakeBrainEvent extends MutableEvent implements LivingEvent {
    public static final EventBus<LivingMakeBrainEvent> BUS = EventBus.create(LivingMakeBrainEvent.class);

    private final LivingEntity entity;
    private final BrainBuilder<?> brainBuilder;

    public LivingMakeBrainEvent(LivingEntity entity, BrainBuilder<?> brainBuilder) {
        this.entity = entity;
        this.brainBuilder = brainBuilder;
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    @SuppressWarnings("unchecked")
    public <E extends LivingEntity> BrainBuilder<E> getTypedBrainBuilder(E ignoredEntity) {
        return (BrainBuilder<E>) this.brainBuilder;
    }
}
