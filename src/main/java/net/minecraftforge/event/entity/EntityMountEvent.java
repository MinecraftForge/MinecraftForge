package net.minecraftforge.event.entity;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;

@Cancelable
public class EntityMountEvent extends EntityEvent
{

    public final Entity rider;
    
    public EntityMountEvent(Entity entity, Entity rider)
    {
        super(entity);
        this.rider = rider;
    }
    
    @Cancelable
    public static class Dismount extends EntityMountEvent
    {

        public Dismount(Entity entity, Entity rider)
        {
            super(entity, rider);
        }
        
    }
    
}
