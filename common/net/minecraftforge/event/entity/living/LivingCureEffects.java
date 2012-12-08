package net.minecraftforge.event.entity.living;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.ItemStack;
import net.minecraft.src.PotionEffect;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingCureEffects extends LivingEvent
{
    public final EntityLiving entityLiving;
    public final ItemStack curativeItem;
    public final PotionEffect potionEffect;
    
    /**
     * Cancel the cancellation of a PotionEffect often caused by drinking milk.
     * @param entity The entity being cured.
     * @param curativeItem The item used to cure the PotionEffect.
     * @param potionEffect The PotionEffect being cured.
     */
    public LivingCureEffects(EntityLiving entity, ItemStack curativeItem, PotionEffect potionEffect)
    {
        super(entity);
        this.entityLiving = entity;
        this.curativeItem = curativeItem;
        this.potionEffect = potionEffect;
    }
}
