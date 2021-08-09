/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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

import static net.minecraftforge.client.event.render.EntityViewRenderEvent.*;

/**
 * Fired after the field of vision (FOV) modifier for the player is calculated.
 * This can be used to modify the FOV before the FOV settings are applied.
 *
 * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 *
 * @see ForgeHooksClient#getFOVModifier(Player, float)
 * @see FieldOfView
 * @author MachineMuse (Claire Semple)
 */
public class FOVModifierEvent extends Event
{
    private final Player entity;
    private final float fov;
    private float newFOV;

    public FOVModifierEvent(Player entity, float fov)
    {
        this.entity = entity;
        this.fov = fov;
        this.setNewFOV(Mth.lerp(Minecraft.getInstance().options.fovEffectScale, 1.0F, fov));
    }

    /**
     * {@return the player affected by this event}
     */
    public Player getPlayer()
    {
        return entity;
    }

    /**
     * {@return the original field of vision (FOV) of the player, before any modifications or interpolation}
     */
    public float getFOV()
    {
        return fov;
    }

    /**
     * {@return the current field of vision (FOV) of the player}
     */
    public float getNewFOV()
    {
        return newFOV;
    }

    /**
     * Sets the new field of vision (FOV) of the player.
     *
     * @param newFOV the new field of vision (FOV)
     */
    public void setNewFOV(float newFOV)
    {
        this.newFOV = newFOV;
    }
}
