/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import javax.annotation.Nullable;

/**
 * BabyEntitySpawnEvent is fired just before a baby entity is about to be spawned. <br>
 * Parents will have disengaged their relationship. {@link @Cancelable} <br>
 * It is possible to change the child completely by using {@link #setChild(EntityAgeable)} <br>
 * This event is fired from {@link EntityAIMate#spawnBaby()} and {@link EntityAIVillagerMate#giveBirth()} <br>
 * <br>
 * {@link #parentA} contains the initiating parent entity.<br>
 * {@link #parentB} contains the secondary parent entity.<br>
 * {@link #causedByPlayer} contains the player responsible for the breading (if applicable).<br>
 * {@link #child} contains the child that will be spawned.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the child Entity is not added to the world, and the parents <br>
 * will no longer attempt to mate.
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class BabyEntitySpawnEvent extends Event
{
    private final EntityLiving parentA;
    private final EntityLiving parentB;
    private final EntityPlayer causedByPlayer;
    private EntityAgeable child;

    public BabyEntitySpawnEvent(EntityLiving parentA, EntityLiving parentB, @Nullable EntityAgeable proposedChild)
    {
        //causedByPlayer calculated here to simplify the patch.
        EntityPlayer causedByPlayer = null;
        if (parentA instanceof EntityAnimal) {
            causedByPlayer = ((EntityAnimal)parentA).getPlayerInLove();
        }

        if (causedByPlayer == null && parentB instanceof EntityAnimal)
        {
            causedByPlayer = ((EntityAnimal)parentB).getPlayerInLove();
        }

        this.parentA = parentA;
        this.parentB = parentB;
        this.causedByPlayer = causedByPlayer;
        this.child = proposedChild;
    }

    public EntityLiving getParentA()
    {
        return parentA;
    }

    public EntityLiving getParentB()
    {
        return parentB;
    }

    @Nullable
    public EntityPlayer getCausedByPlayer()
    {
        return causedByPlayer;
    }

    @Nullable
    public EntityAgeable getChild()
    {
        return child;
    }

    public void setChild(EntityAgeable proposedChild)
    {
        child = proposedChild;
    }
}