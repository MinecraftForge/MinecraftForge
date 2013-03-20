package net.minecraftforge.event.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class EntityCountEvent extends Event
{    
    /**
     * This event is fired when the spawning system counts the entities
     * of a type in the world.
     * 
     * If you set the result to 'ALLOW', it means that you want to return
     * the value of spawnCount as the number of Counted Entities
     */
    public final List loadedEntityList;
    public final EnumCreatureType creatureType;
    public final boolean forSpawnCount;
    
    public int spawnCount;
    
    public EntityCountEvent(List loadedEntityList, EnumCreatureType creatureType, boolean forSpawnCount)
    {
        this.loadedEntityList = loadedEntityList;
        this.creatureType = creatureType;
        this.forSpawnCount = forSpawnCount;
    }
    
}
