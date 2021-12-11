package net.minecraftforge.event.entity;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;

/**
 * This event is called when an entity finalizes its spawn, usually to give it something (eg, skeletons with bows)<br>
 * This event should be used if a modder wants to add something to an entity the first time it's spawned.<br>
 * If a modder wants to add something everytime an entity is spawned they should use {@link EntityJoinWorldEvent}
 */
public class EntityFinalizeSpawnEvent extends EntityEvent
{
    private final DifficultyInstance difficultyInstance;
    private final MobSpawnType mobSpawnType;
    @Nullable
    private final SpawnGroupData groupData;
    @Nullable
    private final CompoundTag compoundTag;

    public EntityFinalizeSpawnEvent(Mob entity, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, SpawnGroupData data, CompoundTag compoundTag)
    {
        super(entity);
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
    public final CompoundTag getCompoundTag()
    {
        return compoundTag;
    }
}
