package net.minecraftforge.event.entity.living;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.EntityLivingBase;

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
    
    /**
	 * Event posted after a successful ender teleport; not cancelable and changing fields has no effect
	 */
	public static class PostEnderTeleport extends EnderTeleportEvent {
		/**
		 * The entity has successfully teleported and already been set to its new position
		 * Parameters originX, originY, and originZ are the original position of the entity
		 */
		public PostEnderTeleport(EntityLivingBase entity, double originX, double originY, double originZ, float damage) {
			super(entity, originX, originY, originZ, damage);
		}
	}
}
