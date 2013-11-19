package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

/**
 * Event for when an Enderman teleports or an ender pearl is used.  Can be used to either modify the target position, or cancel the teleport outright.
 * @author Mithion
 *
 */
@Cancelable
public class EnderTeleportEvent extends LivingEvent
{

    public double targetX;
    public double targetY;
    public double targetZ;
    public float attackDamage;

    public EnderTeleportEvent(EntityLivingBase entity, double targetX, double targetY, double targetZ, float attackDamage)
    {
        super(entity);
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.attackDamage = attackDamage;
    }
}
