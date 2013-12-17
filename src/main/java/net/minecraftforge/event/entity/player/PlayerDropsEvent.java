package net.minecraftforge.event.entity.player;

import java.util.ArrayList;

import cpw.mods.fml.common.eventhandler.Cancelable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

/**
 * Child class of LivingDropEvent that is fired specifically when a
 * player dies.  Canceling the event will prevent ALL drops from entering the
 * world.
 */
@Cancelable
public class PlayerDropsEvent extends LivingDropsEvent
{
    public final EntityPlayer entityPlayer;

    /**
     * Creates a new event containing all the items that will drop into the
     * world when a player dies.
     * @param entity The dying player. 
     * @param source The source of the damage which is killing the player.
     * @param drops List of all drops entering the world.
     */
    public PlayerDropsEvent(EntityPlayer entity, DamageSource source, ArrayList<EntityItem> drops, boolean recentlyHit)
    {
        super(entity, source, drops, 
            (source.getEntity() instanceof EntityPlayer) ? 
                EnchantmentHelper.getLootingModifier(((EntityPlayer)source.getEntity())) : 0,
            recentlyHit, 0);
        
        this.entityPlayer = entity;
    }
}