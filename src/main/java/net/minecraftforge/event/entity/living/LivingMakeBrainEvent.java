/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import com.mojang.serialization.Dynamic;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BrainBuilder;
import net.minecraftforge.eventbus.api.Cancelable;

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
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class LivingMakeBrainEvent extends LivingEvent
{
    private final BrainBuilder<?> brainBuilder;

    public LivingMakeBrainEvent(LivingEntity entity, BrainBuilder<?> brainBuilder)
    {
        super(entity);
        this.brainBuilder = brainBuilder;
    }

    public <E extends LivingEntity> BrainBuilder<E> getTypedBrainBuilder(E ignoredEntity)
    {
        return (BrainBuilder<E>) this.brainBuilder;
    }
}
