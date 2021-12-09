package net.minecraftforge.event.entity;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;

/**
 * This event is called when an entity finalizes its spawn, usually to give it something (eg, skeletons with bows)
 */
public class EntityFinalizeSpawnEvent extends EntityEvent
{
    
    private final DifficultyInstance instance;
    private final MobSpawnType mobSpawnType;
    @Nullable
    private final SpawnGroupData groupData;
    @Nullable
    private final CompoundTag tag;

    public EntityFinalizeSpawnEvent(Mob entity, DifficultyInstance instance, MobSpawnType mobSpawnType, SpawnGroupData data, CompoundTag tag)
    {
        super(entity);
        this.instance = instance;
        this.mobSpawnType = mobSpawnType;
        this.groupData = data;
        this.tag = tag;

    }

    public MobSpawnType getMobSpawnType()
    {
        return mobSpawnType;
    }

    public DifficultyInstance getInstance()
    {
        return instance;
    }
    
    @Nullable
    public SpawnGroupData getGroupData() 
    {
        return groupData;
    }

    @Nullable
    public final CompoundTag getTag()
    {
        return tag;
    }
}
