package net.minecraftforge.event.entity.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * AttackEntityEvent is fired when a player is about to make a critical hit.<br>
 * This event is fired whenever a player attacks an Entity in
 * EntityPlayer#attackTargetEntityWithCurrentItem(Entity).<br>
 * <br>
 * {@link #livingTarget} contains the Entity that was damaged by the player. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the player does not make a critical hit (but he still will attack!).<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class CriticalHitEvent extends AttackEntityEvent
{
    public final EntityLivingBase livingTarget;
    /**
     * This is how much damage you will deal. By default you deal 150% of you normal damage.
     * If you set it to 0, then the particles are still generated but you don't do damage.
    */
    public float damageModifier;
    public CriticalHitEvent(EntityPlayer player, EntityLivingBase target)
    {
        super(player, target);
        this.livingTarget = target;
        this.damageModifier = 1.5F;
    }
}
