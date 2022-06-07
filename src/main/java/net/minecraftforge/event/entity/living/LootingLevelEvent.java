/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.Nullable;

public class LootingLevelEvent extends LivingEvent {

    @Nullable
    private final DamageSource damageSource;

    private int lootingLevel;

    public LootingLevelEvent(LivingEntity entity, @Nullable DamageSource damageSource, int lootingLevel) {
        super(entity);
        this.damageSource = damageSource;
        this.lootingLevel = lootingLevel;
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
