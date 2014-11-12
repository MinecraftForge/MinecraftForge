package net.minecraftforge.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

public class ThornsEvent extends Event
{
    public Entity entity;
    public EntityLivingBase enchanted;
    public int damageDealt;
    
    public ItemStack armor;
    
    /***
     * @param entity The entity that damaged enchanted
     * @param enchanted The entity that was wearing thorns armor when damaged by entity
     * @param armor The enchanted armor that contains the thorns enchantment
     * @param damageDealt The damage dealt to enchanted by entity
     */
    private ThornsEvent(Entity entity, EntityLivingBase enchanted, ItemStack armor, int damageDealt)
    {
        this.entity = entity;
        this.enchanted = enchanted;
        this.armor = armor;
        this.damageDealt = damageDealt;
    }
    
    /***
     * Fired before damaging the entity before damaging the armor.
     * 
     * Cancelable. Will cause the entity no damage, and the armor to stay as is.
     */
    @Cancelable
    public static class ThornsEntityDamageEvent extends ThornsEvent
    {
        public ThornsEntityDamageEvent(Entity entity, EntityLivingBase enchanted, ItemStack armor, int damageDealt)
        {
            super(entity, enchanted, armor, damageDealt);
        }
    }
    
    /***
     * Fired before damaging the armor stack after hurting the mob.
     * 
     * Cancelable. Will cause the armor to stay as is.
     */
    @Cancelable
    public static class ThornsArmorDamageEvent extends ThornsEvent
    {
        public ThornsArmorDamageEvent(Entity entity, EntityLivingBase enchanted, ItemStack armor, int damageDealt)
        {
            super(entity, enchanted, armor, damageDealt);
        }
    }
}
