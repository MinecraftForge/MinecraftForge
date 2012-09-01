package net.minecraftforge.event.entity.living;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.ItemStack;
import net.minecraftforge.event.Cancelable;

/**
 * Called when an Entity shoots a bow.
 * If the event is cancelled the arrow-entity will be destroyed.
 */
@Cancelable
public class LivingShootBowEvent extends LivingEvent
{
    public final EntityLiving shooter;
    public final ItemStack bow;
    public final Entity projectile;
    public final float force;

    public LivingShootBowEvent(EntityLiving entity, ItemStack bow, Entity projectile, float force)
    {
        super(entity);
        this.shooter = entity;
        this.bow = bow;
        this.projectile = projectile;
        this.force = force;
    }

}
