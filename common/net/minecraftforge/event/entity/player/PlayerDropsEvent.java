package net.minecraftforge.event.entity.player;

import java.util.ArrayList;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EnchantmentHelper;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

/**
 * Copy-pasta child class of LivingDropEvent that is fired specifically when a
 * player dies.  Cancelling the vent will prevent ALL drops from entering the
 * world.
 * 
 * @author Grenadier
 */
@Cancelable
public class PlayerDropsEvent extends LivingDropsEvent
{
    /**
     * The dying player.
     */
    public final EntityPlayer entityPlayer;
    
    /**
     * Creates a new event containing all the items that will drop into the
     * world when a player dies.
     * @param entity The dying player. 
     * @param source The source of the damage which is killing the player.
     * @param drops List of all drops entering the world.
     */
    public PlayerDropsEvent(EntityPlayer entity, DamageSource source, ArrayList<EntityItem> drops)
    {
        /*
         * Recently hit should always be considered true for a player death.
         * Special drop calculations for killing other players is meaningless in this context.
         * 
         * Java is a piece of shit and requires super() to be the first expression.
         * Couple this with someone's idea to make all the variables final, and
         * you get this clusterfuck of a one-liner.
         */
        super(entity, source, drops, 
                (source.getEntity() instanceof EntityPlayer) ? EnchantmentHelper.getLootingModifier(((EntityPlayer)source.getEntity()).inventory) : 0,
                true, 0);
        
        this.entityPlayer = entity;
    }
    
}