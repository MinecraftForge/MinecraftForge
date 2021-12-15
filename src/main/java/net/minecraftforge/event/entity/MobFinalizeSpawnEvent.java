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

package net.minecraftforge.event.entity;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;

/**
 * This event is called when an entity finalizes its spawn, usually to give it something (eg, skeletons with bows)<br>
 * This event should be used if a modder wants to add something to an entity the first time it's spawned.<br>
 * If a modder wants to add something everytime an entity is added to the world they should use {@link EntityJoinWorldEvent}
 */
public class MobFinalizeSpawnEvent extends EntityEvent
{
    private final Mob mob;
    private final DifficultyInstance difficultyInstance;
    private final MobSpawnType mobSpawnType;
    @Nullable
    private final SpawnGroupData groupData;
    @Nullable
    private final CompoundTag compoundTag;

    public MobFinalizeSpawnEvent(Mob mob, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData data, @Nullable CompoundTag compoundTag)
    {
        super(mob);
        this.mob = mob;
        this.difficultyInstance = difficultyInstance;
        this.mobSpawnType = mobSpawnType;
        this.groupData = data;
        this.compoundTag = compoundTag;
    }

    public MobSpawnType getMobSpawnType()
    {
        return mobSpawnType;
    }

    public DifficultyInstance getDifficultyInstance()
    {
        return difficultyInstance;
    }
    
    @Nullable
    public SpawnGroupData getGroupData() 
    {
        return groupData;
    }

    @Nullable
    public CompoundTag getCompoundTag()
    {
        return compoundTag;
    }
    
    @Override
    public Mob getEntity()
    {
        return mob;
    }
}
