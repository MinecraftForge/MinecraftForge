package net.minecraftforge.event.entity.living;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired when an enchantment level is being looked up for an entity. <br>
 * This event is fired in
 * {@link EnchantmentHelper#getEnchantmentLevel(Enchantment, ItemStack, EntityLivingBase)} and. <br>
 * <br>
 * This event is fired via the {@link ForgeHooks#getEnchantmentLevel(EntityLivingBase, Enchantment, ItemStack, int)}.<br>
 * <br>
 * {@link #enchantment} contains the Enchantment that is being looked up. <br>
 * {@link #heldItem} contains the held item when the level is being looked up. <br>
 * {@link #level} contains the enchantment level, which is initialized by Vanilla's default logic. <br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event has a result {@link #level}. {@link HasResult}<br>
 *<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 * @author rubensworks
 */
@Event.HasResult
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
