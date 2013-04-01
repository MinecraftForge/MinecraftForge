package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.Cancelable;

/**
 * Fired before the entity is checked for despawning
 * Cancelling will cause entity to not despawn
 */
@Cancelable
public class LivingDespawnEvent extends LivingEvent {

    /**
     * Whether this Entity would usually Despawn
     */
    public final boolean canDespawn;

    public LivingDespawnEvent(EntityLiving entity, boolean canDespawn)
    {
        super(entity);
        this.canDespawn = canDespawn;
    }
}
