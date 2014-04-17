package net.minecraftforge.event.entity;

import net.minecraft.entity.item.EntityBoat;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class BoatCrashEvent extends EntityEvent
{
    public EntityBoat entityBoat;
    public double power;
    
    public BoatCrashEvent(EntityBoat entityBoat, double power)
    {
        super(entityBoat);
        this.entityBoat = entityBoat;
        this.power = power;
    }

}
