/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class LootingLevelEvent extends MutableEvent implements LivingEvent {
    public static final EventBus<LootingLevelEvent> BUS = EventBus.create(LootingLevelEvent.class);

    private final LivingEntity entity;
    private final @Nullable DamageSource damageSource;

    private int lootingLevel;

    public LootingLevelEvent(LivingEntity entity, @Nullable DamageSource damageSource, int lootingLevel) {
        this.entity = entity;
        this.damageSource = damageSource;
        this.lootingLevel = lootingLevel;
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    @Nullable
    public DamageSource getDamageSource() {
        return damageSource;
    }

    public int getLootingLevel() {
        return lootingLevel;
    }

    public void setLootingLevel(int lootingLevel) {
        this.lootingLevel = lootingLevel;
    }
}
