/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
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
public class BabyEntitySpawnEvent extends net.minecraftforge.eventbus.api.Event
{
    private final MobEntity parentA;
    private final MobEntity parentB;
    private final PlayerEntity causedByPlayer;
    private AgeableEntity child;

    public BabyEntitySpawnEvent(MobEntity parentA, MobEntity parentB, @Nullable AgeableEntity proposedChild)
    {
        //causedByPlayer calculated here to simplify the patch.
        PlayerEntity causedByPlayer = null;
        if (parentA instanceof AnimalEntity) {
            causedByPlayer = ((AnimalEntity)parentA).getLoveCause();
        }

        if (causedByPlayer == null && parentB instanceof AnimalEntity)
        {
            causedByPlayer = ((AnimalEntity)parentB).getLoveCause();
        }

        this.parentA = parentA;
        this.parentB = parentB;
        this.causedByPlayer = causedByPlayer;
        this.child = proposedChild;
    }

    public MobEntity getParentA()
    {
        return parentA;
    }

    public MobEntity getParentB()
    {
        return parentB;
    }

    @Nullable
    public PlayerEntity getCausedByPlayer()
    {
        return causedByPlayer;
    }

    @Nullable
    public AgeableEntity getChild()
    {
        return child;
    }

    public void setChild(AgeableEntity proposedChild)
    {
        child = proposedChild;
    }
}
