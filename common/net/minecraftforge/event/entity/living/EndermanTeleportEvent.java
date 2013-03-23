package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

/**
 * Event for when an Enderman teleports.  Can be used to either modify the target position, or cancel the teleport outright.
 * @author Mithion
 *
 */
@Cancelable
public class EndermanTeleportEvent extends LivingEvent{
    
    public double targetX;
    public double targetY;
    public double targetZ;
    
    public EndermanTeleportEvent(EntityLiving entity, double targetX, double targetY, double targetZ)
    {
        super(entity);
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }    
}
