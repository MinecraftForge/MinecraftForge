package net.minecraftforge.event.entity.player;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraftforge.event.Cancelable;

/**
 * Called when an Entity shoots a bow.
 * If the event is cancelled the arrow-entity will be destroyed.
 */
@Cancelable
public class PlayerShootBowEvent extends PlayerEvent
{
    public final ItemStack bow;
    public final Entity projectile;
    public final float force;

    public PlayerShootBowEvent(EntityPlayer entity, ItemStack bow, Entity projectile, float force)
    {
        super(entity);
        this.bow = bow;
        this.projectile = projectile;
        this.force = force;
    }

}
