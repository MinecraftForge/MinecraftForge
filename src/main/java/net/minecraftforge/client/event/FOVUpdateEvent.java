/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 6:07 PM, 9/5/13
 */
public class FOVUpdateEvent extends Event
{
    private final PlayerEntity entity;
    private final float fov;
    private float newfov;

    public FOVUpdateEvent(PlayerEntity entity, float fov)
    {
        this.entity = entity;
        this.fov = fov;
        this.setNewfov(fov);
    }

    public PlayerEntity getEntity()
    {
        return entity;
    }

    public float getFov()
    {
        return fov;
    }

    public float getNewfov()
    {
        return newfov;
    }

    public void setNewfov(float newfov)
    {
        this.newfov = newfov;
    }
}
