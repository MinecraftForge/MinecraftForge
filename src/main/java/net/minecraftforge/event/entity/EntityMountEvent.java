package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class EntityMountEvent extends EntityEvent{

    public final Entity rider;
    public final Entity entity;
    
    public EntityMountEvent(Entity rider, Entity entity)
    {
        super(entity);
        this.entity = entity;
        this.rider = rider;
    }

}
