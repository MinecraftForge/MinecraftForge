/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Event for when an entity drops experience on its death, can be used to change
 * the amount of experience points dropped or completely prevent dropping of experience
 * by canceling the event.
 */
@Cancelable
public class LivingExperienceDropEvent extends LivingEvent
{
    private final Player attackingPlayer;
    private final int originalExperiencePoints;

    private int droppedExperiencePoints;

    public LivingExperienceDropEvent(LivingEntity entity, Player attackingPlayer, int originalExperience)
    {
        super(entity);

        this.attackingPlayer = attackingPlayer;
        this.originalExperiencePoints = this.droppedExperiencePoints = originalExperience;
    }

    public int getDroppedExperience()
    {
        return droppedExperiencePoints;
    }

    public void setDroppedExperience(int droppedExperience)
    {
        this.droppedExperiencePoints = droppedExperience;
    }

    public Player getAttackingPlayer()
    {
        return attackingPlayer;
    }

    public int getOriginalExperience()
    {
        return originalExperiencePoints;
    }
}
