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

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;

/**
 * LivingSetAttackTargetEvent is fired when an Entity sets a target to attack.<br>
 * This event is fired whenever an Entity sets a target to attack in
 * {@link EntityLiving#setAttackTarget(EntityLivingBase)} and
 * {@link EntityLivingBase#setRevengeTarget(EntityLivingBase)}.<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingSetAttackTarget(EntityLivingBase, EntityLivingBase)}.<br>
 * <br>
 * {@link #target} contains the newly targeted Entity.<br>
 * <br>
 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class LivingSetAttackTargetEvent extends LivingEvent
{

    private final LivingEntity target;
    public LivingSetAttackTargetEvent(LivingEntity entity, LivingEntity target)
    {
        super(entity);
        this.target = target;
    }

    public LivingEntity getTarget()
    {
        return target;
    }
}
