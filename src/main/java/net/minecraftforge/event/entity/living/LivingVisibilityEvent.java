/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.event.entity.living;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;

/**
 * LivingVisibilityEvent is fired when a LivingEntity is checked to be visible (e.g. by the AI of another entity). <br>
 * This event is fired whenever the visibility is determined in
 * {@link LivingEntity#getVisibilityMultiplier(Entity)}. <br>
 * <br>
 * This event is fired via the {@link ForgeHooks#getLivingVisibility(LivingEntity, Entity, double)}.<br>
 * <br>
 * {@link #originalVisibility} contains the original visibility value determined by vanilla. <br>
 * {@link #newVisibility} contains the new visibility value that will be used and can be changed. <br>
 * <br>
 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 *<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class LivingVisibilityEvent extends LivingEvent
{
    private final double originalVisibility;
    private double newVisibility;
    private final Entity lookingEntity;
    public LivingVisibilityEvent(LivingEntity entity, @Nullable Entity lookingEntity, double visibility)
    {
        super(entity);
        this.originalVisibility = visibility;
        this.newVisibility = visibility;
        this.lookingEntity = lookingEntity;
    }

    public double getOriginalVisibility() { return this.originalVisibility; }
    public double getVisibility() { return newVisibility; }
    
    public void setVisibility(double visibility)
    {
        this.newVisibility = visibility;
    }
    
    @Nullable public Entity getLookingEntity() { return this.lookingEntity; }
}