package net.minecraftforge.event.entity.item;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event.HasResult;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;

/**
 * LivingDeathEvent is fired when an Entity dies. <br>
 * This event is fired whenever an Entity dies in 
 * EntityItem#attackEntityFrom(DamageSource, float). <br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onItemDeath(EntityItem, DamageSource)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused the entity to die. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity does not die.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class ItemDeathEvent extends ItemEvent
{

    public final DamageSource damageSource;
    
    public ItemDeathEvent(EntityItem entityItem, DamageSource damageSource)
    {
        super(entityItem);        
        this.damageSource = damageSource;
    }

}
