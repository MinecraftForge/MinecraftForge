/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraftforge.eventbus.api.Event;

public class FOVModifierEvent extends Event
{
    private final Player entity;
    private final float fov;
    private float newfov;

    public FOVModifierEvent(Player entity, float fov)
    {
        this.entity = entity;
        this.fov = fov;
        this.setNewfov(Mth.lerp(Minecraft.getInstance().options.fovEffectScale, 1.0F, fov));
    }

    public Player getEntity()
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
