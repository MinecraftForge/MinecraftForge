/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingDrownEvent is fired whenever a living entity can't breathe and its air aupply is less than or equal to
 * zero.<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingBreathe(LivingEntity, int, int)}.<br>
 * <br>
 * {@link #isDrowning()} contains if this entity is drowning.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, this entity won't take drowning damage, no particles will be spawned and the air supply
 * won't be reset to zero ignoring if {@link #isDrowning()} is true or not.<br>
 * <br>
 * This event does not have a result. {@link HasResult}
 **/
@Cancelable
public class LivingDrownEvent extends LivingEvent
{
    private boolean isDrowning;

    public LivingDrownEvent(LivingEntity entity, boolean isDrowning)
    {
        super(entity);
        this.isDrowning = isDrowning;
    }

    public boolean isDrowning()
    {
        return isDrowning;
    }

    public void setDrowning(boolean isDrowning)
    {
        this.isDrowning = isDrowning;
    }
}