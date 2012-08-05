package net.minecraftforge.event.entity;

import net.minecraft.src.Entity;
import net.minecraftforge.event.Event;

public class EntityEvent extends Event
{
    private final Entity entity;
    
    public EntityEvent(Entity entity)
    {
        this.entity = entity;
    }
    
    public Entity getEntity()
    {
        return entity;
    }
}
