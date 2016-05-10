package net.minecraftforge.event.entity.living;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * This event is fired when an enchantment level is being looked up for an entity.
 * @author rubensworks
 **/
public class LivingEnchantmentLevelEvent extends LivingEvent
{

    private final Enchantment enchantment;
    private final ItemStack heldItem;
    private int level;

    public LivingEnchantmentLevelEvent(EntityLivingBase entityLiving, Enchantment enchantment, ItemStack heldItem, int level)
    {
        super(entityLiving);
        this.enchantment = enchantment;
        this.heldItem = heldItem;
        this.level = level;
    }

    public Enchantment getEnchantment()
    {
        return enchantment;
    }
    
    public ItemStack getHeldItem()
    {
        return heldItem;
    }
    
    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }
}
