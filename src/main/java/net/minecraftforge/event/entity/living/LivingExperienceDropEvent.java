/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Event for when an entity drops experience on its death, can be used to change
 * the amount of experience points dropped or completely prevent dropping of experience
 * by canceling the event.
 */
@NullMarked
public final class LivingExperienceDropEvent extends MutableEvent implements Cancellable, LivingEvent {
    public static final CancellableEventBus<LivingExperienceDropEvent> BUS = CancellableEventBus.create(LivingExperienceDropEvent.class);

    private final LivingEntity entity;
    private final @Nullable Player attackingPlayer;
    private final int originalExperiencePoints;

    private int droppedExperiencePoints;

    public LivingExperienceDropEvent(LivingEntity entity, @Nullable Player attackingPlayer, int originalExperience) {
        this.entity = entity;
        this.attackingPlayer = attackingPlayer;
        this.originalExperiencePoints = this.droppedExperiencePoints = originalExperience;
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    public int getDroppedExperience() {
        return droppedExperiencePoints;
    }

    public void setDroppedExperience(int droppedExperience) {
        this.droppedExperiencePoints = droppedExperience;
    }

    /**
     * @return The player that last attacked the entity and thus caused the experience. This can be null, in case the player has since logged out.
     */
    @Nullable
    public Player getAttackingPlayer() {
        return attackingPlayer;
    }

    public int getOriginalExperience() {
        return originalExperiencePoints;
    }
}
