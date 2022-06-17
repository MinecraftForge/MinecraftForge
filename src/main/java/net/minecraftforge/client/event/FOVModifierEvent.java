/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired after the field of vision (FOV) modifier for the player is calculated.
 * This can be used to modify the FOV before the FOV settings are applied.
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. </p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 *
 * @see EntityViewRenderEvent.FieldOfView
 */
public class FOVModifierEvent extends Event
{
    private final Player player;
    private final float fov;
    private float newFov;

    /**
     * @hidden
     * @see ForgeHooksClient#getFieldOfView(Player, float)
     */
    public FOVModifierEvent(Player player, float fov)
    {
        this.player = player;
        this.fov = fov;
        this.setNewFov((float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0F, fov));
    }

    /**
     * {@return the player affected by this event}
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * {@return the original field of vision (FOV) of the player, before any modifications or interpolation}
     */
    public float getFov()
    {
        return fov;
    }

    /**
     * {@return the current field of vision (FOV) of the player}
     */
    public float getNewFov()
    {
        return newFov;
    }

    /**
     * Sets the new field of vision (FOV) of the player.
     *
     * @param newFov the new field of vision (FOV)
     */
    public void setNewFov(float newFov)
    {
        this.newFov = newFov;
    }
}
