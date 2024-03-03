/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraftforge.eventbus.api.Event.HasResult;
import net.minecraft.world.entity.Mob;

@HasResult
public class LivingPackSizeEvent extends LivingEvent
{
    private int maxPackSize;
    
    public LivingPackSizeEvent(Mob entity)
    {
        super(entity);
    }

    /**
     * This event is fired when the spawning system determines the
     * maximum amount of the selected entity that can spawn at the same
     * time.
     *
     * If you set the result to 'ALLOW', it means that you want to return
     * the value of maxPackSize as the maximum pack size for current entity.
     */
    public int getMaxPackSize()
    {
        return maxPackSize;
    }

    public void setMaxPackSize(int maxPackSize)
    {
        this.maxPackSize = maxPackSize;
    }
}
