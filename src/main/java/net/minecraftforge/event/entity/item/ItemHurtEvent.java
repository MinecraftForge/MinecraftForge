package net.minecraftforge.event.entity.item;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event.HasResult;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;

/**
 * ItemHurtEvent is fired when an Entity is set to be hurt. <br>
 * This event is fired whenever an Entity is hurt in 
 * EntityItem#onDeath(DamageSource, float). <br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onItemHurt(EntityItem, DamageSource, float)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused this Entity to be hurt. <br>
 * {@link #amount} contains the amount of damage dealt to the Entity that was hurt. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity is not hurt.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class ItemHurtEvent extends ItemEvent
{
    
    public final DamageSource damageSource;
    public final float amount;
    public ItemHurtEvent(EntityItem entityItem, DamageSource damageSource, float amount)
    {
        super(entityItem);
        this.damageSource = damageSource;
        this.amount = amount;
    }

}
