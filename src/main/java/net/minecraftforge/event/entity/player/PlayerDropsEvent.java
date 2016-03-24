package net.minecraftforge.event.entity.player;

import java.util.List;

import net.minecraftforge.fml.common.eventhandler.Cancelable;

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
    private final EntityPlayer entityPlayer;

    /**
     * Creates a new event containing all the items that will drop into the
     * world when a player dies.
     * @param entity The dying player.
     * @param source The source of the damage which is killing the player.
     * @param drops List of all drops entering the world.
     */
    public PlayerDropsEvent(EntityPlayer entity, DamageSource source, List<EntityItem> drops, boolean recentlyHit)
    {
        super(entity, source, drops,
            (source.getEntity() instanceof EntityPlayer) ?
                EnchantmentHelper.getLootingModifier(((EntityPlayer)source.getEntity())) : 0,
            recentlyHit);

        this.entityPlayer = entity;
    }

    public EntityPlayer getEntityPlayer()
    {
        return entityPlayer;
    }
}