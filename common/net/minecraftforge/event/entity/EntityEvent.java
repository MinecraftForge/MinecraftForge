package net.minecraftforge.event.entity;

import net.minecraft.src.Entity;
import net.minecraftforge.event.Event;

public class EntityEvent extends Event
{
    public final Entity entity;
    
    public EntityEvent(Entity entity)
    {
        this.entity = entity;
    }
    
    public static class CanUpdate extends EntityEvent
    {
        public boolean canUpdate = false;
        public CanUpdate(Entity entity)
        {
            super(entity);
        }
    }
}
