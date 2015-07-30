package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * LivingProcreateEvent is fired before a baby entity is spawned due to mating. <br>
 * This event is fired whenever an Entity attempts to spawn a baby in <br>
 * EntityAIMate#spawnBaby() <br>
 * <br>
 * This event is fired via {@link ForgeHooks#onEntityMate(EntityAgeable, EntityAnimal, EntityAnimal, int)}.<br>
 * <br>
 * {@link #child} contains the child about to spawn.<br>
 * {@link #parent1} contains one of the parents<br>
 * {@link #parent2} contains another one of the parents<br>
 * {@link #xp} contains the xp dropped by the breeding<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the child does not spawn.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 * @author austinv11
 */
@Cancelable
public class LivingProcreateEvent extends LivingEvent 
{
    
    public EntityAgeable child;
    public final EntityAnimal parent1;
    public final EntityAnimal parent2;
    public int xp;
    public LivingProcreateEvent(EntityAgeable child, EntityAnimal parent1, EntityAnimal parent2, int xp)
    {
        super(child);
        this.child = child;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.xp = xp;
    }
}
